package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UsersSearchItems extends RecyclerView.ViewHolder {

    public TextView searchFirstName;
    public TextView searchLastName;


    public UsersSearchItems(@NonNull View itemView) {
        super(itemView);

        searchFirstName = itemView.findViewById(R.id.searchFirstName);
        searchLastName = itemView.findViewById(R.id.searchLastName);
    }
}