package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private EditText firstName;
    private EditText lastName;
    private Button confirmProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.firstName);
        lastName  = findViewById(R.id.lastName);
        confirmProfile   = findViewById(R.id.confirmProfile);

        confirmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get value
                String firstName1 = firstName.getText().toString();
                String lastName1 = lastName.getText().toString();
                System.out.println("test1");
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Users");
                //TODO CHECK WITH OTHER EMAIL AND ADD THE EMAIL TO THE CHILD
                databaseReference.child(firstName1).setValue(firstName1 + " " + lastName1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firstName.setText("");
                        lastName.setText("");
                        Toast.makeText(ProfileActivity.this, "Maybe", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}