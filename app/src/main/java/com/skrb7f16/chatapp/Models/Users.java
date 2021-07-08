package com.skrb7f16.chatapp.Models;

import android.widget.Toast;

import com.skrb7f16.chatapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Users {
    String userId,username;
    List<String> roomnames;

    public List<String> getRoomnames() {
        return roomnames;
    }

    public void setRoomnames(List<String> roomnames) {
        this.roomnames = roomnames;
    }

    public Users(String userId, String username, List<String> roomnames) {
        this.userId = userId;
        this.username = username;
        this. roomnames = roomnames;
    }

    public Users(String userId, String username) {
        this.userId = userId;
        this.username = username;
        roomnames=new ArrayList<String>();
    }

    public Users() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void addRoom(String room){
        if(roomnames==null){
            roomnames=new ArrayList<String>();
        }
        int i=0;
        for(String s:roomnames){
            if(s.equals(room)){
                i=1;
            }
        }
        if(i==0)
        this.roomnames.add(room);

    }
}
