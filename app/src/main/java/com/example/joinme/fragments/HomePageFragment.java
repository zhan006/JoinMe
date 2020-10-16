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

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.example.joinme.utils;
import com.example.joinme.workers.DownloadingTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomePageFragment extends Fragment {
    private ImageView icon;
    private TextView welcome;
    private Button organise, search;
    private Set attendingList;
    private LinearLayout board;
    private final String WELCOME = "Welcome ";
    private final int UPDATE_MSG = 1,UPDATE_EVENTS=2,GET_EVENT_DETAIL=3;
    private final String FIRSTNAME="firstName",LASTNAME="lastName",LOCATION="location",
    ABOUT = "about";
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bd = msg.getData();
            switch (msg.what){
                case UPDATE_MSG:
                    welcome.setText(WELCOME+bd.getString(FIRSTNAME)+" "+bd.getString(LASTNAME));
                    icon.setImageResource(R.drawable.photo);
                    break;
                case UPDATE_EVENTS:
                    ArrayList<String> attendingList = bd.getStringArrayList("att");
                    ExecutorService pool = Executors.newFixedThreadPool(5);
                    for(String s:attendingList){
                        pool.execute(new DownloadingTask(handler,"Event/"+s,downloadEventListner));
                    }
                    pool.shutdown();
                    Log.d("data",attendingList.toString());
                    break;
                case GET_EVENT_DETAIL:
                    HashMap event = (HashMap) bd.getSerializable("event");
                    TextView v = new TextView(getContext());
                    v.setText((String)event.get("name"));
                    board.addView(v);
                    Log.d("event",event.toString());

            }
        }
    };
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
        board = view.findViewById(R.id.board);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        organise.setOnClickListener((v)->{
            FragmentManager fm = getActivity().getSupportFragmentManager();
            utils.replaceFragment(fm,new PublishEvent(),"publish_event");
        });
        new Thread(getUserProfile).start();
        new Thread(getAttendingList).start();

    }
    private Runnable getUserProfile = ()-> {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //String username = dataSnapshot.getValue(String.class);
                HashMap data = (HashMap) dataSnapshot.getValue();
                Message msg = new Message();
                msg.what = UPDATE_MSG;
                Bundle bd = new Bundle();
                bd.putString(FIRSTNAME,(String)data.get(FIRSTNAME));
                bd.putString(LASTNAME,(String)data.get(LASTNAME));
                bd.putString("profileImage",(String)data.get("profileImage"));
                bd.putString("username",(String)data.get("username"));
                bd.putString(ABOUT,(String)data.get(ABOUT));
                bd.putString(LOCATION,(String)data.get(LOCATION));
                msg.setData(bd);
                handler.sendMessage(msg);
                //Log.d(TAG, "onDataChange: username = "+username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("home", "loadPost:onCancelled", databaseError.toException());
            }
        };

        //FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43/username", userListener);
        FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43", userListener);
    };
    private Runnable getAttendingList = ()->{
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap map = (HashMap) snapshot.getValue();
                Bundle bd = new Bundle();
                bd.putStringArrayList("att",new ArrayList(map.keySet()));
                Message msg = new Message();
                msg.what = UPDATE_EVENTS;
                msg.setData(bd);
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("AttendingList/qa6KACdJ0RYZfVDXLtpKL2HcxJ43", eventListener);
    };
    private ValueEventListener downloadEventListner = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            HashMap map = (HashMap) snapshot.getValue();
            Bundle bd = new Bundle();
            bd.putSerializable("event",map);
            Message msg = new Message();
            msg.what = GET_EVENT_DETAIL;
            msg.setData(bd);
            handler.sendMessage(msg);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}


