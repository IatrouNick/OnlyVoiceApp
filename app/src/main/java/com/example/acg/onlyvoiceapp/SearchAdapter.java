package com.example.acg.onlyvoiceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchUsersItems> {

    private Context mContext;
    private List<Users> mUserList;
    private OnItemClickListener onItemClickListener;

    public SearchAdapter(Context context, List<Users> userList) {
        mContext = context;
        mUserList = userList;
    }


    public interface OnItemClickListener {
        void onItemClick(Users users);
    }

    public void setOnItemClickListener(SearchAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SearchUsersItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_users_search_items, parent, false);
        return new SearchUsersItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUsersItems holder, int position) {
        //get adapters fields to set them on the recycle view
        Users users = mUserList.get(position);
        holder.searchFirstName.setText(users.getFirstName());
        holder.searchLastName.setText(users.getLastName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(users);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}

