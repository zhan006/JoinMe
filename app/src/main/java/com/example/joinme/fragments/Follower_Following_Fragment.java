package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.ManageEventAdapter;
import com.example.joinme.adapter.followFollowingAdapter;
import com.example.joinme.reusableComponent.NavBar;

public class Follower_Following_Fragment extends Fragment {
    private Button follower, following;
    private RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.event_management_activity,container,false);
        follower = v.findViewById(R.id.followers);
        following = v.findViewById(R.id.followings);
        recyclerView = v.findViewById(R.id.friend_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }





}
