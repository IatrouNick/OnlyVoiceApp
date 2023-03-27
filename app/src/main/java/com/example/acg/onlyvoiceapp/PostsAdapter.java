package com.example.acg.onlyvoiceapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class PostsAdapter extends RecyclerView.Adapter<PostsItems> {

    private Context mContext;
    private List<Posts> mPostList;
    private PostsAdapter.OnItemClickListener onItemClickListener;
    private PostsAdapter.OnDoubleClickListener onDoubleClickListener;
    private FirebaseAuth mAuth;
    public boolean found = false;

    public PostsAdapter(Context context, List<Posts> postList) {
        mContext = context;
        mPostList = postList;

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
        holder.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //open a comment section

            }
        });

        //delete button
        holder.postDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open a comment section
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts/" + postKey);

                // Call removeValue() method to delete the node
                ref.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
            }
        });

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
