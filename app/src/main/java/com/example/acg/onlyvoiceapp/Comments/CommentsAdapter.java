package com.example.acg.onlyvoiceapp.Comments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acg.onlyvoiceapp.Classes.Comments;
import com.example.acg.onlyvoiceapp.Classes.Posts;
import com.example.acg.onlyvoiceapp.Classes.Users;
import com.example.acg.onlyvoiceapp.Posts.PostsDetails;
import com.example.acg.onlyvoiceapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsItems> {

    private Context mContext;
    private List<Comments> mCommentsList;
    private List<Posts> mPostList;
    private CommentsAdapter.OnItemClickListener onItemClickListener;
    private CommentsAdapter.OnDoubleClickListener onDoubleClickListener;
    private FirebaseAuth mAuth;
    public boolean found = false;

    public CommentsAdapter(Context context, List<Comments> commentsList) {
        mContext = context;
        mCommentsList = commentsList;

    }

    public void setOnItemClickListener(PostsDetails postsAdapter) {
    }

    public interface OnItemClickListener {
        void onItemClick(DatabaseReference posts);
    }

    public interface OnDoubleClickListener extends CommentsAdapter.OnItemClickListener {
        void onDoubleClick(Comments comments);
    }


    @NonNull
    @Override
    public CommentsItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_item, parent, false);
        return new CommentsItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsItems holder, int position) {
        // userKey;
        // commentKey;
        // userName;
        // commendBody;
        Comments comments= mCommentsList.get(position);
        String commentsKey = comments.getCommentKey();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userKey = firebaseUser.getUid();

        //Get authors name
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query queryRef = usersRef.orderByChild("userKey").equalTo(comments.getUserKey());
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    holder.commentAuthorFullName.setText(users.getFirstName() + " " + users.getLastName());
                    if (users.getUserKey().equals(userKey)) {
                        holder.commentDelete.setVisibility(View.VISIBLE);
                    } else {
                        holder.commentDelete.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        //get body
        holder.commentBody.setText(comments.getCommentBody());



        //delete comment button
        holder.commentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open a comment section
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments/" + commentsKey);

                //this is a confirmation dialog for the delete comments functionality
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete this comment?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Call removeValue() method to delete the node
                        ref.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }


    @Override
    public int getItemCount() {
        if (mCommentsList != null && mCommentsList.size() > 0) {
            return mCommentsList.size();
        } else return 0;
    }

    public void setComments(List<Comments> comments) {
        mCommentsList = comments;
        notifyDataSetChanged();
    }


}
