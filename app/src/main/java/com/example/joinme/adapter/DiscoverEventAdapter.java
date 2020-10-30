package com.example.joinme.adapter;

import android.location.Location;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.objects.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscoverEventAdapter extends RecyclerView.Adapter<DiscoverEventAdapter.ViewHolder> implements Filterable {
    private List<Event> eventList;
    private List<Event> filteredList;
    private static Location loc;
    public DiscoverEventAdapter(List<Event> eventList, Location l) {
        this.eventList = eventList;
        this.filteredList = eventList;
        this.loc = l;
    }

    public Location getLoc(){
        return loc;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = eventList;
                } else {
                    List<Event> f = new ArrayList<>();
                    for (Event e : eventList) {
                        //这里根据需求，添加匹配规则
                        if (e.getId().equals(charSequence)) {
                            f.add(e);
                        }
                    }

                    filteredList = f;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<Event>) filterResults.values;
                //刷新数据
                notifyDataSetChanged();
            }
        };
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,time,address;
        Button detail;
        Button join;
        @RequiresApi(api = Build.VERSION_CODES.N)
        public ViewHolder(@NonNull View itemView, DiscoverEventAdapter adapter) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.event_name);
            time = (TextView) itemView.findViewById(R.id.event_datetime);
            address = (TextView) itemView.findViewById(R.id.event_location);
            detail = (Button)itemView.findViewById(R.id.detail_button);
            join = (Button)itemView.findViewById(R.id.detail_button);
            detail.setOnClickListener((v)->{
                /*
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

                });*/
                adapter.sortEvents();
            });
            join.setOnClickListener((v)->{
                Collections.reverse(adapter.eventList);
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view,this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = filteredList.get(position);
        ((ViewHolder) holder).title.setText(event.getEventName());
        if(event.getDatetime()!=null) ((ViewHolder) holder).time.setText(event.getDatetime().toString());
        if(event.getLocation()!=null) ((ViewHolder) holder).address.setText(event.getLocation().getAddress()+" "+event.getLocation().distanceTo(loc));

    }
    /*
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }*/

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    public void sortEvents(){
        Collections.shuffle(eventList);
        this.notifyDataSetChanged();
    }
}

