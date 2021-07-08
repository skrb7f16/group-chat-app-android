package com.skrb7f16.chatapp.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatTab {
    String roomName,roomKey,lastMessage;

    public ChatTab(String roomName, String roomKey) {
        this.roomName = roomName;
        this.roomKey = roomKey;
        fetchLastMessage();
    }

    public ChatTab(){

    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void fetchLastMessage(){
        FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/").getReference().child("Chats").child(roomName+roomKey).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Message m=dataSnapshot.getValue(Message.class);
                    lastMessage=m.getMessage();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
