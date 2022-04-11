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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.chatapp.Models.Message;
import com.skrb7f16.chatapp.Models.Rooms;
import com.skrb7f16.chatapp.Models.Users;
import com.skrb7f16.chatapp.databinding.ActivityRoomInfoBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RoomInfoActivity extends AppCompatActivity {

    Rooms rooms;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ActivityRoomInfoBinding binding;
    String roomname,roomkey;
    ProgressDialog progressbar;
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        binding=ActivityRoomInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#03b1fc"));
        actionBar.setBackgroundDrawable(colorDrawable);
        progressbar=new ProgressDialog(this);

        progressbar.setMessage("Please Wait....");

        roomname=getIntent().getStringExtra("roomname");
        roomkey=getIntent().getStringExtra("roomkey");
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/");


        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users=snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("Rooms").child(roomname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms=snapshot.getValue(Rooms.class);
                populate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setTitle("Leaving");
                progressbar.show();
                leaveRoom();
            }
        });
    }

    public String getTimeFromStamp(long stamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stamp);
        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar).toString();
        return date;
    }

    public void populate(){
        List<String> temp=new ArrayList<String>();
        for(Users u:rooms.getMembers()){
            temp.add(u.getUsername());

        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.mytextview, temp);
        binding.memberlist.setAdapter(adapter);
        binding.createdOn.setText("Created on: "+getTimeFromStamp(Long.parseLong(rooms.getCreated())));
        binding.groupName.setText(rooms.getRoomName());

    }

    public void leaveRoom(){
        String temp=auth.getUid();
        Iterator<Users>u=rooms.getMembers().iterator();
        while(u.hasNext()){
            Users tempUser=u.next();
            if(tempUser.getUserId().equals(users.getUserId())){
                u.remove();
                break;
            }
        }
//        for(Users u:rooms.getMembers()){
//            if(u.getUserId().equals(temp)){
//                rooms.getMembers().remove(u);
//
//            }
//        }

        users.getRoomnames().remove(roomname.toString());
        populate();
        database.getReference().child("Users").child(users.getUserId()).setValue(users);
        Message model = new Message();
        model.setMessage(auth.getCurrentUser().getDisplayName() + " has left the room");
        model.setSender("System");
        model.setSenderUid("SYSTEM");
        model.setAt(new Date().getTime());
        model.setRoomName(roomname);
        model.setSystem(true);
        database.getReference().child("Chats").child(roomname + roomkey).push().setValue(model);
        database.getReference().child("Rooms").child(roomname).setValue(rooms).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressbar.hide();
                Intent intent=new Intent(RoomInfoActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
        });

    }

}