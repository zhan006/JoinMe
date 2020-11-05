package com.example.joinme.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.EventAdapter;
import com.example.joinme.adapter.photoAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.interfaces.UserRenderable;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.TitleBar;
import com.example.joinme.utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class visitorProfileFragment extends Fragment implements UserRenderable, EventRenderable {
    //public static final int FRIEND_DISPLAY=3;
    private TextView aboutMe,name,location;
    private ImageView profileImage;
    private Button followButton;

    private ArrayList<String> imageUrls;
    private RecyclerView upcomming_event;

    private User pageUser;
    private TitleBar bar;
    private RecyclerView.Adapter adapter;
    private final String pageUserID;

    public visitorProfileFragment(String uid){
        super();
        this.pageUserID = uid;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));

    }

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.activity_profile,container,false);
        profileImage = view.findViewById(R.id.profile_image);
        aboutMe = view.findViewById(R.id.aboutMe);
        name = view.findViewById(R.id.name);
        Button viewMorePhoto = view.findViewById(R.id.view_more_photo);
        bar = view.findViewById(R.id.profile_title);
        location = view.findViewById(R.id.location);
        followButton = view.findViewById(R.id.edit_profile);

        Button seeFriend = view.findViewById(R.id.see_Friend);
        seeFriend.setVisibility(View.GONE);

        ImageButton addAlbum = view.findViewById(R.id.addAlbum);
        addAlbum.setVisibility(View.GONE);

        RecyclerView friends = view.findViewById(R.id.friend_gallery);
        friends.setVisibility(View.GONE);

        imageUrls = new ArrayList<>();
        //ArrayList<String> followingUids = new ArrayList<>();

        upcomming_event = view.findViewById(R.id.profile_upcomming_event);
        upcomming_event.setLayoutManager(new LinearLayoutManager(this.getContext()));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager lm1 = new LinearLayoutManager(getContext());
        lm1.setOrientation(LinearLayoutManager.HORIZONTAL);

        RecyclerView albums = view.findViewById(R.id.albums);
        albums.setLayoutManager(lm);
        adapter = new photoAdapter(imageUrls,getContext());
        albums.setAdapter(adapter);

        view.findViewById(R.id.friend_view).setVisibility(View.GONE);
        viewMorePhoto.setOnClickListener((v)->{
            Log.d("profile","clicked");
            utils.replaceFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),new Album_fragment(pageUserID),null);
        });
        bar.setBackable(true);
        bar.setOnClickBackListener((v)-> Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack());

        renderEvent();
        renderUser();
        initAlbum();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        followButton.setText("follow");
        followButton.setOnClickListener((v)->{
            follow();
            followButton.setText("Already followed");
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    public void setName(String name){
        this.name.setText(name);
    }
    public void setLocation(String location){
        this.location.setText(location);
    }
    public void setAboutMe(String text){
        aboutMe.setText(text);
    }

    /*
    public void addFriends(int image){
        if(albums.getChildCount()<FRIEND_DISPLAY){
            ImageView v= new ImageView(getContext());
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            v.setImageResource(image);
            albums.addView(v);
        }
    }*/

    //Fetch event information
    @Override
    public void renderEvent() {
        ArrayList<String> eventIDs = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Boolean> map = (HashMap) snapshot.getValue();
                assert map != null;
                Log.d("event",map.toString());
                //Bundle bd = new Bundle();
                for(String k : map.keySet()){
                    if(map.get(k)){eventIDs.add(k);}
                }
                upcomming_event.setAdapter(new EventAdapter(eventIDs));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("AttendingList/"+pageUserID,eventListener);

    }

    //Fetch user information
    @Override
    public void renderUser() {
        User.loadProfileImage(getContext(),pageUserID,profileImage);
        FirebaseAPI.getFirebaseData("User/" + pageUserID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pageUser = snapshot.getValue(User.class);
                assert pageUser != null;
                setName(pageUser.username);
                setAboutMe(pageUser.about);
                if(pageUser.getLocation()!=null){
                    setLocation(pageUser.getLocation().getAddress());
                }

                bar.setTitle(pageUser.username);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Fetch user album
    public void initAlbum(){
        FirebaseAPI.rootRef.child("UserAlbum/"+pageUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String url = snapshot.getValue(String.class);
                imageUrls.add(url);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Function of the follow button
    void follow() {
        String userID = FirebaseAPI.getUser().getUid();
        //String followingUserPath = "FollowingUser/" + pageUserID+ "/" + userID;
        //String userFollowingPath = "UserFollowing/" + userID + "/" + pageUserID;
        Log.d("Follow", userID+"> "+pageUserID);
        FirebaseAPI.rootRef.child("FollowingUser").child(pageUserID).child(userID).setValue(true);
        FirebaseAPI.rootRef.child("UserFollowing").child(userID).child(pageUserID).setValue(true);

    }
}
