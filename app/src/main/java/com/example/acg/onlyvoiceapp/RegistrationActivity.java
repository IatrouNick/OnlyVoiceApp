package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailReg;
    private EditText passReg;
    private Button regBtn;
    private Button loginBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailReg = findViewById(R.id.emailReg);
        passReg  = findViewById(R.id.passReg);
        regBtn   = findViewById(R.id.regBtn);
        loginBtn = findViewById(R.id.loginBtn);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(view -> {
            createUser();

        });

        loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
        });

    }

    private void createUser() {

        String email = emailReg.getText().toString();
        String password = passReg.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailReg.setError("Email cannot be empty");
            emailReg.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            passReg.setError("Password cannot be empty");
            passReg.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task){
                    if (task.isSuccessful()){
                        mAuth.getCurrentUser().sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "An email has been send to verify your account.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Registration failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                  //      @Override
                  //              public void onComplete(@NonNull Task<Void> task) {
                  //                  if (task.isSuccessful()) {
                  //                      Toast.makeText(RegistrationActivity.this, "You have been registered Succesfully", Toast.LENGTH_SHORT).show();
                  //                      startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                  //                  }else{
                  //                      Toast.makeText(RegistrationActivity.this,"Registration failed" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                  //                  }
                  //              }
                  //          });


                    }else{
                        Toast.makeText(RegistrationActivity.this,"Registration failed" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }) ;
        }
    }
}