package com.example.acg.onlyvoiceapp.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import com.example.acg.onlyvoiceapp.Classes.Posts;
import com.example.acg.onlyvoiceapp.Classes.Users;
import com.example.acg.onlyvoiceapp.Posts.PostsAdapter;
import com.example.acg.onlyvoiceapp.R;
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

public class ShowProfile extends AppCompatActivity implements PostsAdapter.OnItemClickListener{

    private TextView userKey;
    private RecyclerView mRecyclerViewPosts;
    private PostsAdapter mAdapterPosts;
    private List<Posts> mPostsList = new ArrayList<>();
    private DatabaseReference postsRef;
    private Button postDelete;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        //get email from previous activity to show correct profile

        String userKey = getIntent().getStringExtra("userKey");
        TextView showUsersName = findViewById(R.id.showUsersName);
        postDelete = findViewById(R.id.postDelete);

        //check if the profile is from the current user to enable delete functionality
        //get info from my db for the user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userKey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });





        //Get user's profile name
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query queryRef = usersRef.orderByChild("userKey").equalTo(userKey);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    showUsersName.setText(users.getFirstName() + " " + users.getLastName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //show posts of user
        postsRef = FirebaseDatabase.getInstance().getReference("Posts");
        mRecyclerViewPosts = findViewById(R.id.postsResultsRecyclerView);
        mRecyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        mAdapterPosts = new PostsAdapter(this, mPostsList);


        mAdapterPosts.setOnItemClickListener(this);

        mRecyclerViewPosts.setAdapter(mAdapterPosts);

        //make the sizee of the recycler view to be limited to the screen height so i can also have my button in the bottom
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = (displayMetrics.heightPixels*65)/100;
        mRecyclerViewPosts.getLayoutParams().height = screenHeight;


        loadPosts(userKey);

    }

    private void loadPosts(String userKey) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Posts");
        Query postsRef = usersRef.orderByChild("userKey").equalTo(userKey);
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