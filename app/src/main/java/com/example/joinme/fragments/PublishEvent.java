package com.example.joinme.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.joinme.R;
import com.example.joinme.interfaces.DateTimeClick;
import com.example.joinme.objects.Event;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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

        Spinner category_spinner = (Spinner) view.findViewById(R.id.event_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);

        initPlaces();
        getCurrentPlace();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AutocompleteSupportFragment completeLocation = (AutocompleteSupportFragment)getActivity().getSupportFragmentManager().
                findFragmentById(R.id.autoCompleteLocation);
        completeLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        completeLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("location", "Place: " + place.getName() + ", " + place.getLatLng());
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

    public void initPlaces(){
        Places.initialize(getContext(),"AIzaSyDLzlvA6LLbYiY35Ch3tEziWe-dGzdJhLo");
        placesClient = Places.createClient(getContext());

    }
    public void getCurrentPlace(){
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i("location", String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getAddress(),
                                placeLikelihood.getLikelihood()));
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("location", "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
            getCurrentPlace();
            Log.d("location", String.valueOf(ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)));
        }
    }

    @Override
    public void OnDateTimeSelected(int Year, int Month, int Day, int Hour, int Minute) {
        event.setDatetime();
    }
}
