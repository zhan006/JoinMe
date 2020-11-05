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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.joinme.objects.User;
import com.example.joinme.utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//This fragment is to show user profile to visitors.
//Comparing to user's own profile page, some private information is not shown.
public class visitorProfileFragment extends Fragment implements UserRenderable{
    public static final int FRIEND_DISPLAY=3;

    //Main components
    private TextView aboutMe,name,location;
    private ImageView profileImage;
    private Button followButton;

    private ArrayList<String> imageUrls;
    //private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    //private User user;
    //private User pageUser;

    //private RecyclerView upcoming_event;
    private RecyclerView albums;
    private RecyclerView.Adapter adapter;
    private final String pageUserID;
    //RecyclerView friendGallery;
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
        ImageButton addAlbum = view.findViewById(R.id.addAlbum);
        name = view.findViewById(R.id.name);
        Button viewMorePhoto = view.findViewById(R.id.view_more_photo);
        location = view.findViewById(R.id.location);
        followButton = view.findViewById(R.id.edit_profile);
        Button seeFriend = view.findViewById(R.id.see_Friend);
        seeFriend.setVisibility(View.GONE);
        RecyclerView friends = view.findViewById(R.id.friend_gallery);
        RelativeLayout friend_view = view.findViewById(R.id.friend_view);
        friend_view.setVisibility(View.GONE);
        addAlbum.setVisibility(View.GONE);
        imageUrls = new ArrayList<>();
        ArrayList<String> followingUids = new ArrayList<>();
        //friendGallery = view.findViewById(R.id.friend_gallery);
        RecyclerView profile_upcomming_event= view.findViewById(R.id.profile_upcomming_event);
        //profile_upcomming_event.setVisibility(View.GONE);
        RelativeLayout text_upcoming_event= view.findViewById(R.id.text_upcoming_event);
        //text_upcoming_event.setVisibility(View.GONE);
        //upcoming_event = view.findViewById(R.id.profile_upcomming_event);
        //upcoming_event.setLayoutManager(new LinearLayoutManager(this.getContext()));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager lm1 = new LinearLayoutManager(getContext());
        lm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        albums.setLayoutManager(lm);
        friends.setLayoutManager(lm1);
        adapter = new photoAdapter(imageUrls,getContext());
        RecyclerView.Adapter friendAdapter = new FriendPhotoAdapter(followingUids, getContext());
        albums.setAdapter(adapter);
        friends.setAdapter(friendAdapter);
        //TextView albumText = view.findViewById(R.id.album_text);
        viewMorePhoto.setOnClickListener((v)->{
            Log.d("profile","clicked");
            utils.replaceFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),new Album_fragment(pageUserID),null);
        });
        //renderEvent();
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
    public void addAlbum(int image){
        File output = new File(getContext().getExternalCacheDir(),"output.jpg");
        try{
            if(output.exists()){
                output.delete();
            }
            output.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT>=24){
            imageUri = FileProvider.getUriForFile(getActivity(),"com.example.joinme.fileprovider",output);
        }
        else{imageUri = Uri.fromFile(output);}

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,1);
    }*/
    public void addFriends(int image){
        if(albums.getChildCount()<FRIEND_DISPLAY){
            ImageView v= new ImageView(getContext());
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            v.setImageResource(image);
            albums.addView(v);
        }
    }
    public List<Event> initDummyEvents(){
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(new Event("Hang out together","38 Little Lonsdale",new DateTime()));
        events.add(new Event("Eat dinner","1 Bouverie",new DateTime()));
        events.add(new Event("League of Legends","netfish cafe",new DateTime()));
        return events;
    }

    /*
    public ArrayList<Event> getParentEventList(){
        return ((MainActivity) Objects.requireNonNull(getActivity())).getEventList();
    }*/

    /*
    public User getParentUser(){
        return ((MainActivity) Objects.requireNonNull(getActivity())).getUser();
    }*/

    /*
    @Override
    public void renderEvent() {
        ArrayList<Event> eventList = getParentEventList();

        if(eventList !=null){
            upcoming_event.setAdapter(new EventAdapter(eventList));
        }
    }*/

    @Override
    public void renderUser() {
        User.loadProfileImage(getActivity(), pageUserID, profileImage);
        FirebaseAPI.getFirebaseData("User/" + pageUserID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("username")) {
                    setName(snapshot.child("username").getValue(String.class));
                }
                if (snapshot.hasChild("about")) {
                    setAboutMe(snapshot.child("about").getValue(String.class));
                }
                if (snapshot.hasChild("location")) {
                    setLocation(snapshot.child("location").child("address").getValue(String.class));
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
        //String followingUserPath = "FollowingUser/" + pageUserID+ "/" + userID;
        //String userFollowingPath = "UserFollowing/" + userID + "/" + pageUserID;
        Log.d("Follow", userID+"> "+pageUserID);
        FirebaseAPI.rootRef.child("FollowingUser").child(pageUserID).child(userID).setValue(true);
        FirebaseAPI.rootRef.child("UserFollowing").child(userID).child(pageUserID).setValue(true);

    }
}
