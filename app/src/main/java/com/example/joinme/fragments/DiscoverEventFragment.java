package com.example.joinme.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
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
import com.example.joinme.adapter.DiscoverEventAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.LocationRenderable;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//This fragment is used for event discovering. Users can select their interested events in this page.
public class  DiscoverEventFragment extends Fragment implements LocationRenderable {

    public RecyclerView eventRecyclerView;
    private Button study,entertainment, dailyLife; //Buttons for selection
    private Button distance1,distance2,distance3; //Buttons for selection
    private Button oneDay, oneWeek, oneMonth; //Buttons for selection
    private String topic = ""; //record the selected topic
    private int distanceLimit = Integer.MAX_VALUE; //record the selected distanceLimit
    private String date=""; //record the selected date limit
    private ArrayList<Event> events = new ArrayList<>(); //record all events
    private Editable target; //the searching target
    public Location curLoc; //current Location
    public TitleBar bar;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int prev = ((NavBar) getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }
    public DiscoverEventFragment(){
        super();
    }

    //set the initial searching target
    public DiscoverEventFragment(Editable t){
        super();
        target=t;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.discover_events, container, false);
        EditText searchBar = v.findViewById(R.id.search_text);
        searchBar.setHint("search by name or ID");
        bar = v.findViewById(R.id.search_event_title);
        bar.setOnClickBackListener((view)-> Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack());
        ImageButton searchButton = v.findViewById(R.id.search_button);

        //click the search button to search
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
        // when initialized with a searching task, call search() to show the result
        if(target!=null&&!target.toString().equals(""))
            search();
        return v;
    }

    //initialize the buttons;
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

    //set listener for topic buttons
    private void setTopicListener(Button b){
        b.setOnClickListener(v -> {
            if(topic.contentEquals(b.getText())){
                //cancel selecting if the button is already selected;
                topic="";
                refreshRV();
                b.setPressed(false);
            }
            else{
                //cancel selecting other topic buttons
                study.setPressed(false);
                entertainment.setPressed(false);
                dailyLife.setPressed(false);
                topic = (String) b.getText();
                refreshRV();
                b.setPressed(true);
            }

        });
    }

    //set listener for distance limit buttons
    private void setDistanceLimitListener(Button b, int i){
        b.setOnClickListener(v->{
            if(distanceLimit==i){
                //cancel selecting if the button is already selected;
                distanceLimit = Integer.MAX_VALUE;
                refreshRV();
                b.setPressed(false);
            }
            else{
                //cancel selecting other distance limit buttons
                distance1.setPressed(false);
                distance2.setPressed(false);
                distance3.setPressed(false);
                distanceLimit = i;
                refreshRV();
                b.setPressed(true);
            }
        });
    }

    //set listener for date selecting buttons
    private void setDateListener (Button b, String d){
        b.setOnClickListener(v->{
            if(date.equals(d)){
                //cancel selecting if the button is already selected;
                date = "";
                refreshRV();
                b.setPressed(false);
            }
            else{
                //cancel selecting other date selecting buttons
                oneDay.setPressed(false);
                oneWeek.setPressed(false);
                oneMonth.setPressed(false);
                date = d;
                refreshRV();
                b.setPressed(true);
            }
        });
    }

    //search events with the given key word in its title or description
    public void search() {
        List<Event> selected = new ArrayList<>();
        initEvents();
        for(int i=0;i<events.size();i++){
            Event e = events.get(i);
            if(e.getEventName().contains(target.toString())||e.getDescription().contains(target.toString())){
                selected.add(e);
            }
        }
        eventRecyclerView.setAdapter(new DiscoverEventAdapter(selected,curLocation()));
    }


    //select the interested events
    private void refreshRV(){
        //Log.println(Log.INFO,"Discover configure:::",topic+" "+Integer.valueOf(distanceLimit)+" "+date);
        List<Event> selected = new ArrayList<>();
        for(int i=0;i<events.size();i++){
            Event e = events.get(i);
            if(!topic.equals("")&&!e.getEventCategory().equals(topic)){
                continue;
            }
            if(e.getLocation().distanceTo(curLocation())>distanceLimit){
                continue;
            }
            if(!date.equals("")){
                long timestampDifference = e.getDatetime().getTimeStamp()-new DateTime().getTimeStamp();
                Log.println(Log.INFO,"timestampDifference",timestampDifference+" "+timestampDifference / 1000 / 60 / 60 / 24);
                if(e.getDatetime()==null)
                    continue;
                switch (date) {
                    case "oneDay":
                        if (timestampDifference > ((long) 24) * 60 * 60 * 1000) {
                            continue;
                        }
                        break;
                    case "oneWeek":
                        if (timestampDifference > ((long) 7) * 24 * 60 * 60 * 1000) {
                            continue;
                        }
                        break;
                    case "oneMonth":
                        if (timestampDifference > ((long) 30) * 24 * 60 * 60 * 1000) {
                            continue;
                        }
                        break;
                }
            }
            selected.add(e);
        }
        eventRecyclerView.setAdapter(new DiscoverEventAdapter(selected,curLocation()));
    }

    public Location curLocation(){
        /*
        if(curLoc!=null){
            return curLoc;
        }*/
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                1);
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    //initialize the events by reading database
    public void initEvents(){
        curLoc = curLocation();
        events = new ArrayList<>();
        DateTime curDate = new DateTime();
        Log.println(Log.INFO,"datetime",curDate.getDate()+" "+curDate.getTime()+" "+ curDate.getTimeStamp());
        FirebaseAPI.rootRef.child("Event").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Event event = snapshot.getValue(Event.class);
                //only collect the upcoming events
                assert event != null;
                if(curDate.getTimeStamp()<event.getDatetime().getTimeStamp())
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
