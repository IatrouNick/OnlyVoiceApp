package com.example.acg.onlyvoiceapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

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




    public PostsAdapter(Context context, List<Posts> postList) {
        mContext = context;
        mPostList = postList;

    }
    public interface OnItemClickListener {
        void onItemClick(Posts posts);
    }

    public void setOnItemClickListener(PostsAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
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

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query queryRef = usersRef.orderByChild("userKey").equalTo(posts.getUserKey());
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    holder.postAuthorFullName.setText(users.getFirstName() + " " + users.getLastName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        holder.postBody.setText(posts.getPostBody());
        holder.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //open a comment section

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(posts);
                    System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

                }
            }
        });
    }
    @Override
    public int getItemCount() {
        if(mPostList != null && mPostList.size() > 0) {
            return mPostList.size();
        }else return 0;
    }

    public void setPosts(List<Posts> posts) {
        mPostList = posts;
        notifyDataSetChanged();
    }

}
