package com.example.joinme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;

import com.example.joinme.objects.Friend;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter {
    private List<Friend> mFriendList;
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

    public FriendAdapter(List<Friend> friendList){
        mFriendList=friendList;
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
        Friend friend = mFriendList.get(position);
        ((ViewHolder)holder).name.setText(friend.getName());
        ((ViewHolder)holder).latestMsg.setText(friend.getLatestMessage().getContent());
        ((ViewHolder)holder).lastTime.setText(friend.getLatestMessage().getTime().toString());
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }
}
