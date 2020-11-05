package com.example.joinme.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements UserRenderable, EventRenderable {
    public static final int FRIEND_DISPLAY=3, TAKE_PHOTO=1;
    private static final String TAG = "ProfileFragment";
    private TextView aboutMe,name,location;
    private ImageButton addAlbum;
    private ImageView profileImage;
    private Button editProfile,seeFriend,viewMorePhoto;
    private ArrayList<Event> eventList;
    private ArrayList<String> imageUrls;
    private ArrayList<String> followingUids;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private RecyclerView upcomming_event,albums,friends;
    private User user;
    private String uid;
    private RecyclerView.Adapter adapter,friendAdapter;
    LinearLayout albumContainer;
    private Uri imageUri;
    private final int PHOTO = 1;
    LinearLayout friendGallery;
    private User hostUser;

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
        uid = ((MainActivity)getActivity()).getUid();

        profileImage = view.findViewById(R.id.profile_image);
        aboutMe = view.findViewById(R.id.aboutMe);
        albums = view.findViewById(R.id.albums);
        addAlbum=view.findViewById(R.id.addAlbum);
        name = view.findViewById(R.id.name);
        viewMorePhoto = view.findViewById(R.id.view_more_photo);
        location = view.findViewById(R.id.location);
        editProfile = view.findViewById(R.id.edit_profile);
        seeFriend = view.findViewById(R.id.see_Friend);
        friends = view.findViewById(R.id.friend_gallery);
        addAlbum.setOnClickListener((v)->{
            addAlbum(R.drawable.default_icon);
        });
        imageUrls = new ArrayList<String>();
        followingUids = new ArrayList<String>();
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
            Log.d("profile",uid);

            utils.replaceFragment(getActivity().getSupportFragmentManager(),new Album_fragment(uid),null);
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

        editProfile.setOnClickListener((v)->{
            EditProfileFragment f = new EditProfileFragment();
            Bundle bd = new Bundle();
            bd.putSerializable("user",getParentUser());
            f.setArguments(bd);
            utils.replaceFragment(getActivity().getSupportFragmentManager(),f,"edit_profile");

        });
        seeFriend.setOnClickListener((v)->{
            utils.replaceFragment(getActivity().getSupportFragmentManager(),new Follower_Following_Fragment(uid),"follower");
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Calendar now = Calendar.getInstance();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    images.add(imageBitmap);
                    final StorageReference filepath = FirebaseAPI.getStorageRef(
                            "album/" +uid+"/"+now.getTimeInMillis());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
//                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(bytes);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // save storage url to realtime database
                    Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUri = task.getResult().toString();
                                FirebaseAPI.rootRef.child("UserAlbum").child(uid).child(String.valueOf(now.getTimeInMillis())).setValue(downloadUri);
                            }
                        }
                    });
                }
            break;
        }
    }
    public void initAlbum(){
        FirebaseAPI.rootRef.child("UserAlbum/"+uid).addChildEventListener(new ChildEventListener() {
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

    public void setName(String name){
        this.name.setText(name);
    }
    public void setLocation(String location){
        this.location.setText(location);
    }
    public void setAboutMe(String text){
        aboutMe.setText(text);
    }
    public void addAlbum(int image){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, TAKE_PHOTO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(),"Activity not found",Toast.LENGTH_SHORT);
            // display error state to the user
        }
//        File output = new File(getContext().getExternalCacheDir(),"output.jpg");
//        try{
//            if(output.exists()){
//                output.delete();
//            }
//            output.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(Build.VERSION.SDK_INT>=24){
//            imageUri = FileProvider.getUriForFile(getActivity(),"com.example.joinme.fileprovider",output);
//        }
//        else{imageUri = Uri.fromFile(output);}
//
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//        startActivityForResult(intent,1);
    }
    public void initFriends(int image){
        FirebaseAPI.rootRef.child("FollowingUser").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Boolean> map = (HashMap<String, Boolean>) snapshot.getValue();
                for(String id:map.keySet()){
                    if(map.get(id)){
                        followingUids.add(id);
                        friendAdapter.notifyDataSetChanged();
                        Log.d("Profile","getFollowing=>"+followingUids.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<Event> getParentEventList(){
        return ((MainActivity)getActivity()).getEventList();
    }
    public User getParentUser(){
        return ((MainActivity)getActivity()).getUser();
    }
    @Override
    public void renderEvent() {
        eventList = getParentEventList();
        if(eventList!=null){
            upcomming_event.setAdapter(new EventAdapter(eventList));
        }
    }

    @Override
    public void renderUser() {
        user = getParentUser();
        if(user !=null){
            setName(user.username);
            setAboutMe(user.about);

            user.loadProfileImage(getActivity(), uid, profileImage);
            if(user.getLocation()!=null){
                location.setText(user.getLocation().getAddress());
            }

        }

    }
    /*
    public void setUser(User newUser) {
        if(newUser != null){
            user = newUser;
            setName(user.username);
            setAboutMe(user.about);
            if(user.getLocation()!=null){
                location.setText(user.getLocation().getAddress());
            }
            if(!(user.getUsername().equals(getParentUser().username))){
                visitorMode = true;
            }
        }
        if(visitorMode){
            editProfile.setText("Follow");
        }

    }*/
    void follow(String pageUserId) {
        String userID = FirebaseAPI.getUser().getUid();
        String followingUserPath = "FollowingUser/" + pageUserId+ "/" + userID;
        String userFollowingPath = "UserFollowing/" + userID + "/" + pageUserId;

        // get unique message ID for current message

        // create current message

        Log.d("Follow", userID+"> "+pageUserId);
        FirebaseAPI.rootRef.child("FollowingUser").child(pageUserId).child(userID).setValue(true);
        FirebaseAPI.rootRef.child("UserFollowing").child(userID).child(pageUserId).setValue(true);

    }
}
