package com.example.joinme.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;

import com.example.joinme.activity.ChatActivity;
import com.example.joinme.objects.Conversation;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter {
    private List<Conversation> mConversationList;
    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,latestMsg,lastTime;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            latestMsg = itemView.findViewById(R.id.latest_msg);
            lastTime = itemView.findViewById(R.id.time);
        }
    }

    public FriendAdapter(List<Conversation> conversationList, Context context){
        mConversationList = conversationList;
        mContext = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Conversation conversation = mConversationList.get(position);
        ((ViewHolder)holder).name.setText(conversation.getName());
        ((ViewHolder)holder).latestMsg.setText(conversation.getLatestMessage().getMessageContent());
        ((ViewHolder)holder).lastTime.setText(conversation.getLatestMessage().getTime().getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump to chat activity
                Intent chatActivity = new Intent(v.getContext(), ChatActivity.class);

                // Testing purpose
                chatActivity.putExtra("friendUid", "qa6KACdJ0RYZfVDXLtpKL2HcxJ43");
                chatActivity.putExtra("friendUsername", "IU");
                mContext.startActivity(chatActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConversationList.size();
    }
}
