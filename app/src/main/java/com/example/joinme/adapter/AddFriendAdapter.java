package com.example.joinme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.objects.User;

import java.util.List;

public class AddFriendAdapter extends RecyclerView.Adapter {
    private List<User> userList;

    public AddFriendAdapter(List<User> userList) {
        this.userList = userList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, about;
        ImageView profilePhoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            about = itemView.findViewById(R.id.user_about);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_friend_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = userList.get(position);
        ((ViewHolder) holder).name.setText(user.getUsername());
        ((ViewHolder) holder).about.setText(user.getAbout());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

