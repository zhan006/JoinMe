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
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter{
    private List<Event> eventList;

    public NotificationAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date,time;
        public Button location;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.event_location);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        NotificationAdapter.ViewHolder viewHolder = new NotificationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        ((NotificationAdapter.ViewHolder) holder).name.setText(event.getEventName());
        ((NotificationAdapter.ViewHolder) holder).date.setText(event.getDatetime());
        ((NotificationAdapter.ViewHolder) holder).location.setText(event.getLocation());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

