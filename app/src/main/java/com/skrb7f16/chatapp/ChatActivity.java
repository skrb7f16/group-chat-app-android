package com.skrb7f16.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.chatapp.Adapters.ChatAdapter;
import com.skrb7f16.chatapp.Models.Message;
import com.skrb7f16.chatapp.Models.Users;
import com.skrb7f16.chatapp.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    Users users;
    FirebaseDatabase database;
    ProgressDialog progressBar;
    ArrayList<Message> messages=new ArrayList<>();
    ChatAdapter chatAdapter;
    String roomname;
    String roomkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        roomname=getIntent().getStringExtra("roomname");
        roomkey=getIntent().getStringExtra("roomkey");
        binding.RoomName.setText(roomname);
        chatAdapter=new ChatAdapter(messages,this);
        binding.ChatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.ChatRecyclerView.setLayoutManager(layoutManager);

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Logging in");
        progressBar.setTitle("Please wait.....");
        progressBar.show();
        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        database=FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/");
        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users=snapshot.getValue(Users.class);
                progressBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("Chats").child(roomname+roomkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    Message message=d.getValue(Message.class);
                    messages.add(message);
                }
                chatAdapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(messages.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.messageToSend.getText().toString().length()<1){
                    return;
                }
                Message model=new Message();
                model.setMessage(binding.messageToSend.getText().toString());
                model.setSender(auth.getCurrentUser().getDisplayName());
                model.setSenderUid(auth.getUid());
                model.setAt(new Date().getTime());
                model.setRoomName(roomname);
                database.getReference().child("Chats").child(roomname+roomkey).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        binding.messageToSend.setText("");
                    }
                });
            }
        });
        binding.RoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("meow", "onClick: ");
                Intent intent=new Intent(ChatActivity.this,RoomInfoActivity.class);
                intent.putExtra("roomname",roomname);
                intent.putExtra("roomkey",roomkey);
                startActivityForResult(intent,100);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}