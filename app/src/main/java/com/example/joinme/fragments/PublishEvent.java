package com.example.joinme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinme.R;
import com.example.joinme.reusableComponent.TitleBar;

public class PublishEvent extends Fragment {
    @Nullable
    private TitleBar titleBar;
    private EditText event_name, event_time, event_location, about;
    private Spinner event_category;
    private ImageButton calendar, location_button, invite_friend;
    private Button publish_event;
    private NumberPicker min_group_size, max_group_size;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.activity_publish_event,container,false);
        titleBar = view.findViewById(R.id.title_bar);
        event_name = view.findViewById(R.id.event_name);
        event_time = view.findViewById(R.id.event_time);
        event_location = view.findViewById(R.id.event_location);
        event_category = view.findViewById(R.id.event_category);
        min_group_size = view.findViewById(R.id.min_group_size);
        min_group_size.setMaxValue(100);
        min_group_size.setMinValue(1);
        max_group_size = view.findViewById(R.id.max_group_size);
        max_group_size.setMaxValue(100);
        max_group_size.setMinValue(1);
        calendar = view.findViewById(R.id.calendar);
        location_button = view.findViewById(R.id.location_button);
        about = view.findViewById(R.id.about);
        invite_friend = view.findViewById(R.id.invite_friend);
        publish_event = view.findViewById(R.id.publish_event);

        Spinner category_spinner = (Spinner) view.findViewById(R.id.event_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);
        
        return view;
    }

    

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleBar.setOnClickBackListener((v) -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });


    }
}
