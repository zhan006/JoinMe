package com.example.joinme.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.interfaces.DateTimeClick;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.location;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;

public class PublishEvent extends Fragment implements DateTimeClick {
    @Nullable
    private TitleBar titleBar;
    private EditText event_name, event_location, about, event_duration, date_time;
    private Spinner event_category;
    private ImageButton location_button, invite_friend;
    private Button publish_event;
    private EditText min_group_size, max_group_size;
    private PlacesClient placesClient;
    private Event event= new Event();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.activity_publish_event,container,false);
        titleBar = view.findViewById(R.id.title_bar);
        event_name = view.findViewById(R.id.event_name);
        event_location = view.findViewById(R.id.event_location);
        event_category = view.findViewById(R.id.event_category);
        min_group_size = view.findViewById(R.id.min_group_size);
        max_group_size = view.findViewById(R.id.max_group_size);
        about = view.findViewById(R.id.about);
        invite_friend = view.findViewById(R.id.invite_friend);
        publish_event = view.findViewById(R.id.publish_event);
        event_duration = view.findViewById(R.id.event_duration);
        date_time = view.findViewById(R.id.date_time);
        date_time.setFocusable(false);
        date_time.setOnClickListener((v -> {
            DialogFragment date_time = new date_time_pickers();
            date_time.show(getActivity().getSupportFragmentManager(),"date_time");
        }));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        event_category.setAdapter(adapter);
        initPlaces();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        publish_event.setOnClickListener((v)->{
            try {
                addEvent(event);
            } catch (CompulsoryElementException e) {
                Snackbar.make(getView(),"please check compulsory elements",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        AutocompleteSupportFragment completeLocation = (AutocompleteSupportFragment)getActivity().getSupportFragmentManager().
                findFragmentById(R.id.autoCompleteLocation);
        completeLocation.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME));
        completeLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {

                Log.i("location", "Place: " + place.getName() + ", " + place.getLatLng());
                LatLng latlang = place.getLatLng();
                event.setLocation(new location(latlang.latitude,latlang.longitude,place.getName()));

            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("location", "An error occurred: " + status);
            }
        });
        titleBar.setOnClickBackListener((v) -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // to prevent duplicated fragment being created
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.autoCompleteLocation));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    public void addEvent(Event event) throws CompulsoryElementException {
//        DatabaseReference ref = FirebaseAPI.rootRef.child("Event").push();
        String key = FirebaseAPI.pushFirebaseNode("Event");
        event.setId(key);
        String name = event_name.getText().toString().trim();
        if(!name.equals("")) event.setEventName(name);
        else{throw new CompulsoryElementException();}
        if(event.getDatetime()==null) throw new CompulsoryElementException();
        String duration = event_duration.getText().toString().trim();
        if(!duration.equals("")) event.setDuration(duration);
        else{throw new CompulsoryElementException();}
        if(event.getLocation()==null) throw new CompulsoryElementException();
        String category = event_category.getSelectedItem().toString();
        if(!category.equals("")) event.setEventCategory(category);
        else{throw new CompulsoryElementException();}
        int min = 0;
        try{
            min = Integer.parseInt(min_group_size.getText().toString());
        } catch (Exception e){
            throw new CompulsoryElementException();
        }
        int max = 0;
        try{
            max = Integer.parseInt(max_group_size.getText().toString());
        } catch (Exception e){
            throw new CompulsoryElementException();
        }
        if(min>max){throw new CompulsoryElementException();}
        else{ event.setMin(min);event.setMax(max);}
        String ab = about.getText().toString();
        event.setDescription(ab);
        String uid = ((MainActivity)getActivity()).getUid();
        event.setOrganizerid(uid);
        FirebaseAPI.addEvent(event, uid, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null){
                    Snackbar.make(getView(),"Success!",Snackbar.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                else{
                    Snackbar.make(getView(),error.getDetails(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });




    }

    public void initPlaces(){
        Places.initialize(getContext(),"AIzaSyDLzlvA6LLbYiY35Ch3tEziWe-dGzdJhLo");
        placesClient = Places.createClient(getContext());

    }

    @Override
    public void OnDateTimeSelected(int Year, int Month, int Day, int Hour, int Minute) {
        Log.d("time",String.valueOf(Year)+String.valueOf(Month)+String.valueOf(Day)+String.valueOf(Hour));
        event.setDatetime(new DateTime(Year,Month,Day,Hour,Minute));
        date_time.setText(event.getDatetime().toString());
    }
}
class CompulsoryElementException extends Exception{
    public CompulsoryElementException(){
        super();
    }
    public CompulsoryElementException(String msg){
        super(msg);
    }
}
