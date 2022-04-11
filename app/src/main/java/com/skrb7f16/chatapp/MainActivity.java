package com.skrb7f16.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.chatapp.Models.Message;
import com.skrb7f16.chatapp.Models.Rooms;
import com.skrb7f16.chatapp.Models.Users;
import com.skrb7f16.chatapp.Utilities.LogoutAsk;
import com.skrb7f16.chatapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    boolean create=false;
    boolean join=false;
    ProgressDialog progressBar;
    FirebaseUser u;
    Users users;
    Rooms rooms;
    int active=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#03b1fc"));
        actionBar.setBackgroundDrawable(colorDrawable);
        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Checking ");
        progressBar.setTitle("Please wait.....");
        progressBar.show();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/");
        rooms=new Rooms();
        u=auth.getCurrentUser();
        users=new Users(u.getUid(),u.getDisplayName());
        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users=snapshot.getValue(Users.class);
                progressBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.hide();
                Toast.makeText(MainActivity.this,"Please login again",Toast.LENGTH_SHORT).show();
                finish();
            }

        });
        binding.newRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(active==2){

                    binding.joinRoomLinearLayout.setVisibility(View.GONE);
                    active=0;
                    join=!join;
                    binding.joinRoom.setBackgroundColor(Color.parseColor("#03b1fc"));
                }
                if(!create) {
//                    binding.makeRoomLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                    binding.makeRoomLinearLayout.setVisibility(View.VISIBLE);
                    binding.newRoom.setBackgroundColor(Color.GREEN);
                    active=1;
                }
                else{
//                    binding.makeRoomLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                    binding.makeRoomLinearLayout.setVisibility(View.GONE);
                    binding.newRoom.setBackgroundColor(Color.parseColor("#03b1fc"));
                    active=0;
                }
                create=!create;
            }
        });
        binding.joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(active==1){
                    binding.makeRoomLinearLayout.setVisibility(View.GONE);
                    active=0;
                    create=!create;
                    binding.newRoom.setBackgroundColor(Color.parseColor("#03b1fc"));
                }
                if(!join) {
//                    binding.joinRoomLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                    binding.joinRoomLinearLayout.setVisibility(View.VISIBLE);
                    binding.joinRoom.setBackgroundColor(Color.GREEN);
                    active=2;
                }
                else{
//                    binding.joinRoomLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                    binding.joinRoomLinearLayout.setVisibility(View.GONE);
                    binding.joinRoom.setBackgroundColor(Color.parseColor("#03b1fc"));
                    active=0;
                }
                join=!join;

            }
        });

        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.roomName.getText().toString().length()>=4&&binding.roomKey.getText().toString().length()>4){
                    progressBar.show();
                    checkRoom(binding.roomName.getText().toString(),1);
                }
                else{

                    Toast.makeText(MainActivity.this,"Roomname or roomkey name too short",Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.roomNameJoin.getText().toString().length()>=4&&binding.roomKeyJoin.getText().toString().length()>4){
                    progressBar.show();
                    checkRoom(binding.roomNameJoin.getText().toString(),2);
                }
                else{
                    Toast.makeText(MainActivity.this,"Roomname or roomkey name too short",Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.allRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this, AllChatsActivity.class);
                ArrayList<String> listOfStrings;
                if(users.getRoomnames()!=null) {
                    listOfStrings= new ArrayList<>(users.getRoomnames().size());
                    listOfStrings.addAll(users.getRoomnames());
                }
                else{
                    listOfStrings=new ArrayList<>();
                }

                intent.putStringArrayListExtra("roomnames", listOfStrings);
                startActivity(intent);
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutAsk logoutAsk=new LogoutAsk(MainActivity.this);
                logoutAsk.showDialog(MainActivity.this);

            }
        });
    }
    public void checkRoom(String roomName,int action){

        database.getReference().child("Rooms").child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                   if(action==1){

                       Toast.makeText(MainActivity.this,"This room already exist",Toast.LENGTH_SHORT).show();
                       progressBar.hide();
                   }
                   else if(action==2){
                        joinRoom();
                   }
                }
                else{
                    if(action==1){

                        createRoom();
                    }
                    else if(action==2){
                        Toast.makeText(MainActivity.this,"This room doesnt exist",Toast.LENGTH_SHORT).show();
                        progressBar.hide();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void createRoom(){
        users.addRoom(binding.roomName.getText().toString());
        database.getReference().child("Users").child(users.getUserId()).setValue(users);
        rooms=new Rooms(binding.roomName.getText().toString(),binding.roomKey.getText().toString(),users, ""+new Date().getTime());
        database.getReference().child("Rooms").child(binding.roomName.getText().toString()).setValue(rooms);
        Message model=new Message();
        model.setMessage(auth.getCurrentUser().getDisplayName()+" has created the room");
        model.setSender("System");
        model.setSenderUid("SYSTEM");
        model.setAt(new Date().getTime());
        model.setRoomName(binding.roomName.getText().toString());
        model.setSystem(true);
        database.getReference().child("Chats").child(binding.roomName.getText().toString()+binding.roomKey.getText().toString()).push().setValue(model);
        progressBar.hide();
        Intent intent=new Intent(MainActivity.this,RoomCreatingActivity.class);
        intent.putExtra("roomname",binding.roomName.getText().toString());
        intent.putExtra("roomkey",binding.roomKey.getText().toString());
        startActivityForResult(intent,100);
    }

    public void joinRoom(){
        users.addRoom(binding.roomNameJoin.getText().toString());
        database.getReference().child("Users").child(users.getUserId()).setValue(users);
        database.getReference().child("Rooms").child(binding.roomNameJoin.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms=snapshot.getValue(Rooms.class);
                Log.d("meow", "onDataChange: "+binding.roomKeyJoin.getText());
                if(rooms.getRoomKey().equals(binding.roomKeyJoin.getText().toString())){
                    boolean test=rooms.addMember(users,MainActivity.this);
                    if(!test) {
                        Message model = new Message();
                        model.setMessage(auth.getCurrentUser().getDisplayName() + " has joined the room");
                        model.setSender("System");
                        model.setSenderUid("SYSTEM");
                        model.setAt(new Date().getTime());
                        model.setRoomName(binding.roomNameJoin.getText().toString());
                        model.setSystem(true);
                        database.getReference().child("Chats").child(binding.roomNameJoin.getText().toString() + binding.roomKeyJoin.getText().toString()).push().setValue(model);
                    }
                    updateRoom(binding.roomNameJoin.getText().toString());

                }
                else{
                    Toast.makeText(MainActivity.this,"Wrong room key",Toast.LENGTH_SHORT).show();
                    binding.roomNameJoin.setText("");
                    binding.roomKeyJoin.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateRoom(String roomname){
        database.getReference().child("Rooms").child(roomname).setValue(rooms).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.hide();
                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("roomname",binding.roomNameJoin.getText().toString());
                intent.putExtra("roomkey",binding.roomKeyJoin.getText().toString());
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            binding.roomNameJoin.setText("");
            binding.roomName.setText("");
            binding.roomKey.setText("");
            binding.roomKeyJoin.setText("");
            create=false;
            join=false;
        }
    }


}