package com.example.joinme.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.location;
import com.example.joinme.reusableComponent.NavBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscoverEventFragment extends Fragment {

    public RecyclerView eventRecyclerView;
    private Button study,entertainment, dailyLife;
    private Button distance1,distance2,distance3;
    private Button today, thisWeek, thisMonth;
    private String topic;
    private int distanceLimit = Integer.MAX_VALUE;
    private String date;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar) getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.discover_events, container, false);
        EditText searchBar = v.findViewById(R.id.search_text);
        searchBar.setHint("search by name or ID");

        initButtons(v);
        initEvent(v);

        return v;
    }
    private void initButtons(View v){
        study = v.findViewById( R.id.study_button);
        entertainment = v.findViewById(R.id.entertainment_button);
        dailyLife = v.findViewById(R.id.daily_life_button);
        distance1 = v.findViewById(R.id.within1_button);
        distance2 = v.findViewById(R.id.within5_button);
        distance3 = v.findViewById(R.id.distance_all_button);
        today = v.findViewById(R.id.today_button);
        thisWeek = v.findViewById(R.id.this_week_button);
        thisMonth = v.findViewById(R.id.this_month_button);
        setTopicListener(study);
        setTopicListener(entertainment);
        setTopicListener(dailyLife);

        setDistanceLimitListener(distance1,1);
        setDistanceLimitListener(distance1,5);
        setDistanceLimitListener(distance1,Integer.MAX_VALUE);

        setDateListener(today,"today");
        setDateListener(thisWeek,"thisWeek");
        setDateListener(thisMonth,"thisMonth");


    }
    private void setTopicListener(Button b){
        b.setOnClickListener(v -> {
            topic = (String) b.getText();
            refreshRV();
        });
    }
    private void setDistanceLimitListener(Button b, int i){
        b.setOnClickListener(v->{
            distanceLimit = i;
            refreshRV();
        });
    }
    private void setDateListener (Button b, String d){
        b.setOnClickListener(v->{
            date = d;
            refreshRV();
        });
    }


    private void refreshRV(){
        List<Event> selected = new ArrayList<>();
        for(int i=0;i<initEvents().size();i++){
            Event e = initEvents().get(i);
            if(!e.getEventCategory().equals(topic)){
                break;
            }


            selected.add(e);

        }
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


    private void initEvent(View v) {
        eventRecyclerView = v.findViewById(R.id.discover_events_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setAdapter(new DiscoverEventAdapter(initEvents(),curLocation()));


    }

    public Location curLocation(){
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
        return((MainActivity) getActivity()).locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }



    /*
    List<String> initTopics() {
        ArrayList<String> topics = new ArrayList<>();
        topics.add("Basketball");
        topics.add("Movie");
        topics.add("Music");
        topics.add("Study");
        return topics;
    }

    List<String> initDistances() {
        ArrayList<String> distances = new ArrayList<>();
        distances.add("0-5km");
        distances.add("5-8km");
        distances.add(">8km");
        return distances;
    }

    List<String> initDates() {
        ArrayList<String> dates = new ArrayList<>();
        dates.add("Aug 1");
        dates.add("Aug 2");
        dates.add("Aug 3");
        dates.add("After");
        return dates;
    }
    */
    List<Event> initEvents(){
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<String> eventNames = new ArrayList<>();
        ArrayList<location> locations = new ArrayList<>();
        ArrayList<DateTime> datetimes = new ArrayList<>();
        ArrayList<String> categorys = new ArrayList<>();
        ArrayList<String> usr_ids = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        for(int i=0;i<5;i++){
            eventNames.add("EVENT"+Integer.toString(i));
            locations.add(new location(-37.797+0.001*i, 144.961+0.001*i,"Unimelb"));
            datetimes.add(new DateTime());
            categorys.add("study");
            usr_ids.add("1");
            descriptions.add("");
            ids.add(Integer.toString(i));
        }
        for(int i=0;i<5;i++){
            Event e = new Event(eventNames.get(i),locations.get(i),datetimes.get(i),categorys.get(i),usr_ids.get(i),descriptions.get(i),ids.get(i));
            events.add(e);

        }

        return events;

    }
}
