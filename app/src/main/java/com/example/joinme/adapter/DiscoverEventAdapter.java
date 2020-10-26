package com.example.joinme.adapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.fragments.DiscoverEventFragment;
import com.example.joinme.fragments.EventDetailFragment;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.objects.location;

import java.util.Comparator;
import java.util.List;

public class DiscoverEventAdapter extends RecyclerView.Adapter {
    private List<Event> eventList;
    private static Location loc;
    public DiscoverEventAdapter(List<Event> eventList, Location l) {
        this.eventList = eventList;
        this.loc = l;
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,time,address;
        Button detail;
        Button join;
        @RequiresApi(api = Build.VERSION_CODES.N)
        public ViewHolder(@NonNull View itemView, List<Event> e) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.event_name);
            time = (TextView) itemView.findViewById(R.id.event_datetime);
            address = (TextView) itemView.findViewById(R.id.event_location);
            detail = (Button)itemView.findViewById(R.id.detail_button);
            join = (Button)itemView.findViewById(R.id.detail_button);
            detail.setOnClickListener((v)->{
                e.sort(new Comparator<Event>() {
                    @Override
                    public int compare(Event o1, Event o2) {
                        if(o1.getLocation().distanceTo(DiscoverEventAdapter.loc)<o2.getLocation().distanceTo(DiscoverEventAdapter.loc)){
                            return -1;
                        }
                        else if(o1.getLocation().distanceTo(DiscoverEventAdapter.loc)>o2.getLocation().distanceTo(DiscoverEventAdapter.loc)){
                            return 1;
                        }
                        return 0;
                    }
                });
            });
            join.setOnClickListener((v)->{
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view,eventList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        ((ViewHolder) holder).title.setText(event.getEventName());
        if(event.getDatetime()!=null) ((ViewHolder) holder).time.setText(event.getDatetime().toString());
        if(event.getLocation()!=null) ((ViewHolder) holder).address.setText(event.getLocation().getAddress()+" "+event.getLocation().distanceTo(loc));

        /*((ViewHolder) holder).detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

