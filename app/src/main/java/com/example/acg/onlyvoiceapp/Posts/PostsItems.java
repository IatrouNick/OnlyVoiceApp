package com.example.acg.onlyvoiceapp.Posts;

import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acg.onlyvoiceapp.R;
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

public class PostsItems extends RecyclerView.ViewHolder {


    //postAuthorFirstName
    //postAuthorLastName
    //postBody
    //postComment

    public TextView postAuthorFullName;
    public TextView postBody;
    public ImageButton postComment;
    public TextView postLike;
    public ImageButton postLikesImage;

    private FirebaseAuth mAuth;
    public ImageButton postDelete;
    public RecyclerView commentsRecyclerView;


    public PostsItems(View view) {
        super(view);


        postAuthorFullName = itemView.findViewById(R.id.postAuthorFullName);
        postBody = itemView.findViewById(R.id.postBody);
        postComment = itemView.findViewById(R.id.postComment);
        postLike = itemView.findViewById(R.id.postLike);
        postLikesImage = itemView.findViewById(R.id.postLikesImage);
        postDelete = itemView.findViewById(R.id.postDelete);
        commentsRecyclerView = itemView.findViewById(R.id.commentsRecyclerView);


        //Use this for onClick functionalities for posts
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;
                String userId = firebaseUser.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
                Query queryRef = databaseReference.orderByChild("userKey").equalTo(userId);
                queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            String postKey = postSnapshot.getKey();
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