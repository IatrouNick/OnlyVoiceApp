package com.example.acg.onlyvoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostsDetails extends AppCompatActivity implements CommentsAdapter.OnItemClickListener {

    private List<Comments> mCommentsList = new ArrayList<>();
    private List<Posts> mPostsList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;

    private TextView postDetailsAuthorFullName;
    private TextView postDetailsBody;
    private ImageButton voiceCommentBtn;
    private EditText commentDetailsBody;
    private ImageButton postCommentBtn;
    private RecyclerView commentsRecyclerView;
    private TextView postKeyDetailsBody;
    public String userKey;
    public String userName;

    private DatabaseReference commentsRef;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_details);

        String postKey = getIntent().getStringExtra("postKey");
        postDetailsAuthorFullName = findViewById(R.id.postDetailsAuthorFullName);
        postDetailsBody = findViewById(R.id.postDetailsBody);
        voiceCommentBtn = findViewById(R.id.voiceCommentBtn);
        commentDetailsBody = findViewById(R.id.commentDetailsBody);
        postCommentBtn = findViewById(R.id.postCommentBtn);
        postKeyDetailsBody = findViewById(R.id.postKeyDetailsBody);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        postKeyDetailsBody.setText(postKey);

        commentsRef = FirebaseDatabase.getInstance().getReference("Comments");

        Query commentsQuery = commentsRef.orderByChild("postKey").equalTo(postKey);
        commentsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterate through the comments and add them to a list
                List<Comments> commentsList = new ArrayList<>();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comments comments = commentSnapshot.getValue(Comments.class);
                    commentsList.add(comments);
                }

                System.out.println("comments are " + commentsList);
                commentsRecyclerView.setLayoutManager(new LinearLayoutManager(PostsDetails.this));
                commentsAdapter = new CommentsAdapter(PostsDetails.this, commentsList);
                commentsRecyclerView.setAdapter(commentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });



  //      commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
  //      commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  //      commentsAdapter = new CommentsAdapter(this, mCommentsList);
  //
  //      commentsAdapter.setOnItemClickListener(this);
  //
  //      commentsRecyclerView.setAdapter(commentsAdapter);



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Posts posts = dataSnapshot.getValue(Posts.class);
                postDetailsAuthorFullName.setText(posts.getUserName());
                postDetailsBody.setText(posts.getPostBody());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                userKey = users.getUserKey();
                userName = users.getFirstName() + " " + users.getLastName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });


        voiceCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording(view);
            }
        });



        //create post

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String commentBody = commentDetailsBody.getText().toString();
                long timestamp = System.currentTimeMillis();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments");
                //generate a unique key for each post
                String commentKey = databaseReference.push().getKey();
                // create new post object
                Comments comments = new Comments(userKey, postKey, commentKey, userName, commentBody, timestamp);

                databaseReference.child(String.valueOf(commentKey)).setValue(comments).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(PostsDetails.this, "Comment Successful", Toast.LENGTH_SHORT).show();
                        commentDetailsBody.setText("");
                    }
                });
            }
        });

    }

    @Override
    public void onItemClick(DatabaseReference posts) {

    }

    //speech to text functionality
    public void startRecording(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (commentDetailsBody.getText() == null) {
                commentDetailsBody.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            } else {
                String message = commentDetailsBody.getText() + " ";
                commentDetailsBody.setText(message + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            }

        }
    }
}