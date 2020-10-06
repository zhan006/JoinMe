package com.example.joinme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinme.R;

public class ProfileFragment extends Fragment {
    public static final int ALBUM_DISPLAY=3, FRIEND_DISPLAY=3;
    private TextView aboutMe,name,location;
    private ImageButton addAlbum;
    LinearLayout friendGallery,albums;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.activity_profile,container,false);
        aboutMe = view.findViewById(R.id.aboutMe);
        friendGallery = view.findViewById(R.id.friend_gallery);
        albums = view.findViewById(R.id.albums);
        addAlbum=view.findViewById(R.id.addAlbum);
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        addAlbum.setOnClickListener((v)->{
            addAlbum(R.drawable.default_icon);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addAlbum(R.drawable.default_icon);
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
        if(albums.getChildCount()<ALBUM_DISPLAY){
            ImageView v= new ImageView(getContext());
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            v.setImageResource(image);
            albums.addView(v);
        }
    }
    public void addFriends(int image){
        if(albums.getChildCount()<FRIEND_DISPLAY){
            ImageView v= new ImageView(getContext());
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            v.setImageResource(image);
            albums.addView(v);
        }
    }
}
