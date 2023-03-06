package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.content.ContentValues.TAG;

public class WallActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener {

    public ActivityWallBinding binding;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private List<Users> mUserList = new ArrayList<>();

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
        mRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter(this, mUserList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

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
                mAdapter.notifyDataSetChanged();

                return false;
            }


        });

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

        mAdapter.notifyDataSetChanged();
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
                    mAdapter.notifyItemInserted(mUserList.size() - 1);

                    //check if there are no users with that name do remove the recycle view
                    if (mUserList.size() == 0) {
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mRecyclerView.setVisibility(View.INVISIBLE);

            }

        });

        if (mUserList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
       }

    }

    public void onItemClick(Users users) {
       // Toast.makeText(WallActivity.this, "Button Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WallActivity.this, ShowProfile.class);
        intent.putExtra("userEmail", users.getEmail()); // pass the user ID to the next activity
        startActivity(intent);
    }

}