package com.skrb7f16.chatapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.skrb7f16.chatapp.databinding.ActivityRoomCreatingBinding;

public class RoomCreatingActivity extends AppCompatActivity {

    ActivityRoomCreatingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_creating);
        binding=ActivityRoomCreatingBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#03b1fc"));
        actionBar.setBackgroundDrawable(colorDrawable);
        Intent intent=getIntent();
        String roomname=intent.getStringExtra("roomname");
        String roomkey=intent.getStringExtra("roomkey");
        binding.roomname.setText(roomname);
        binding.roomkey.setText(roomkey);
        binding.goToRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RoomCreatingActivity.this, ChatActivity.class);
                intent.putExtra("roomname",roomname);
                intent.putExtra("roomkey",roomkey);
                startActivity(intent);
            }
        });
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"I invite you to join my room and lets chat. Room name is \n"+roomname+"Roomkey is \n"+roomkey);
                startActivity(intent);
            }
        });
    }
}