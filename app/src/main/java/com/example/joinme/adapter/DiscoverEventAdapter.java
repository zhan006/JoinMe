package com.example.joinme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.fragments.DiscoverEventFragment;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;

import java.util.List;

public class DiscoverEventAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private List<Event> eventList;
    public DiscoverEventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }
    private DiscoverEventFragment.DiscoverEventClickListener discoverEventClickListener;
    @Override
    public void onClick(View v) {
        /*
        int position =
        if (discoverEventClickListener !=null){
            discoverEventClickListener.onItemClick(rv,v,position,mapList.get(position));
        }*/
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,time,location;
        Button detail;
        Button Join;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_name);
            time = itemView.findViewById(R.id.event_datetime);
            location = itemView.findViewById(R.id.event_location);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    //声明一下这个接口
    //提供setter方法
    public void setOnItemClickListener(DiscoverEventFragment.DiscoverEventClickListener listener){
        this.discoverEventClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        ((ViewHolder) holder).title.setText(event.getEventName());
        ((ViewHolder) holder).time.setText(event.getDatetime());
        ((ViewHolder) holder).location.setText(event.getLocation());
        ((ViewHolder) holder).detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

