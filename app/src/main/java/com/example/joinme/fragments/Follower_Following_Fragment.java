package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
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

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.ManageEventAdapter;
import com.example.joinme.adapter.followFollowingAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Follower_Following_Fragment extends Fragment {
    private Button follower, following;
    private RecyclerView recyclerView;
    private String uid;
    public Follower_Following_Fragment(String uid){
        super();
        this.uid = uid;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.activity_follow_list,container,false);
        follower = v.findViewById(R.id.followers);
        following = v.findViewById(R.id.followings);
        recyclerView = v.findViewById(R.id.friend_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        try{setOnClickListener();}
        catch (Exception e){Log.d("Debug",e.getMessage());};
        follower.performClick();
        TitleBar bar = v.findViewById(R.id.friend_title);
        bar.setOnClickBackListener((view -> {
            getActivity().getSupportFragmentManager().popBackStack();
        }));
        return v;
    }

    public void setOnClickListener() throws Exception {
        if(uid.equals("")) throw new Exception("unknown uid");
        this.follower.setOnClickListener((v)->{
            String followerPath = "FollowingUser/"+uid;
            follower.setSelected(true);
            following.setSelected(false);
            FirebaseAPI.getFirebaseData(followerPath, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,Boolean> map = (HashMap) snapshot.getValue();
                    ArrayList<String> followerList = new ArrayList();

                    if(map!=null){
                        for(String k : map.keySet()){
                            if(map.get(k)){followerList.add(k);}
                        }
                    }
                    Log.d("friend",followerList.toString());
                    recyclerView.setAdapter(new followFollowingAdapter(followerList));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        this.following.setOnClickListener((v)->{
            String followingPath = "UserFollowing/"+uid;
            follower.setSelected(false);
            following.setSelected(true);
            FirebaseAPI.getFirebaseData(followingPath, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,Boolean> map = (HashMap) snapshot.getValue();
                    ArrayList<String> followingList = new ArrayList();
                    if(map!=null){
                        for(String k : map.keySet()){
                            if(map.get(k)){followingList.add(k);}
                        }
                    }
                    Log.d("friend",followingList.toString());
                    recyclerView.setAdapter(new followFollowingAdapter(followingList));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }
}
