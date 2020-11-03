package com.example.joinme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendPhotoAdapter extends RecyclerView.Adapter {
    private ArrayList<String> uids;
    private Context context;
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
        }
    }
    public FriendPhotoAdapter(ArrayList<String> uids, Context context){
        this.uids = uids;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        ViewHolder holder=  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User.loadProfileImage(context,uids.get(position),((ViewHolder)holder).photo);
    }

    @Override
    public int getItemCount() {
        return uids.size();
    }
}
