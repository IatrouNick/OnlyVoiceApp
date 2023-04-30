package com.example.acg.onlyvoiceapp.Comments;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.acg.onlyvoiceapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CommentsItems extends RecyclerView.ViewHolder {


    //postAuthorFirstName
    //postAuthorLastName
    //postBody
    //postComment

    public TextView commentAuthorFullName;
    public TextView commentBody;

    private FirebaseAuth mAuth;
    public ImageButton commentDelete;


    public CommentsItems(View view) {
        super(view);


        commentAuthorFullName = itemView.findViewById(R.id.commentAuthorFullName);
        commentBody = itemView.findViewById(R.id.commentBody);
        commentDelete = itemView.findViewById(R.id.commentDelete);


        //Use this for onClick functionalities for comments
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;
                String userId = firebaseUser.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments");
                Query queryRef = databaseReference.orderByChild("userKey").equalTo(userId);
                queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String commentKey = snapshot.getKey();
                            String key = databaseReference.push().getKey();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // handle error
                    }
                });
            }
        });
    }
}