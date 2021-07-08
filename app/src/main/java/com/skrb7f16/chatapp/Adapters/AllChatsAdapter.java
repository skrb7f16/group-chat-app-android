package com.skrb7f16.chatapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.chatapp.ChatActivity;
import com.skrb7f16.chatapp.MainActivity;
import com.skrb7f16.chatapp.Models.ChatTab;
import com.skrb7f16.chatapp.Models.Message;
import com.skrb7f16.chatapp.R;

import java.util.ArrayList;

public class AllChatsAdapter extends RecyclerView.Adapter<AllChatsAdapter.ViewHolder> {
    ArrayList<ChatTab> chatTabs;
    Context context;

    public AllChatsAdapter(ArrayList<ChatTab> chatTabs, Context context) {
        this.chatTabs = chatTabs;
        this.context = context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_room_name,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final ChatTab chatTab=chatTabs.get(position);
        holder.roomaname.setText(chatTab.getRoomName());
        holder.roomkey.setText(chatTab.getRoomKey());
        FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/").getReference().child("Chats").child(chatTab.getRoomName()+chatTab.getRoomKey()).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Message m=dataSnapshot.getValue(Message.class);
                    chatTab.setLastMessage(m.getMessage());
                    holder.lastMessage.setText(chatTab.getLastMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("roomname",chatTab.getRoomName());
                intent.putExtra("roomkey",chatTab.getRoomKey());

                ((Activity)context).startActivityForResult(intent,105);
            }
        });
    }



    @Override
    public int getItemCount() {

        return chatTabs.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView roomaname,roomkey,lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomaname=itemView.findViewById(R.id.roomnameAllChats);
            roomkey=itemView.findViewById(R.id.roomkeyAllChats);
            lastMessage=itemView.findViewById(R.id.lastmessage);
        }
    }
}
