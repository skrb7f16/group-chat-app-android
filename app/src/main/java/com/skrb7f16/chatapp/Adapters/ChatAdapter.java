package com.skrb7f16.chatapp.Adapters;


import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.skrb7f16.chatapp.Models.Message;
import com.skrb7f16.chatapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatAdapter extends RecyclerView.Adapter {


    ArrayList<Message> messages;
    Context context;
    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;
    int SYSTEM_MESSAGE=3;

    public ChatAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ChatAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new ChatAdapter.SenderViewHolder(view);
        }
        else if(viewType==RECIEVER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_reciever,parent,false);
            return new ChatAdapter.RecieverViewHolder(view);
        }
        else{
            View view=LayoutInflater.from(context).inflate(R.layout.system_message,parent,false);
            return new ChatAdapter.SystemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=messages.get(position);

        if(holder.getClass()== ChatAdapter.SenderViewHolder.class){

            ((SenderViewHolder)holder).senderMessage.setText(message.getMessage());
            ((ChatAdapter.SenderViewHolder)holder).senderTime.setText(getTimeFromStamp(message.getAt()));

        }
        else if(holder.getClass()== ChatAdapter.RecieverViewHolder.class){
            ((ChatAdapter.RecieverViewHolder)holder).recieverMsg.setText(message.getMessage());
            ((RecieverViewHolder)holder).senderName.setText(message.getSender());
            ((ChatAdapter.RecieverViewHolder)holder).recieverTime.setText(getTimeFromStamp(message.getAt()));

        }
        else if(holder.getClass()==ChatAdapter.SystemViewHolder.class){
            ((SystemViewHolder)holder).message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{

        TextView recieverMsg,recieverTime,senderName;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg=itemView.findViewById(R.id.recieverMessage);
            recieverTime=itemView.findViewById(R.id.recieverTime);
            senderName=itemView.findViewById(R.id.recieverName);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMessage,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage=itemView.findViewById(R.id.senderMessage);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }

    public class SystemViewHolder extends RecyclerView.ViewHolder{

        TextView message;
        public SystemViewHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.systemMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {


        if(messages.get(position).isSystem()){
            return SYSTEM_MESSAGE;
        }
        if (messages.get(position).getSenderUid().equals(FirebaseAuth.getInstance().getUid())) {

            return SENDER_VIEW_TYPE;
        } else {
            return RECIEVER_VIEW_TYPE;
        }
    }
    public String getTimeFromStamp(long stamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stamp);
        String date = DateFormat.format("hh:mm aa", calendar).toString();
        return date;
    }
}
