package com.example.joinme.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.EventAdapter;
import com.example.joinme.adapter.photoAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.interfaces.UserRenderable;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements UserRenderable, EventRenderable {
    public static final int ALBUM_DISPLAY=3, FRIEND_DISPLAY=3, UPDATE_MSG=0,GET_EVENT=1;
    private TextView aboutMe,name,location;
    private ImageButton addAlbum;
    private ImageView profileImage, placeIcon;
    private Button editProfile,seeFriend;
    private ArrayList<Event> eventList;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private RecyclerView upcomming_event,albums;
    private User user;
    private Uri imageUri;
    private final int PHOTO = 1;
    LinearLayout friendGallery;
    private static final String TAG = "ProfileFragment";
    private String currentUID;



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar)getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));

    }

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.activity_profile,container,false);
        profileImage = view.findViewById(R.id.profile_image);
        placeIcon = view.findViewById(R.id.place_icon);
        currentUID = ((MainActivity) getActivity()).getUid();
        aboutMe = view.findViewById(R.id.aboutMe);
        friendGallery = view.findViewById(R.id.friend_gallery);
        albums = view.findViewById(R.id.albums);
        addAlbum=view.findViewById(R.id.addAlbum);
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        editProfile = view.findViewById(R.id.edit_profile);
        seeFriend = view.findViewById(R.id.see_Friend);
        addAlbum.setOnClickListener((v)->{
            addAlbum(R.drawable.default_icon);
        });
        upcomming_event = view.findViewById(R.id.profile_upcomming_event);
        upcomming_event.setLayoutManager(new LinearLayoutManager(this.getContext()));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        albums.setLayoutManager(lm);
        renderEvent();
        renderUser();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String uid = ((MainActivity)getActivity()).getUid();
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
            case PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                        images.add(bitmap);
                        albums.setAdapter(new photoAdapter(images));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            break;
        }
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
    }
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
            loadProfileImage(getActivity(), currentUID, profileImage);
            if(user.getLocation()!=null){
                location.setText(user.getLocation().getAddress());
            }

        }
    }



    public void loadProfileImage(Context context, String uid, ImageView imageView) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("profileImage")) {
                    String image = snapshot.child("profileImage").getValue().toString();
                    if (!image.equals("null")) {
                        //display image from the url in real time database for user profile image
                        Glide.with(context).load(image).into(imageView);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("User/"+uid, valueEventListener);
    }

}
