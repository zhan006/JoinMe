package com.example.joinme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.example.joinme.objects.location;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class EditProfileFragment extends Fragment {
    private EditText username,firstName,lastName,email,phone,bio;
    private Spinner gender;
    private User user;
    private AutocompleteSupportFragment location;
    private PlacesClient placesClient;
    private Button save;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.edit_profile_fragment,container,false);
        initPlaces();
        initView(view);
        return view;
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
    public void initView(View v){
        user = (User)getArguments().getSerializable("user");
        username = v.findViewById(R.id.user_name);
        firstName = v.findViewById(R.id.first_name);
        lastName = v.findViewById(R.id.last_name);
        email = v.findViewById(R.id.email);
        phone = v.findViewById(R.id.phone);
        bio = v.findViewById(R.id.bio);
        gender = v.findViewById(R.id.gender);
        save = v.findViewById(R.id.save);
        save.setOnClickListener((view)->{
            try {
                updateProfile();
            } catch (CompulsoryElementException e) {
                Snackbar.make(getView(),"please check compulsory items",Snackbar.LENGTH_LONG).show();
            }
        });
        location = (AutocompleteSupportFragment)getActivity().getSupportFragmentManager().
                findFragmentById(R.id.autoCompleteLocation);
        location.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME));
        location.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                location.setText(place.getAddress());
                LatLng latlang = place.getLatLng();
                user.setLocation(new location(latlang.latitude,latlang.longitude,place.getName()));
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        if(!(user ==null)){
            username.setText(user.getUsername());
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
            bio.setText(user.getAbout());
            if(user.getLocation()!=null) location.setText(user.getLocation().getAddress());

        }

    }
    public void updateProfile() throws CompulsoryElementException {
        String un = username.getText().toString().trim();
        if(!un.equals("")){user.setUsername(un);}
        else throw new CompulsoryElementException();

        String fn = firstName.getText().toString().trim();
        if(!fn.equals("")){user.setFirstName(fn);}
        else throw new CompulsoryElementException();

        String ln = lastName.getText().toString().trim();
        if(!ln.equals("")){user.setLastName(ln);}
        else throw new CompulsoryElementException();

        String em = email.getText().toString().trim();
        if(!em.equals("")){user.setEmail(em);}
        else throw new CompulsoryElementException();

        String gd = gender.getSelectedItem().toString().trim();
        if(!gd.equals("")){user.setGender(gd);}
        else throw new CompulsoryElementException();

        String bo = bio.getText().toString().trim();
        user.setAbout(bo);

        String ph = phone.getText().toString().trim();
        user.setPhone(ph);
        String uid = ((MainActivity)getActivity()).getUid();

        FirebaseAPI.rootRef.child("User").child(uid).setValue(user).addOnCompleteListener(task -> {
            Snackbar.make(getView(),"Success",Snackbar.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStack();
        });


    }
    public void initPlaces(){
        Places.initialize(getContext(),"AIzaSyDLzlvA6LLbYiY35Ch3tEziWe-dGzdJhLo");
        placesClient = Places.createClient(getContext());

    }
}
