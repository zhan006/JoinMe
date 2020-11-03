package com.example.joinme.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.DiscoverConditionAdapter;
import com.example.joinme.adapter.DiscoverEventAdapter;

import com.example.joinme.adapter.ManageEventAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.objects.location;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class  DiscoverEventFragment extends Fragment {

    public RecyclerView eventRecyclerView;
    private Button study,entertainment, dailyLife;
    private Button distance1,distance2,distance3;
    private Button oneDay, oneWeek, oneMonth;
    private String topic = "";
    private int distanceLimit = Integer.MAX_VALUE;
    private String date="";
    private ArrayList<Event> events = new ArrayList<>();
    private Editable target;
    public Location curLoc;
    public TitleBar bar;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar) getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));

    }
    public DiscoverEventFragment(){
        super();
    }
    public DiscoverEventFragment(Editable t){
        super();
        target=t;
//        search();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.discover_events, container, false);
        EditText searchBar = v.findViewById(R.id.search_text);
        searchBar.setHint("search by name or ID");
        bar = v.findViewById(R.id.search_event_title);
        bar.setOnClickBackListener((view)->{
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        });
        ImageButton searchButton = v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(v1 -> {
            target = searchBar.getText();
            search();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        eventRecyclerView = v.findViewById(R.id.discover_events_recycle);
        eventRecyclerView.setLayoutManager(layoutManager);
        initEvents();
        eventRecyclerView.setAdapter(new DiscoverEventAdapter(events,curLocation()));
        initButtons(v);
        if(target!=null&&!target.toString().equals(""))
            search();
        return v;
    }
    private void initButtons(View v){
        study = v.findViewById( R.id.study_button);
        entertainment = v.findViewById(R.id.entertainment_button);
        dailyLife = v.findViewById(R.id.daily_life_button);
        distance1 = v.findViewById(R.id.within1_button);
        distance2 = v.findViewById(R.id.within5_button);
        distance3 = v.findViewById(R.id.within10_button);
        oneDay = v.findViewById(R.id.one_day_button);
        oneWeek = v.findViewById(R.id.one_week_button);
        oneMonth = v.findViewById(R.id.one_month_button);
        setTopicListener(study);
        setTopicListener(entertainment);
        setTopicListener(dailyLife);

        setDistanceLimitListener(distance1,1000);
        setDistanceLimitListener(distance2,5000);
        setDistanceLimitListener(distance3,10000);

        setDateListener(oneDay,"oneDay");
        setDateListener(oneWeek,"oneWeek");
        setDateListener(oneMonth,"oneMonth");


    }
    private void setTopicListener(Button b){
        b.setOnClickListener(v -> {
            if(b.isPressed()){
                topic="";
                refreshRV();
                b.setPressed(false);
            }
            else{
                study.setPressed(false);
                entertainment.setPressed(false);
                dailyLife.setPressed(false);
                topic = (String) b.getText();
                refreshRV();
                b.setPressed(true);
            }

        });
    }
    private void setDistanceLimitListener(Button b, int i){
        b.setOnClickListener(v->{
            if(b.isPressed()){
                distanceLimit = Integer.MAX_VALUE;
                refreshRV();
                b.setPressed(false);
            }
            else{
                distance1.setPressed(false);
                distance2.setPressed(false);
                distance3.setPressed(false);
                distanceLimit = i;
                refreshRV();
                b.setPressed(true);
            }
        });
    }
    private void setDateListener (Button b, String d){
        b.setOnClickListener(v->{
            if(b.isPressed()){
                date = "";
                refreshRV();
                b.setPressed(false);
            }
            else{
                oneDay.setPressed(false);
                oneWeek.setPressed(false);
                oneMonth.setPressed(false);
                date = d;
                refreshRV();
                refreshRV();
                b.setPressed(true);
            }
        });
    }

    public void search() {
        List<Event> selected = new ArrayList<>();
        initEvents();
        for(int i=0;i<events.size();i++){
            Event e = events.get(i);
            if(e.getEventName().contains(target.toString())||e.getDescription().contains(target.toString())){
                selected.add(e);
            }
        }
        Log.println(Log.INFO,"SelectedSize",Integer.toString(selected.size()));
        eventRecyclerView.setAdapter(new DiscoverEventAdapter(selected,curLocation()));
    }


    private void refreshRV(){
        List<Event> selected = new ArrayList<>();

        for(int i=0;i<events.size();i++){
            Event e = events.get(i);
            if(!topic.equals("")&&!e.getEventCategory().equals(topic)){
                continue;
            }
            //Log.println(Log.INFO,"Distance:",Double.toString(e.getLocation().distanceTo(curLocation())));
            //Log.println(Log.INFO,"DistanceLimit:",Integer.toString(distanceLimit));
            if(e.getLocation().distanceTo(curLocation())>distanceLimit){
                Log.println(Log.INFO,"Distance:","Skip"+Integer.toString(distanceLimit));
                continue;
            }

            if(!date.equals("")){
                if(e.getDatetime()==null)
                    continue;
                if(date.equals("oneDay")){
                    if(e.getDatetime().getTimeStamp()-new DateTime().getTimeStamp()<24*60*60*1000){
                        continue;
                    }
                }
                else if(date.equals("oneWeek")){
                    if(e.getDatetime().getTimeStamp()-new DateTime().getTimeStamp()<7*24*60*60*1000){
                        continue;
                    }
                }
                else if(date.equals("oneMonth")){
                    if(e.getDatetime().getTimeStamp()-new DateTime().getTimeStamp()<30*24*60*60*1000){
                        continue;
                    }
                }
            }
            selected.add(e);
        }
        Log.println(Log.INFO, "Refresh",Integer.toString(selected.size())+"!!!!!");
        //selected.add(initEvents().get(0));
        eventRecyclerView.setAdapter(new DiscoverEventAdapter(selected,curLocation()));
  }


    /*
    private void initTopicList(View v) {
        RecyclerView topicRecyclerView = v.findViewById(R.id.topic_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        topicRecyclerView.setLayoutManager(layoutManager);
        topicRecyclerView.setAdapter(new DiscoverConditionAdapter(initTopics(),this));
    }

    private void initDistanceList(View v) {
        RecyclerView disRecyclerView = v.findViewById(R.id.distance_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        disRecyclerView.setLayoutManager(layoutManager);
        disRecyclerView.setAdapter(new DiscoverConditionAdapter(initDistances(),this));

    }

    private void initDateList(View v) {
        RecyclerView datetimeRecyclerView = v.findViewById(R.id.date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        datetimeRecyclerView.setLayoutManager(layoutManager);
        datetimeRecyclerView.setAdapter(new DiscoverConditionAdapter(initDates(),this));
    }*/


    public Location curLocation(){
        /*
        if(curLoc!=null){
            return curLoc;
        }*/
        ActivityCompat.requestPermissions(getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                1);
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        curLoc = ((MainActivity) getActivity()).locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return curLoc;
    }



    void initEvents(){

        events = new ArrayList<>();/*
        ArrayList<String> eventNames = new ArrayList<>();
        ArrayList<location> locations = new ArrayList<>();
        ArrayList<DateTime> datetimes = new ArrayList<>();
        ArrayList<String> categorys = new ArrayList<>();
        ArrayList<String> usr_ids = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        for(int i=0;i<5;i++){
            eventNames.add("EVENT"+Integer.toString(i));
            locations.add(new location(38+0.003*i, -147+0.003*i,"Unimelb"));
            datetimes.add(new DateTime());
            categorys.add("Study");
            usr_ids.add("1");
            descriptions.add("");
            ids.add(Integer.toString(i));
        }
        for(int i=0;i<5;i++){
            Event e = new Event(eventNames.get(i),locations.get(i),datetimes.get(i),categorys.get(i),usr_ids.get(i),descriptions.get(i),ids.get(i));
            events.add(e);

        }*/
        Log.println(Log.INFO,"events get!!!!","events get!!!");
        FirebaseAPI.rootRef.child("Event").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Event event = snapshot.getValue(Event.class);
                Log.d("Discover", "onDataChange: Event => "+event.toString());
                events.add(event);
                eventRecyclerView.setAdapter(new DiscoverEventAdapter(events,curLoc));
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
}
