package com.example.joinme.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.EventAdapter;
import com.example.joinme.adapter.FriendPhotoAdapter;
import com.example.joinme.adapter.photoAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.interfaces.UserRenderable;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.Time;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.example.joinme.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class visitorProfileFragment extends Fragment implements UserRenderable, EventRenderable {
    public static final int FRIEND_DISPLAY=3;
    public static final int TAKE_PHOTO=1;
    private TextView aboutMe,name,location;
    private ImageButton addAlbum;
    private ImageView profileImage;
    private Button followButton,seeFriend,viewMorePhoto;
    private ArrayList<Event> eventList;
    private ArrayList<String> imageUrls;
    private ArrayList<String> followingUids;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private RecyclerView upcomming_event,albums,friends;
    private User user;
    private User pageUser;
    private TitleBar bar;
    private RecyclerView.Adapter adapter,friendAdapter;
    private String pageUserID;
    private final int PHOTO = 1;
    RecyclerView friendGallery;
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
        albums = view.findViewById(R.id.albums);
        addAlbum=view.findViewById(R.id.addAlbum);
        name = view.findViewById(R.id.name);
        viewMorePhoto = view.findViewById(R.id.view_more_photo);
        bar = view.findViewById(R.id.profile_title);
        location = view.findViewById(R.id.location);
        followButton = view.findViewById(R.id.edit_profile);
        seeFriend = view.findViewById(R.id.see_Friend);
        seeFriend.setVisibility(View.GONE);
        friends = view.findViewById(R.id.friend_gallery);
        addAlbum.setVisibility(View.GONE);
        imageUrls = new ArrayList<>();
        followingUids = new ArrayList<>();
        friendGallery = view.findViewById(R.id.friend_gallery);
        upcomming_event = view.findViewById(R.id.profile_upcomming_event);
        upcomming_event.setLayoutManager(new LinearLayoutManager(this.getContext()));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager lm1 = new LinearLayoutManager(getContext());
        lm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        albums.setLayoutManager(lm);
        friends.setLayoutManager(lm1);
        adapter = new photoAdapter(imageUrls,getContext());
        friendAdapter = new FriendPhotoAdapter(followingUids,getContext());
        albums.setAdapter(adapter);
        friends.setAdapter(friendAdapter);
        TextView albumText = view.findViewById(R.id.album_text);
        viewMorePhoto.setOnClickListener((v)->{
            Log.d("profile","clicked");
            utils.replaceFragment(getActivity().getSupportFragmentManager(),new Album_fragment(pageUserID),null);
        });
        bar.setBackable(true);
        bar.setOnClickBackListener((v)->{
            getActivity().getSupportFragmentManager().popBackStack();
        });
        renderEvent();
        renderUser();
        initAlbum();
        initFriends(1);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        followButton.setText("follow");
        isMyFollowing();
        followButton.setOnClickListener((v)->{
            if(followButton.getText().equals("follow")) follow();
            else unfollow();
            followButton.setText("Already followed");
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    private void isMyFollowing(){
        String muid = ((MainActivity)getActivity()).getUid();
        FirebaseAPI.getFirebaseData("UserFollowing/" + muid, new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Boolean> map = (HashMap)snapshot.getValue();
                if(map.getOrDefault(pageUserID,false)) followButton.setText("Already followed");
                else followButton.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }
    void unfollow(){
        String uid = ((MainActivity)getActivity()).getUid();
        FirebaseAPI.rootRef.child("UserFollowing").child(uid).child(pageUserID).setValue(false);
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

    @Override
    public void renderEvent() {
        ArrayList<String> eventIDs = new ArrayList<String>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Boolean> map = (HashMap) snapshot.getValue();
                if(map!=null){
                    for(String k : map.keySet()){
                        if(map.get(k)){eventIDs.add(k);}
                    }
                }
                if(eventIDs!=null){
                    upcomming_event.setAdapter(new EventAdapter(eventIDs));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("AttendingList/"+pageUserID,eventListener);

    }

    @Override
    public void renderUser() {
        User.loadProfileImage(getContext(),pageUserID,profileImage);
        FirebaseAPI.getFirebaseData("User/" + pageUserID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pageUser = snapshot.getValue(User.class);
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
    public void initFriends(int image){
        FirebaseAPI.rootRef.child("FollowingUser").child(pageUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Boolean> map = (HashMap<String, Boolean>) snapshot.getValue();
                if(map!=null){
                    for(String id:map.keySet()){
                        if(map.get(id)){
                            followingUids.add(id);
                            friendAdapter.notifyDataSetChanged();
                            Log.d("Profile","getFollowing=>"+followingUids.toString());
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

    void follow() {
        String userID = FirebaseAPI.getUser().getUid();
        String followingUserPath = "FollowingUser/" + pageUserID+ "/" + userID;
        String userFollowingPath = "UserFollowing/" + userID + "/" + pageUserID;

        Log.d("Follow", userID+"> "+pageUserID);
        FirebaseAPI.rootRef.child("FollowingUser").child(pageUserID).child(userID).setValue(true);
        FirebaseAPI.rootRef.child("UserFollowing").child(userID).child(pageUserID).setValue(true);

    }
}
