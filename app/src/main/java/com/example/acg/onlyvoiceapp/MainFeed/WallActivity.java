package com.example.acg.onlyvoiceapp.MainFeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.acg.onlyvoiceapp.Classes.Posts;
import com.example.acg.onlyvoiceapp.Classes.Users;
import com.example.acg.onlyvoiceapp.Messages.ChatActivity;
import com.example.acg.onlyvoiceapp.Posts.PostCreateActivity;
import com.example.acg.onlyvoiceapp.Posts.PostsAdapter;
import com.example.acg.onlyvoiceapp.R;
import com.example.acg.onlyvoiceapp.Registration.MainActivity;
import com.example.acg.onlyvoiceapp.Registration.ShowProfile;
import com.example.acg.onlyvoiceapp.Search.SearchAdapter;
import com.example.acg.onlyvoiceapp.databinding.ActivityWallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener , PostsAdapter.OnItemClickListener {

    public ActivityWallBinding binding;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerViewUsers;
    private SearchAdapter mAdapterUsers;
    private List<Users> mUserList = new ArrayList<>();
    private RecyclerView mRecyclerViewPosts;

    private PostsAdapter mAdapterPosts;
    private List<Posts> mPostsList = new ArrayList<>();
    private DatabaseReference postsRef;
    private TextView mainFeedTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainFeedTitle = findViewById(R.id.mainFeedTitle);
        mainFeedTitle.setText("Main Feed");
        getSupportActionBar().setTitle(mainFeedTitle.getText());
        mAuth = FirebaseAuth.getInstance();
        checkUser();

        //go to chat activity
        binding.chatActivityBtn.setOnClickListener(view -> {
            startActivity(new Intent(WallActivity.this, ChatActivity.class));

        });


        //simple logout button
        binding.logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            checkUser();
            finish();
            //startActivity(new Intent(WallActivity.this, MainActivity.class));
        });

        binding.createPostBtn.setOnClickListener(view -> {
        //    Intent intent = new Intent(WallActivity.this, CreatePostActivity.class);
        //    intent.putExtra("userId", mAuth.getCurrentUser()); // pass the user ID to the next activity
        //    startActivity(intent);
            startActivity(new Intent(WallActivity.this, PostCreateActivity.class));
        });




        //search users based on written text in the edittext field
        SearchView searchView = findViewById(R.id.searchView);

        mRecyclerViewUsers = findViewById(R.id.searchResultsRecyclerView);
        mRecyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        mAdapterUsers = new SearchAdapter(this, mUserList);
        mRecyclerViewUsers.setAdapter(mAdapterUsers);

        mAdapterUsers.setOnItemClickListener(this);
        onItemClick(postsRef);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                performSearch(query);
                searchView.clearFocus();
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mUserList.clear();
                mAdapterUsers.notifyDataSetChanged();

                return false;
            }


        });

//show posts here

        postsRef = FirebaseDatabase.getInstance().getReference("Posts");
        mRecyclerViewPosts = findViewById(R.id.postsResultsRecyclerView);
        mRecyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        mAdapterPosts = new PostsAdapter(this, mPostsList);


        mAdapterPosts.setOnItemClickListener(this);

        mRecyclerViewPosts.setAdapter(mAdapterPosts);

        //make the size of the recycler view to be limited to the screen height so i can also have my button in the bottom
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = (displayMetrics.heightPixels*75)/100;
        mRecyclerViewPosts.getLayoutParams().height = screenHeight;


        loadPosts();



    }


    private void checkUser() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            //user is not logged in
            startActivity(new Intent(WallActivity.this, MainActivity.class));
            //finish();
        } else {
            //get user info
            String email = firebaseUser.getEmail();
            binding.userId.setText(email);
        }


    }

    //Perfrorm the sesarch functionality for users
    private void performSearch(String query) {

        //clear list if i change something in the search
        mUserList.clear();

        mAdapterUsers.notifyDataSetChanged();
        // Query the database and add the results to the list
        // Here's an example using the orderByChild and equalTo methods:
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
       // Query queryRef = usersRef.orderByChild("lastName").equalTo(query);
        Query queryRef = usersRef.orderByChild("lastName");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);

                    //search with lastname
                   if (users.getLastName().toLowerCase().contains(query.toLowerCase())) {
                        mUserList.add(users);
                        mAdapterUsers.notifyItemInserted(mUserList.size() - 1);
                    }
                   //else search with firstname
                   else if (users.getFirstName().toLowerCase().contains(query.toLowerCase())) {
                       mUserList.add(users);
                       mAdapterUsers.notifyItemInserted(mUserList.size() - 1);
                   }
                    //check if there are no users with that name do remove the recycle view
                    if (mUserList.size() == 0) {
                        mRecyclerViewUsers.setVisibility(View.GONE);
                    } else {
                        mRecyclerViewUsers.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mRecyclerViewUsers.setVisibility(View.INVISIBLE);

            }

        });

        if (mUserList.size() == 0) {
            mRecyclerViewUsers.setVisibility(View.GONE);
        } else {
            mRecyclerViewUsers.setVisibility(View.VISIBLE);
       }

    }

    //On user click go to their profile
    public void onItemClick(Users users) {
       // Toast.makeText(WallActivity.this, "Button Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WallActivity.this, ShowProfile.class);
        intent.putExtra("userKey", users.getUserKey()); // pass the user ID to the next activity
        startActivity(intent);
    }

    //Load posts from DB
    private void loadPosts() {
        // Attach a listener for Firebase Realtime Database changes
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Posts> posts = new ArrayList<>();
                // Loop through all child nodes under "posts" node
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Convert each child node to a Post object
                    Posts postsLoad = postSnapshot.getValue(Posts.class);
                    if (postsLoad != null) {
                        // Add Post object to list
                        posts.add(postsLoad);
                    }
                }
                // Update PostAdapter with new list of posts
                mAdapterPosts.setPosts(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    @Override
    public void onItemClick(DatabaseReference posts) {
    }
}

