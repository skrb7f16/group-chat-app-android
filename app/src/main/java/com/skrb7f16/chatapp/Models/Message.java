package com.skrb7f16.chatapp.Models;

import java.time.LocalDateTime;

public class Message {
    String roomName,message,sender,senderUid;
    private long at;
    boolean isSystem;


    public Message(String roomName, String message, String sender, Users by, long at) {
        this.roomName = roomName;
        this.message = message;
        this.sender = sender;
        this.at = at;
        this.isSystem=false;
    }

    public Message(String roomName, String message, String sender, String senderUid, long at) {
        this.roomName = roomName;
        this.message = message;
        this.sender = sender;
        this.senderUid = senderUid;
        this.at = at;
        this.isSystem=false;
    }


    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public Message() {
        this.isSystem=false;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getAt() {
        return at;
    }

    public void setAt(long at) {
        this.at = at;
    }
}
