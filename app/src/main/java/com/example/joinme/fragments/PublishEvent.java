package com.example.joinme.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinme.R;
import com.example.joinme.reusableComponent.TitleBar;

public class PublishEvent extends Fragment {
    @Nullable
    private TitleBar titleBar;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.activity_publish_event,container,false);
        titleBar = view.findViewById(R.id.title_bar);
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
