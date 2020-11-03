package com.example.joinme.adapter;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.fragments.DiscoverEventFragment;
import com.example.joinme.objects.DateTime;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.location;

import java.util.ArrayList;
import java.util.List;

public class DiscoverConditionAdapter extends RecyclerView.Adapter{
    private DiscoverEventFragment page;
    private List<String> conditions;
    private static final String TAG = "DiscoverConditionAdapter";
    static class ViewHolder extends RecyclerView.ViewHolder {
        public Button condition;
        public ViewHolder(@NonNull View itemView, DiscoverEventFragment page) {
            super(itemView);
            condition = itemView.findViewById(R.id.discover_button);
            condition.setOnClickListener(v -> {
                page.eventRecyclerView.setAdapter(new DiscoverEventAdapter(initEvents(), page.curLocation()));

            });
        }
    }
    public DiscoverConditionAdapter(List<String> conditions,  DiscoverEventFragment page) {
        this.conditions = conditions;
        this.page= page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_condition_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view,page);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String condition = conditions.get(position);
        ((DiscoverConditionAdapter.ViewHolder) holder).condition.setText(condition);
    }

    @Override
    public int getItemCount() {
        return conditions.size();
    }

    static List<Event> initEvents(){
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
        for(int i=0;i<10;i++){
            Event e = new Event(eventNames.get(i),locations.get(i),datetimes.get(i),categorys.get(i),usr_ids.get(i),descriptions.get(i),ids.get(i));
            events.add(e);

        }

        return events;

    }
}
