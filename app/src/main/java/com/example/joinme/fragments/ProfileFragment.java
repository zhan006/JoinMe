package com.example.joinme.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.EventAdapter;
import com.example.joinme.objects.Event;
import com.example.joinme.reusableComponent.NavBar;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final int ALBUM_DISPLAY=3, FRIEND_DISPLAY=3;
    private TextView aboutMe,name,location;
    private ImageButton addAlbum;
    LinearLayout friendGallery,albums;

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
        aboutMe = view.findViewById(R.id.aboutMe);
        friendGallery = view.findViewById(R.id.friend_gallery);
        albums = view.findViewById(R.id.albums);
        addAlbum=view.findViewById(R.id.addAlbum);
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        addAlbum.setOnClickListener((v)->{
            addAlbum(R.drawable.default_icon);
        });
        RecyclerView recyclerView = view.findViewById(R.id.profile_upcomming_event);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new EventAdapter(initDummyEvents()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addAlbum(R.drawable.default_icon);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
    public List<Event> initDummyEvents(){
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(new Event("Hang out together","38 Little Lonsdale","Today"));
        events.add(new Event("Eat dinner","1 Bouverie","Afternoon"));
        events.add(new Event("League of Legends","netfish cafe","evening"));
        return events;
    }
}
