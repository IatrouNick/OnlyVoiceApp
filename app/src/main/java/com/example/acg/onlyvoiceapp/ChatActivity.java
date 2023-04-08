package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;

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

public class ChatActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener {

    private FrameLayout frame1;
    private FrameLayout frame2;
    private Button chatBtn;
    private Button usersBtn;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerViewUsers;
    private RecyclerView mRecyclerChatViewUsers;
    private SearchAdapter mAdapterUsers;
    private List<Users> mUserList = new ArrayList<>();
    private List<Users> mUserChatList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private List<Chat> mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FrameLayout frame1 = findViewById(R.id.frame1);
        FrameLayout frame2 = findViewById(R.id.frame2);
        Button chatView = findViewById(R.id.chatBtn);
        Button usersBtn = findViewById(R.id.usersBtn);

        frame2.setVisibility(View.GONE);
        //set visibilities for the 2 frames
        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame1.setVisibility(View.VISIBLE);
                frame2.setVisibility(View.GONE);
            }
        });

        usersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame2.setVisibility(View.VISIBLE);
                frame1.setVisibility(View.GONE);
            }
        });

        //search functionality if needed and present all users
        SearchView searchView = findViewById(R.id.searchViewChat);

        mRecyclerViewUsers = findViewById(R.id.searchResultsRecyclerViewChat);
        mRecyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        mAdapterUsers = new SearchAdapter(this, mUserList);
        mRecyclerViewUsers.setAdapter(mAdapterUsers);

        mAdapterUsers.setOnItemClickListener(this);
        //onItemClick(postsRef);
        if (searchView.getQuery().toString().isEmpty()) {
            performSearch("");
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                performSearch(query);
                searchView.clearFocus();
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mUserList.clear();
                mAdapterUsers.notifyDataSetChanged();
                performSearch(newText);

                return false;
            }


        });


        //Have users that we have already chatted before
        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readMessage(firebaseUser.getUid());


        mRecyclerChatViewUsers = findViewById(R.id.recentRecyclerViewChat);
        mRecyclerChatViewUsers.setLayoutManager(new LinearLayoutManager(this));
        mAdapterUsers = new SearchAdapter(this, mUserChatList);
        mRecyclerChatViewUsers.setAdapter(mAdapterUsers);

        mAdapterUsers.setOnItemClickListener(this);
    }


    //Perfrorm the sesarch functionality for users
    private void performSearch(String query) {

        //clear list if i change something in the search
        mUserList.clear();

        mAdapterUsers.notifyDataSetChanged();
        // Query the database and add the results to the list
        // Here's an example using the orderByChild and equalTo methods:
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        // Query queryRef = usersRef.orderByChild("lastName").equalTo(query);
        Query queryRef = usersRef.orderByChild("lastName");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);

                    if (query.equals("")) {
                        mUserList.add(users);
                        mAdapterUsers.notifyItemInserted(mUserList.size() - 1);
                    }
                    //search with lastname
                    else if (users.getLastName().toLowerCase().contains(query.toLowerCase())) {
                        mUserList.add(users);
                        mAdapterUsers.notifyItemInserted(mUserList.size() - 1);
                    }
                    //else search with firstname
                    else if (users.getFirstName().toLowerCase().contains(query.toLowerCase())) {
                        mUserList.add(users);
                        mAdapterUsers.notifyItemInserted(mUserList.size() - 1);
                    }
                    //check if there are no users with that name do remove the recycle view
                    if (mUserList.size() == 0) {
                        mRecyclerViewUsers.setVisibility(View.GONE);
                    } else {
                        mRecyclerViewUsers.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mRecyclerViewUsers.setVisibility(View.INVISIBLE);

            }

        });

        if (mUserList.size() == 0) {
            mRecyclerViewUsers.setVisibility(View.GONE);
        } else {
            mRecyclerViewUsers.setVisibility(View.VISIBLE);
        }

    }


    //On user click go to their profile
    public void onItemClick(Users users) {
        // Toast.makeText(WallActivity.this, "Button Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
        intent.putExtra("userKey", users.getUserKey()); // pass the user ID to the next activity
        startActivity(intent);
    }


    //show messages in each chat
    private List<Users> readMessage(String sender) {
        mChat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getSender().equals(sender) || (chat.getReceiver().equals(sender))) {
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                        Query queryRef = usersRef.orderByChild("userKey").equalTo(chat.getReceiver());
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Users users = dataSnapshot.getValue(Users.class);
                                    if (!mUserChatList.contains(users)) {
                                        mUserChatList.add(users);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }
            });
        System.out.println("list of users is " + mUserChatList);
        return mUserChatList;
        }
    }