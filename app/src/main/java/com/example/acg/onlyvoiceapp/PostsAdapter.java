package com.example.acg.onlyvoiceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.List;

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

        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
        Query query = likesRef.orderByChild("postKey").equalTo(postKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countLikes = 0;
                for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                    countLikes++;

                }
                holder.postLike.setText(String.valueOf(countLikes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        

//use below functionality to like/ remove like from a post
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            private static final long DOUBLE_CLICK_TIME_DELTA = 1000; // Time in milliseconds
            long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {

                    DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
                    Query query = likesRef.orderByChild("postKey").equalTo(postKey);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            found = false;
                            if (snapshot.exists()) {
                                // postKey found in likes database
                                for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                                    // Access like data
                                    String key = likeSnapshot.getKey();
                                    String userKeyId = likeSnapshot.child("userKey").getValue(String.class);
                                    String postKeyId = likeSnapshot.child("postKey").getValue(String.class);
                                    //PostLikes likes = likeSnapshot.getValue(PostLikes.class);
                                    if (userKeyId.equals(userKey) && postKeyId.equals(postKey)) {
                                        found = true;
                                        likesRef.child(key).removeValue();
                                        //likesRef.removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // handle error
                        }
                    });

                System.out.println(found);
                if (found) {
                    //remove like
                    System.out.println(userKey);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes/" + postKey);

                    // Call removeValue() method to delete the node
                    ref.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
                                    Query query = likesRef.orderByChild("postKey").equalTo(postKey);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int countLikes = 0;
                                            for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                                                countLikes++;

                                            }
                                            holder.postLike.setText(String.valueOf(countLikes));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occurred while deleting the node
                                }
                            });


                }
                if (!found) {
                    //like

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes");
                    String key = databaseReference.push().getKey();
                    PostLikes likesAdd = new PostLikes(userKey, postKey);
                    databaseReference.child(String.valueOf(key)).setValue(likesAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
                            Query query = likesRef.orderByChild("postKey").equalTo(postKey);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int countLikes = 0;
                                    for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                                        countLikes++;

                                    }
                                    holder.postLike.setText(String.valueOf(countLikes));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    });
                }
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
