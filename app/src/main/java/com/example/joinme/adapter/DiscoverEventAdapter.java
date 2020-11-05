package com.example.joinme.adapter;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.fragments.EventDetailFragment;
import com.example.joinme.objects.Event;
import com.example.joinme.utils;

import java.util.List;


//This adapter is to manage the RecyclerView for the discoverEventFragment.
public class DiscoverEventAdapter extends RecyclerView.Adapter<DiscoverEventAdapter.ViewHolder> {
    private final List<Event> eventList; //Hold all events for discovering in the database
    private static Location loc; //Hold current location

    public DiscoverEventAdapter(List<Event> eventList, Location l) {
        this.eventList = eventList;
        loc = l;
    }

    //Inner class holding the view
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,time,address;
        Button detail;
        Button join;
        @RequiresApi(api = Build.VERSION_CODES.N)
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.event_name);
            time = (TextView) itemView.findViewById(R.id.event_datetime);
            address = (TextView) itemView.findViewById(R.id.event_location);
            detail = (Button)itemView.findViewById(R.id.detail_button);
            join = (Button)itemView.findViewById(R.id.join_button);
            join.setVisibility(View.GONE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        //Click the detail button to go to the event detail page
        viewHolder.detail.setOnClickListener(view1 -> {
            int position = viewHolder.getAdapterPosition();
            Event currentEvent1 = eventList.get(position);
            Toast.makeText(view1.getContext(), "You clicked on detail button of " + currentEvent1.getEventName(), Toast.LENGTH_SHORT).show();
            EventDetailFragment f = new EventDetailFragment();
            Bundle bd = new Bundle();
            bd.putSerializable("current_event", currentEvent1);
            f.setArguments(bd);
            utils.replaceFragment(((AppCompatActivity) view1.getContext()).getSupportFragmentManager(),f, null);

        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        ((ViewHolder) holder).title.setText(event.getEventName());
        if(event.getDatetime()!=null) ((ViewHolder) holder).time.setText(event.getDatetime().toString());
        //show location and distance.
        if(event.getLocation()!=null) ((ViewHolder) holder).address.setText(
                event.getLocation().getAddress()+"       "+
                        String.format("%.2f", (Math.round(event.getLocation().distanceTo(loc))/1000.0))+" km");

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

}

