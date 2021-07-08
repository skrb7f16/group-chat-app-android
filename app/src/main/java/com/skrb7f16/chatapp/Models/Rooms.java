package com.skrb7f16.chatapp.Models;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.skrb7f16.chatapp.MainActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rooms {
    String roomName,roomKey;
    Users creator;
    List<Users> members;
    String created;


    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }



    public Rooms(String roomName, String roomKey) {
        this.roomName = roomName;
        this.roomKey = roomKey;
        members=new ArrayList<Users>();
    }

    public Rooms() {
    }

    public Rooms(String roomName, String roomKey, Users creator, String created) {
        this.roomName = roomName;
        this.roomKey = roomKey;
        this.creator = creator;
        this.created = created;
        members=new ArrayList<Users>();
        members.add(creator);
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

    public List<Users> getMembers() {
        return members;
    }

    public void setMembers(List<Users> members) {
        this.members = members;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean addMember(Users user, Context context)
    {
        int i=0;
        if(members==null){
            members=new ArrayList<>();
            members.add(user);
            return false;
        }
        for(Users u:members){
            if(u.getUserId().equals(user.getUserId())){
                i=1;
            }
        }
        if(i==0) {
            members.add(user);
            return false;
        }
        else{
            Toast.makeText(context,"You are already in room",Toast.LENGTH_SHORT).show();
            return true;

        }
    }

}
