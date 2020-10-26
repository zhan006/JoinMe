package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.ManageEventAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.EventRenderable;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.Time;
import com.example.joinme.reusableComponent.NavBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManagementFragment extends Fragment implements EventRenderable {
    private EditText searchText;
    private Button invitedBtn,organise,attend;
    private ArrayList<Event> attendingEvent;
    private RecyclerView eventRecycler;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar)getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.event_management_activity,container,false);
        searchText = v.findViewById(R.id.search_text);
        invitedBtn = v.findViewById(R.id.see_invited);
        organise = v.findViewById(R.id.organised_event);
        attend = v.findViewById(R.id.attending_event);
        eventRecycler = v.findViewById(R.id.event_mng_recycle);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        renderEvent();
        setOnClickListener();
        return v;
    }
    public void setOnClickListener(){
        this.organise.setOnClickListener((v)->{
            String uid = ((MainActivity)getActivity()).getUid();
            String organizePath = "OrganizedEvents/"+uid;
            FirebaseAPI.getFirebaseData(organizePath, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,Boolean> map = (HashMap) snapshot.getValue();
                    ArrayList<String> organizeList = new ArrayList();
                    if(map!=null){
                        for(String k : map.keySet()){
                            if(map.get(k)){organizeList.add(k);}
                        }
                    }
                    eventRecycler.setAdapter(new ManageEventAdapter(organizeList,uid, ManageEventAdapter.EventType.ORGANIZE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        this.attend.setOnClickListener((v)->{
            renderEvent();
        });
    }

    public ArrayList<Event> getParentEvents(){
        return ((MainActivity)getActivity()).getEventList();
    }

    @Override
    public void renderEvent() {
        String uid = ((MainActivity)getActivity()).getUid();
        this.attendingEvent = getParentEvents();
        if(attendingEvent!=null){
            eventRecycler.setAdapter(new ManageEventAdapter(this.attendingEvent,uid, ManageEventAdapter.EventType.ATTEND));
        }
    }
}
