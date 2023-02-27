package com.example.acg.onlyvoiceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

public class ShowProfile extends AppCompatActivity {

    private TextView showEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        //get email from previous activity to show correct profile

        String email = getIntent().getStringExtra("userEmail");
        TextView showEmail = findViewById(R.id.showEmail);
        showEmail.setText(email);

    }
}