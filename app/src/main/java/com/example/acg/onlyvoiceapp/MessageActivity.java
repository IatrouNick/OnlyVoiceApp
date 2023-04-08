package com.example.acg.onlyvoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    TextView usernameMessage;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;
    ImageButton sendMessageBtn;
    ImageButton voiceBtn;
    EditText messageText;

    MessagesAdapter messagesAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        usernameMessage = findViewById(R.id.usernameMessage);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        voiceBtn = findViewById(R.id.voiceBtn);
        messageText = findViewById(R.id.messageText);
        recyclerView = findViewById(R.id.chatView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getApplicationContext()));
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        String userId = intent.getStringExtra("userKey");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query queryRef = databaseReference.orderByChild("userKey").equalTo(userId);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usernameMessage.setText(users.getFirstName() + " " + users.getLastName());
                    getSupportActionBar().setTitle(usernameMessage.getText());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

                readMessage(firebaseUser.getUid(), userId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording(view);
            }
        });


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageText.getText().toString();
                if (message != null) {
                    sendMessage(message, userId, firebaseUser.getUid() );
                } else {
                    Toast.makeText(MessageActivity.this, "Message can not be empty", Toast.LENGTH_SHORT).show();
                }
                messageText.setText("");

            }
        });


    }


    //create the object that will be send into firebase
    private void sendMessage(String message, String receiver, String sender) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("receiver", receiver);
        hashMap.put("sender", sender);


        reference.child("Chat").push().setValue(hashMap);

    }

    //show messages in each chat
    private void readMessage(String sender, String receiver) {
        mChat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if ((chat.getReceiver().equals(receiver) && chat.getSender().equals(sender))
                            || (chat.getReceiver().equals(sender) && chat.getSender().equals(receiver))) {
                        mChat.add(chat);
                    }
                    messagesAdapter = new MessagesAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messagesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//speech to text functionality
    public void startRecording(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start");
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK){
            if (messageText.getText()==null) {
                messageText.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            }
            else{
                String message = messageText.getText() + " ";
                messageText.setText(message + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            }

        }

    }

}