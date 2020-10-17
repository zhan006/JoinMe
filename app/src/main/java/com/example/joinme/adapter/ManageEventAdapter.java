package com.example.joinme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.fragments.EventDetailFragment;
import com.example.joinme.objects.Event;

import java.util.List;

public class ManageEventAdapter extends RecyclerView.Adapter {
    private List<Event> mEventList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventName,eventLocation,eventDatetime;
        Button cancel,update;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location);
            eventDatetime = (TextView) itemView.findViewById(R.id.event_datetime);
            cancel=(Button)itemView.findViewById(R.id.cancel_button);
            update=(Button)itemView.findViewById(R.id.update_button);

            cancel.setOnClickListener((v)->{});
//            update.setOnClickListener((v)->{
//                ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
//                        new HomePageFragment(),null).commit();
//            });

//            Deng: this is where I should show my event_details page!
            update.setOnClickListener((v)->{
                ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                        new EventDetailFragment(),null).commit();
            });

        }
    }

    public ManageEventAdapter(List<Event> eventList){
        mEventList=eventList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mng_event_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = mEventList.get(position);
        ((ViewHolder)holder).eventName.setText(event.getEventName());
        ((ViewHolder)holder).eventDatetime.setText(event.getDatetime());
        ((ViewHolder)holder).eventLocation.setText(event.getLocation());
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
