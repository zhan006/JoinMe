package com.example.joinme.fragments;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.NotificationAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.interfaces.UserRenderable;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;


public class HomePageFragment extends Fragment implements UserRenderable, EventRenderable {
    private ImageView icon;
    private TextView welcome;
    private Button organise, search;
    private NotificationAdapter adapter;
    private ArrayList<Event> eventList;
    private RecyclerView board;
    private User user;
    private Event event;
    private final String WELCOME = "Welcome ";
    private final int UPDATE_MSG = 1,UPDATE_EVENTS=2,GET_EVENT_DETAIL=3;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.activity_home,container,false);
        icon = view.findViewById(R.id.icon);
        welcome = view.findViewById(R.id.welcome_name);
        organise = view.findViewById(R.id.organize_event);
        search = view.findViewById(R.id.search_event);
        board = view.findViewById(R.id.home_billboard);
        board.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = getParentEventList();
        renderEvent();
        renderUser();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("fragment", String.valueOf(getActivity().getSupportFragmentManager().getFragments().get(0).isVisible()));
        organise.setOnClickListener((v)->{
            FragmentManager fm = getActivity().getSupportFragmentManager();
            utils.replaceFragment(fm,new PublishEvent(),"publish_event");
        });
        search.setOnClickListener((v)->{
            Log.d("fragment", ((MainActivity)getActivity()).getCurrentFragment().getTag());
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
            board.setAdapter(new NotificationAdapter(eventList));
        }
    }

    @Override
    public void renderUser() {
        user = getParentUser();
        if(user !=null){
            welcome.setText(WELCOME+user.firstName+" "+user.lastName);
            icon.setImageResource(R.drawable.photo);
        }

    }
}


