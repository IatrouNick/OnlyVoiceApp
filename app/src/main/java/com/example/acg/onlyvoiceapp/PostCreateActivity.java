package com.example.acg.onlyvoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class PostCreateActivity extends AppCompatActivity {

    private EditText postBody;
    private Button postBtn;
    private FirebaseAuth mAuth;
    private String fullName;
    private String postTitle;
    private ImageButton speakBtn;
    private ImageButton clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postBtn = findViewById(R.id.postBtn);
        speakBtn = findViewById(R.id.speakBtn);
        clearBtn = findViewById(R.id.clearBtn);
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


        //clear button
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postBody.setText(null);
            }
        });


        //POST BUTTON
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
                            Toast.makeText(PostCreateActivity.this, "Creation Successful", Toast.LENGTH_SHORT).show();
                        }
                    });


                    finish();
                }
            }

        });
    }

    public void startRecording(View view){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start");
            startActivityForResult(intent,100);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK){
            if (postBody.getText()==null) {
                postBody.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            }
            else{
                String message = postBody.getText() +" ";
                if (message.equals(" ")){
                    postBody.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                }else {
                    postBody.setText(message + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                }
            }

        }

    }
}