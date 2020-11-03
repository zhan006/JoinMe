package com.example.joinme.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.activity.ChatActivity;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.fragments.DiscoverEventFragment;
import com.example.joinme.fragments.Follower_Following_Fragment;
import com.example.joinme.fragments.visitorProfileFragment;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class followFollowingAdapter extends RecyclerView.Adapter {
    private ArrayList<String> uids;
    public Follower_Following_Fragment fragment;
    static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView name,bio;
        public ImageButton message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.host_profile_photo);
            name = itemView.findViewById(R.id.user_name);
            bio = itemView.findViewById(R.id.user_about);
            message = itemView.findViewById(R.id.message_icon);
        }
    }

    public followFollowingAdapter(ArrayList<String> uids,Follower_Following_Fragment fragment){
        this.uids = uids;
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friend_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String uid = this.uids.get(position);
        String path = "User/"+uid;
        FirebaseAPI.getFirebaseData(path, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ((ViewHolder)holder).name.setText(user.getUsername());
                ((ViewHolder)holder).bio.setText(user.getAbout());
                ((ViewHolder)holder).message.setOnClickListener((v)->{
                    // jump to chat activity
                    Intent chatActivity = new Intent(v.getContext(), ChatActivity.class);
                    // Testing purpose
                    chatActivity.putExtra("friendUid", uid);
                    chatActivity.putExtra("friendUsername", user.getUsername());
                    v.getContext().startActivity(chatActivity);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = fragment.getActivity().getSupportFragmentManager();
                utils.replaceFragment(fm, new visitorProfileFragment(uid), "visitorProfile");
            }
        });
    }

    @Override
    public int getItemCount() {
        return uids.size();
    }
}

