package com.example.acg.onlyvoiceapp.Messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acg.onlyvoiceapp.Classes.Chat;
import com.example.acg.onlyvoiceapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final int MSG_TYPE_RECEIVER = 0;
    private static final int MSG_TYPE_SENDER = 1;

    private Context mContext;
    private List<Chat> mChat;
    FirebaseUser firebaseUser;

    public MessagesAdapter(Context context, List<Chat> mChat) {
        this.mContext = context;
        this.mChat = mChat;

    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_SENDER){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sender, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_receiver, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.showMessage.setText(chat.getMessage());

    }

    @Override
    public int getItemCount() {
        if (mChat != null && mChat.size() > 0) {
            return mChat.size();
        } else return 0;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_SENDER;
        }else {
            return MSG_TYPE_RECEIVER;
        }
    }
}