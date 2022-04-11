package com.skrb7f16.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.chatapp.Adapters.AllChatsAdapter;
import com.skrb7f16.chatapp.Models.ChatTab;
import com.skrb7f16.chatapp.Models.Rooms;
import com.skrb7f16.chatapp.Models.Users;
import com.skrb7f16.chatapp.databinding.ActivityAllChatsBinding;

import java.util.ArrayList;
import java.util.List;

public class AllChatsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    Users users;
    FirebaseUser firebaseUser;
    ProgressDialog progressBar;
    ActivityAllChatsBinding binding;
    ArrayList<ChatTab> chatTabs=new ArrayList<>();
    AllChatsAdapter allChatsAdapter;
    Rooms rooms;
    ChatTab chatTab;
    int i=0;
    ArrayList<String>temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);
        binding=ActivityAllChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#03b1fc"));
        actionBar.setBackgroundDrawable(colorDrawable);

        allChatsAdapter=new AllChatsAdapter(chatTabs,AllChatsActivity.this);
        binding.AllChatsRecyclerView.setAdapter(allChatsAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(AllChatsActivity.this);
        binding.AllChatsRecyclerView.setLayoutManager(linearLayoutManager);
        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Setting up your rooms ");
        progressBar.setTitle("Please wait.....");

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        users=new Users(firebaseUser.getUid(),firebaseUser.getDisplayName());
        temp=getIntent().getStringArrayListExtra("roomnames");
        if(temp.size()==0){
            progressBar.hide();
            Toast.makeText(AllChatsActivity.this,"Sorry You dont have any rooms to see yet please make some",Toast.LENGTH_SHORT).show();
        }
        progressBar.show();
        List<String>realRooms=temp;
        users.setRoomnames(realRooms);
        database=FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/");
        fetchThings();


    }

    public void fetchThings(){
        database.getReference().child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatTabs.clear();
                for (DataSnapshot d:snapshot.getChildren()){

                    rooms=new Rooms();
                    rooms=d.getValue(Rooms.class);
                    for(String s:temp){
                        if(s.equals(rooms.getRoomName())){
                            chatTab=new ChatTab(rooms.getRoomName(),rooms.getRoomKey());
                            chatTabs.add(chatTab);
                        }
                    }
                }
                allChatsAdapter.notifyDataSetChanged();
                progressBar.hide();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==105){
            fetchThings();
        }
    }
}