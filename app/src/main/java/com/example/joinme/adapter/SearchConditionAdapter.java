package com.example.joinme.adapter;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;

import java.util.List;

public class SearchConditionAdapter extends RecyclerView.Adapter {

    private List<String> searchConditions;
    private static final String TAG = "SearchConditionAdapter";
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView condition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            condition = itemView.findViewById(R.id.search_condition_item_text);
        }
    }
    public SearchConditionAdapter(List<String> searchConditions) {
        this.searchConditions = searchConditions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_condition_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String condition = searchConditions.get(position);
        ((ViewHolder) holder).condition.setText(condition);
    }

    @Override
    public int getItemCount() {
        return searchConditions.size();
    }
}
