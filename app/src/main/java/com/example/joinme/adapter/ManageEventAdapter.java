package com.example.joinme.adapter;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.fragments.EventDetailFragment;
import com.example.joinme.objects.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEventAdapter extends RecyclerView.Adapter {
    private List<Event> mEventList;
    private ArrayList<String> mEventIDs;
    String uid;
    EventType type;
    public enum EventType {ORGANIZE,ATTEND};
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
//            Deng: this is where I should show my event_details page!
            update.setOnClickListener((v)->{
                ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                        new EventDetailFragment(),null).commit();
            });

        }
    }

    public ManageEventAdapter(List<Event> eventList,String id,EventType type){
        this.uid = id;
        mEventList=eventList;
        this.type = type;
    }
    public ManageEventAdapter(ArrayList<String> eventIDs,String id,EventType type){
        this.uid = id;
        mEventIDs = eventIDs;
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
        if(mEventList!=null){
            Event event = mEventList.get(position);
            ((ViewHolder)holder).eventName.setText(event.getEventName());
            if(event.getDatetime()!=null) ((ViewHolder)holder).eventDatetime.setText(event.getDatetime().toString());
            if(event.getLocation()!=null) ((ViewHolder)holder).eventLocation.setText(event.getLocation().getAddress());

            ((ViewHolder)holder).cancel.setOnClickListener((v)->{
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure to cancel the event?");
                builder.setTitle("Alert");
                builder.setPositiveButton("Confirm", (dialogInterface, i) ->{
                            FirebaseAPI.rootRef.child("AttendingList/"+uid).child(event.getId()).setValue(false);
                            mEventList.remove(position);
                            this.notifyDataSetChanged();
                        });

                builder.setNegativeButton("Cancel",((dialogInterface, i) ->
                        dialogInterface.cancel()));
                builder.create().show();
            });
        }
        else if(mEventIDs!=null){
            String id = mEventIDs.get(position);
            String path = "Event/"+id;
            FirebaseAPI.getFirebaseData(path, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Event event = snapshot.getValue(Event.class);
                    ((ViewHolder)holder).eventName.setText(event.getEventName());
                    if(event.getDatetime()!=null) ((ViewHolder)holder).eventDatetime.setText(event.getDatetime().toString());
                    if(event.getLocation()!=null) ((ViewHolder)holder).eventLocation.setText(event.getLocation().getAddress());
                    ((ViewHolder)holder).cancel.setOnClickListener((v)->{
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("Are you sure to cancel the event?");
                        builder.setTitle("Alert");
                        builder.setPositiveButton("Confirm", (dialogInterface, i) ->{
                            FirebaseAPI.rootRef.child("OrganizedEvents/"+uid).child(event.getId()).setValue(false);
                            mEventIDs.remove(position);
                            notifyDataSetChanged();
                        });

                        builder.setNegativeButton("Cancel",((dialogInterface, i) ->
                                dialogInterface.cancel()));
                        builder.create().show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mEventList!=null) return mEventList.size();
        if(mEventIDs!=null) return mEventIDs.size();
        return 0;
    }
}
