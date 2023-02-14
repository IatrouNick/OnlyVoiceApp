package com.example.acg.onlyvoiceapp;
/*
This activity will always be the first activity a new user is seeing to make create their profile
It will store email/name/surname and will have an aut increment for the id
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private long id = 0;

    private EditText firstName;
    private EditText lastName;
    private Button confirmProfile;

    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        confirmProfile = findViewById(R.id.confirmProfile);

        imageView = findViewById(R.id.imageProfile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String email = firebaseUser.getEmail();

        confirmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //get values for user
                String firstName1 = firstName.getText().toString();
                String lastName1 = lastName.getText().toString();
                Users users = new Users(email, firstName1, lastName1);


                //get db reference
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                //get count of users to increment the ID
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            id = (snapshot.getChildrenCount());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.child(String.valueOf(id + 1)).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firstName.setText("");
                        lastName.setText("");
                        Toast.makeText(ProfileActivity.this, "Maybe", Toast.LENGTH_SHORT).show();
                        System.out.println(id);


                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);

                    }
                });
            }
        });
    }

    //TODO or not  implement an image management system
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                // Get the Uri of the selected image
                Uri imageUri = data.getData();

                // Display the selected image in the ImageView
                imageView.setImageURI(imageUri);
            }
        }

}