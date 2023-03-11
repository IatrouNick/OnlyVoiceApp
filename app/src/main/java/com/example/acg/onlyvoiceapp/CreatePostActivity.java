package com.example.acg.onlyvoiceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreatePostActivity extends AppCompatActivity {

    private EditText postBody;
    private Button postBtn;
    private FirebaseAuth mAuth;
    private String fullName;
    private String postTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postBtn = findViewById(R.id.postBtn);
        postBody = findViewById(R.id.postBody);


        //get info from my db for the user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    postTitle = firstName + ' ' + lastName;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postBody != null) {

                    String postBodyPart = postBody.getText().toString();
                    long timestamp = System.currentTimeMillis();



                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
                    //generate a unique key for each post
                    String postKey = databaseReference.push().getKey();
                    // create new post object
                    Posts posts = new Posts(userId, postKey, postTitle, postBodyPart, timestamp);

                    databaseReference.child(String.valueOf(postKey)).setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CreatePostActivity.this, "Creation Successful", Toast.LENGTH_SHORT).show();
                        }
                    });


                    finish();
                }
            }

        });
    }
}