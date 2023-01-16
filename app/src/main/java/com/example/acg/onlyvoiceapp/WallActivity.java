package com.example.acg.onlyvoiceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.acg.onlyvoiceapp.databinding.ActivityWallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WallActivity extends AppCompatActivity {

    public ActivityWallBinding binding;

    //Button logoutBtn;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_wall);

        //find by id
        //    logoutBtn =findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            checkUser();
            startActivity(new Intent(WallActivity.this, MainActivity.class));
        });
    }

    private void checkUser(){

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser==null){
            //user is not logged in
            startActivity(new Intent(WallActivity.this, MainActivity.class));
        }else{
            //get user info
            String email = firebaseUser.getEmail();
            binding.userId.setText(email);
        }


    }

}