package com.example.acg.onlyvoiceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsItems> {

    private Context mContext;
    private List<Posts> mPostList;
    private PostsAdapter.OnItemClickListener onItemClickListener;
    private PostsAdapter.OnDoubleClickListener onDoubleClickListener;
    private FirebaseAuth mAuth;
    public boolean found = false;
    private LayoutInflater inflater;
    public String userName;



    public PostsAdapter(Context context, List<Posts> postList) {
        mContext = context;
        mPostList = postList;
        inflater = LayoutInflater.from(context);

    }

    public interface OnItemClickListener {
        void onItemClick(DatabaseReference posts);
    }

    public void setOnItemClickListener(PostsAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }

    public interface OnDoubleClickListener extends PostsAdapter.OnItemClickListener {
        void onDoubleClick(Posts posts);

    }

    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        this.onDoubleClickListener = listener;
    }

    @NonNull
    @Override
    public PostsItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_posts_items, parent, false);
        return new PostsItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsItems holder, int position) {
        //postAuthorFirstName
        //postAuthorLastName
        //postBody
        //postComment
        Posts posts = mPostList.get(position);
        String postKey = posts.getPostKey();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userKey = firebaseUser.getUid();




        //Get authors name
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query queryRef = usersRef.orderByChild("userKey").equalTo(posts.getUserKey());
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    holder.postAuthorFullName.setText(users.getFirstName() + " " + users.getLastName());
                    if (users.getUserKey().equals(userKey)) {
                        holder.postDelete.setVisibility(View.VISIBLE);
                    } else {
                        holder.postDelete.setVisibility(View.GONE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //get body
        holder.postBody.setText(posts.getPostBody());


        //get comments button
        //show comments of a post/ create comment
        holder.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, PostsDetails.class);
                intent.putExtra("postKey", postKey); // pass the post key to the next activity
                mContext.startActivity(intent);
                    }
                });


        //delete button
        holder.postDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open a comment section
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts/" + postKey);

                //this is a confirmation dialog for the delete post functionality
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete this post?");
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

        //postsRef = FirebaseDatabase.getInstance().getReference("Posts");


//find likes for each post
        // Attach a listener to each post to count the number of likes
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes").child("postKey");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    if (postSnapshot.getKey().equals(postKey)) {
                        int likesCount = (int) postSnapshot.getChildrenCount();
                        holder.postLike.setText(String.valueOf(likesCount));

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


//use below functionality to like/ remove like from a post
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            private static final long DOUBLE_CLICK_TIME_DELTA = 200; // Time in milliseconds
            long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {

                    //like/dislike a post
                    DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes").child("postKey");
                    likesRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userKey)) {

                                boolean liked = snapshot.child(userKey).getValue(Boolean.class);
                                if (liked) {
                                    if (holder.postLike.getText().toString().equals("1")) {
                                        holder.postLike.setText(String.valueOf("0"));

                                    }
                                    likesRef.child(postKey).child(userKey).removeValue();


                                } else {
                                    likesRef.child(postKey).child(userKey).setValue(true);
                                }
                            } else {
                                likesRef.child(postKey).child(userKey).setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                lastClickTime = clickTime;
            }

        });

    }


    @Override
    public int getItemCount() {
        if (mPostList != null && mPostList.size() > 0) {
            return mPostList.size();
        } else return 0;
    }

    public void setPosts(List<Posts> posts) {
        mPostList = posts;
        notifyDataSetChanged();
    }


}
