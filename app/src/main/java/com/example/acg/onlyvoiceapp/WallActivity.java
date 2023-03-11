package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        checkUser();


        //simple logout button
        binding.logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            checkUser();
            startActivity(new Intent(WallActivity.this, MainActivity.class));
        });

        binding.createPostBtn.setOnClickListener(view -> {
        //    Intent intent = new Intent(WallActivity.this, CreatePostActivity.class);
        //    intent.putExtra("userId", mAuth.getCurrentUser()); // pass the user ID to the next activity
        //    startActivity(intent);
            startActivity(new Intent(WallActivity.this, CreatePostActivity.class));
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

        //make the sizee of the recycler view to be limited to the screen height so i can also have my button in the bottom
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
        } else {
            //get user info
            String email = firebaseUser.getEmail();
            binding.userId.setText(email);
        }


    }

    private void performSearch(String query) {

        //clear list if i change something in the search
        mUserList.clear();

        mAdapterUsers.notifyDataSetChanged();
        // Query the database and add the results to the list
        // Here's an example using the orderByChild and equalTo methods:
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query queryRef = usersRef.orderByChild("lastName").equalTo(query);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    mUserList.add(users);
                    mAdapterUsers.notifyItemInserted(mUserList.size() - 1);

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

    public void onItemClick(Users users) {
       // Toast.makeText(WallActivity.this, "Button Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WallActivity.this, ShowProfile.class);
        intent.putExtra("userEmail", users.getEmail()); // pass the user ID to the next activity
        startActivity(intent);
    }

    public void onItemClick(Posts posts) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    }
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
        System.out.println("aabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaa");
    }
}

