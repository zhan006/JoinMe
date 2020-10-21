package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.SearchConditionAdapter;
import com.example.joinme.reusableComponent.NavBar;

import java.util.ArrayList;
import java.util.List;

public class DiscoverEventFragment extends Fragment {


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar)getActivity().findViewById(R.id.navbar)).getPrevSelected();
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

        initTopicList(v);
        initDistanceList(v);
        initDateList(v);

        return v;
    }

    public interface DiscoverEventClickListener{
        void onItemClick(RecyclerView parent,View view, int position, String data);

    }




    private void initTopicList(View v) {
        RecyclerView eventRecyclerView = v.findViewById(R.id.topic_list);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventRecyclerView.setAdapter(new SearchConditionAdapter(initTopics()));
    }

    private void initDistanceList(View v) {
        RecyclerView hobbyRecyclerView = v.findViewById(R.id.distance_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        hobbyRecyclerView.setLayoutManager(layoutManager);
        hobbyRecyclerView.setAdapter(new SearchConditionAdapter(initDistances()));

    }

    private void initDateList(View v) {
        RecyclerView genderRecyclerView = v.findViewById(R.id.date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        genderRecyclerView.setLayoutManager(layoutManager);
        genderRecyclerView.setAdapter(new SearchConditionAdapter(initDates()));
    }



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
}
