package com.example.joinme.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.R;

import java.util.ArrayList;

public class photoAdapter extends RecyclerView.Adapter {
    private ArrayList<String> images;
    private Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.photo);
        }
    }
    public photoAdapter(ArrayList<String> images, Context context){
        this.images = images;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        photoAdapter.ViewHolder viewHolder = new photoAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Glide.with(context).load(images.get(position)).into(((ViewHolder)holder).image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
