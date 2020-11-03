package com.example.joinme.fragments;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.LoginActivity;
import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.NotificationAdapter;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.interfaces.UserRenderable;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.TitleBar;
import com.example.joinme.utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;


public class HomePageFragment extends Fragment implements UserRenderable, EventRenderable {
    private ImageView icon;
    private TextView welcome;
    private Button organise, search, signout;
    private NotificationAdapter adapter;
    private ArrayList<Event> eventList;
    private RecyclerView board;
    private User user;
    private String currentUID;
    private Event event;
    private TitleBar bar;
    private Geocoder geocoder;
    private final String WELCOME = "Welcome ";
    private final int UPDATE_MSG = 1, UPDATE_EVENTS = 2, GET_EVENT_DETAIL = 3;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.activity_home, container, false);
        icon = view.findViewById(R.id.icon);
        welcome = view.findViewById(R.id.welcome_name);
        organise = view.findViewById(R.id.organize_event);
        search = view.findViewById(R.id.search_event);
        board = view.findViewById(R.id.home_billboard);
        board.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = getParentEventList();
        currentUID = ((MainActivity) getActivity()).getUid();
        bar = view.findViewById(R.id.home_title);

        renderEvent();
        renderUser();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("fragment", String.valueOf(getActivity().getSupportFragmentManager().getFragments().get(0).isVisible()));
        organise.setOnClickListener((v) -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            utils.replaceFragment(fm, new PublishEvent(), "publish_event");
        });
        // for testing location service;
        search.setOnClickListener((v) -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            utils.replaceFragment(fm, new DiscoverEventFragment(), "discover_event");
        });

        // test signout service
        bar.setIconListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            // [START config_signin]
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // [END config_signin]
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            if (mGoogleSignInClient != null){
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            }
            Intent intentSignin = new Intent(getActivity(), LoginActivity.class);
            startActivity(intentSignin);
        });

    }
    public ArrayList<Event> getParentEventList(){
        return ((MainActivity)getActivity()).getEventList();
    }
    public User getParentUser(){
        return ((MainActivity)getActivity()).getUser();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void renderEvent() {
        eventList = getParentEventList();
        if(eventList!=null){
            eventList.sort((event, t1) -> event.getDatetime().compareTo(t1.getDatetime()));
            eventList.removeIf((e)-> Calendar.getInstance().getTimeInMillis()>e.getDatetime().getTimeStamp());
            board.setAdapter(new NotificationAdapter(eventList));
        }
    }

    @Override
    public void renderUser() {
        user = getParentUser();
        if(user !=null){
            welcome.setText(WELCOME+user.firstName+" "+user.lastName);
//          Connect to database and retrieve the profile image from database under "User"
            user.loadProfileImage(getActivity(), currentUID,icon);

        }

    }
}


