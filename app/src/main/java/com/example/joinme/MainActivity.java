package com.example.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.example.joinme.interfaces.LocationRenderable;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String uid;
    private ArrayList<Event> eventList;
    private final int GET_USER = 0, GET_EVENT = 1;
    public LocationManager locationManager;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bd = msg.getData();
            switch (msg.what) {
                case GET_USER:
                    user = (User) bd.getSerializable("user");
                    Fragment cFragment = getCurrentFragment();
                    if (cFragment instanceof UserRenderable) {
                        ((UserRenderable) cFragment).renderUser();
                    }
                    break;
                case GET_EVENT:
                    ArrayList<String> ids = bd.getStringArrayList("ids");
                    new Thread(() -> {
                        ValueEventListener listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                snapshot.getChildren();
                                HashMap map = (HashMap) snapshot.getValue();
                                eventList = new ArrayList<Event>();
                                for (String id : ids) {

                                    Event event = new Event((HashMap)map.get(id));
                                    eventList.add(event);
                                }
                                Log.d("MainActivity", eventList.toString());
                                Fragment cFragment = getCurrentFragment();
                                if (cFragment instanceof EventRenderable) {
                                    ((EventRenderable) cFragment).renderEvent();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        FirebaseAPI.getFirebaseDataOnce("Event", listener);
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
        if (actionbar != null) actionbar.hide();
        NavBar nav = findViewById(R.id.navbar);
        fm.beginTransaction().replace(R.id.main_fragment_container, new HomePageFragment(), "home").commit();
        nav.setSelectedItem(R.id.tab_home);
        getCurrentLocation();
        loadUserProfile();
        loadAttendingList();

        // test the UID transformation from loginActivity
        Intent intent = getIntent();
        String currentUID = intent.getStringExtra("UID");
//        this should be used after implementing insert new user into the database
        uid = currentUID;
        Toast.makeText(MainActivity.this, "current UID: " + currentUID,
                Toast.LENGTH_SHORT).show();
    }
    public void loadUserProfile(){
        new Thread(getUserProfile).start();
    }
    public void loadAttendingList(){
        new Thread(getAttendingList).start();
    }

    private Runnable getUserProfile = () -> {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //String username = dataSnapshot.getValue(String.class);
                User user = dataSnapshot.getValue(User.class);
                Message msg = new Message();
                msg.what = GET_USER;
                Bundle bd = new Bundle();
                bd.putSerializable("user", user);
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

        FirebaseAPI.getFirebaseData("User/"+uid, userListener);
    };
    private Runnable getAttendingList = () -> {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Boolean> map = (HashMap) snapshot.getValue();
//                Log.d("event",map.toString());
                Bundle bd = new Bundle();
                ArrayList attendingList = new ArrayList();
                if(map!=null){
                    for(String k : map.keySet()){
                        if(map.get(k)){attendingList.add(k);}
                    }
                    Log.d("event",attendingList.toString());
                }

                bd.putStringArrayList("ids", attendingList);
                Message msg = new Message();
                msg.what = GET_EVENT;
                msg.setData(bd);
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("AttendingList/"+uid, eventListener);
    };

    public User getUser() {
        return user;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public Fragment getCurrentFragment() {
        List<Fragment> ls = getSupportFragmentManager().getFragments();
        if (ls != null) {
            for (Fragment fragment : ls) {
                if (fragment.isVisible()) {
                    return fragment;
                }
            }
        }
        return null;

    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
            getFragmentManager().beginTransaction().commit();
        } else {
            super.onBackPressed();
        }
    }

    public void getCurrentLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
        }
        else{
            LocationListener listener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("location", location.getLatitude()+"");
                    Fragment f = getCurrentFragment();
                    if(f instanceof LocationRenderable){
                        ((LocationRenderable)f).initEvents();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10,
                    listener);
        }
    }
}