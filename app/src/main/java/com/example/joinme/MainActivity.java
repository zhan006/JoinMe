package com.example.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.fragments.HomePageFragment;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.interfaces.UserRenderable;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String uid;
    private ArrayList<Event> eventList;
    private final int GET_USER=0,GET_EVENT=1;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bd = msg.getData();
            switch (msg.what){
                case GET_USER:
                    user = (User)bd.getSerializable("user");
                    Fragment cFragment = getCurrentFragment();
                    if(cFragment instanceof UserRenderable){
                        ((UserRenderable) cFragment).renderUser();
                    }
                    break;
                case GET_EVENT:
                    ArrayList<String> ids = bd.getStringArrayList("ids");
                    new Thread(()->{
                        ValueEventListener listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap map = (HashMap)snapshot.getValue();
                                eventList = new ArrayList<Event>();
                                for(String id:ids){
                                    Event event = new Event((HashMap) map.get(id));
                                    eventList.add(event);
                                    Fragment cFragment = getCurrentFragment();
                                    if(cFragment instanceof EventRenderable){
                                        ((EventRenderable) cFragment).renderEvent();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        FirebaseAPI.getFirebaseDataOnce("Event",listener);
                    }).start();




            }
        }
    };


    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        FragmentManager fm = getSupportFragmentManager();
        if(actionbar != null) actionbar.hide();
        NavBar nav = findViewById(R.id.navbar);
        fm.beginTransaction().replace(R.id.main_fragment_container,new HomePageFragment(),"home").commit();
        nav.setSelectedItem(R.id.tab_home);
        uid = "qa6KACdJ0RYZfVDXLtpKL2HcxJ43";
        new Thread(getUserProfile).start();
        new Thread(getAttendingList).start();

        // test the UID transformation from loginActivity
        Intent intent = getIntent();
        String currentUID = intent.getStringExtra("UID");
        Toast.makeText(MainActivity.this, "current UID: " + currentUID,
                Toast.LENGTH_SHORT).show();

        /*
         * Firebase testing demo
         */

        ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //String username = dataSnapshot.getValue(String.class);
                HashMap data = (HashMap)dataSnapshot.getValue();
                Log.d(TAG, "onDataChange: username = "+data.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        //FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43/username", userListener);
        FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43",userListener);
        FirebaseAPI.setFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43/username", "Giovana")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
    private Runnable getUserProfile = ()-> {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //String username = dataSnapshot.getValue(String.class);
                User user =  dataSnapshot.getValue(User.class);
                Message msg = new Message();
                msg.what = GET_USER;
                Bundle bd = new Bundle();
                bd.putSerializable("user",user);
                Log.d("user",user.toString());
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

        FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43", userListener);
    };
    private Runnable getAttendingList = ()->{
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap map = (HashMap) snapshot.getValue();
                Bundle bd = new Bundle();
                bd.putStringArrayList("ids",new ArrayList(map.keySet()));
                Message msg = new Message();
                msg.what = GET_EVENT;
                msg.setData(bd);
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("AttendingList/qa6KACdJ0RYZfVDXLtpKL2HcxJ43", eventListener);
    };
    public User getUser(){
        return user;
    }
    public String getUid(){return uid;}
    public ArrayList<Event> getEventList(){return eventList;}
    public Fragment getCurrentFragment(){
        List<Fragment> ls = getSupportFragmentManager().getFragments();
        if(ls !=null){
            for(Fragment fragment:ls){
                if(fragment.isVisible()){
                    return fragment;
                }
            }
        }
        return null;

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStackImmediate();
            getFragmentManager().beginTransaction().commit();
        } else {
            super.onBackPressed();
        }
    }
}