package com.example.acg.onlyvoiceapp;
/*
This activity will always be the first activity a new user is seeing to make create their profile
It will store email/name/surname and will have an aut increment for the id
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileCreationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private long id = 0;
    private EditText firstName;
    private EditText lastName;
    private Button confirmProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        confirmProfile = findViewById(R.id.confirmProfile);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String email = firebaseUser.getEmail();

        confirmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //get values for user
                String firstName1 = firstName.getText().toString();
                String lastName1 = lastName.getText().toString();

                //get db reference
                //FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                //String key = databaseReference.push().getKey();
                String key= mAuth.getUid();
                Users users = new Users(email, firstName1, lastName1, key);

                databaseReference.child(String.valueOf(key)).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firstName.setText("");
                        lastName.setText("");
                        Toast.makeText(ProfileCreationActivity.this, "Creation Successful", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}