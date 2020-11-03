package com.example.joinme.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.fragments.EventDetailFragment;
import com.example.joinme.objects.Event;
import com.example.joinme.utils;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter {
    private List<Event> mEventList;


    private static final String TAG = "EventAdapter";


    static class ViewHolder extends RecyclerView.ViewHolder{
        View eventView;
        TextView eventName,eventLocation,eventDatetime;
        Button detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventView = itemView;
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location);
            eventDatetime = (TextView) itemView.findViewById(R.id.event_datetime);
            detail=(Button)itemView.findViewById(R.id.detail_button);

        }
    }

    public EventAdapter(List<Event> eventList){
        mEventList=eventList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.detail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Event currentEvent1 = mEventList.get(position);
                Toast.makeText(view.getContext(), "You clicked on detail button of " + currentEvent1.getEventName(), Toast.LENGTH_SHORT).show();
                EventDetailFragment f = new EventDetailFragment();
                Bundle bd = new Bundle();
                bd.putSerializable("current_event", currentEvent1);
                f.setArguments(bd);
                utils.replaceFragment(((AppCompatActivity)view.getContext()).getSupportFragmentManager(),f, null);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = mEventList.get(position);
        ((ViewHolder)holder).eventName.setText(event.getEventName());
        if(event.getDatetime()!=null) ((ViewHolder)holder).eventDatetime.setText(event.getDatetime().toString());
        if(event.getLocation()!=null) ((ViewHolder)holder).eventLocation.setText(event.getLocation().getAddress());

    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }










}
