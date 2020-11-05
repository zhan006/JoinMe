package com.example.joinme.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.R;
import com.example.joinme.activity.PhotoActivity;
import com.example.joinme.database.FirebaseAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter {
    private ArrayList<String> images,keys;
    private Context context;
    private String uid;
    private boolean isYou;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ImageButton delete;
        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.img);
            delete = itemView.findViewById(R.id.deletePhoto);
        }
    }
    public AlbumAdapter(ArrayList<String> images, ArrayList<String> keys,Context mContext,String uid,boolean isYou){
        this.images = images;
        this.keys = keys;
        context = mContext;
        this.uid = uid;
        this.isYou = isYou;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Glide.with(context).load(images.get(position)).into(((ViewHolder)holder).image);
        ((ViewHolder) holder).image.setOnLongClickListener((v)->{
            if(isYou){
                ((ViewHolder) holder).delete.setVisibility(View.VISIBLE);
            }

            return true;
        });
        ((ViewHolder) holder).image.setOnClickListener((v)->{
            if(((ViewHolder) holder).delete.getVisibility()==View.VISIBLE){

                ((ViewHolder) holder).delete.setVisibility(View.INVISIBLE);
            }
            else{
                Intent intent = new Intent(v.getContext(), PhotoActivity.class);
                intent.putExtra("url",images.get(position));
                v.getContext().startActivity(intent);
            }
        });
        ((ViewHolder) holder).delete.setOnClickListener((v)->{
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Are you sure to delete the photo?");
            builder.setTitle("Alert");
            builder.setPositiveButton("Confirm", (dialogInterface, i) ->{
                FirebaseAPI.rootRef.child("UserAlbum/"+uid).child(keys.get(position)).getRef().
                        removeValue().addOnCompleteListener(task -> {
                    images.remove(position);
                    keys.remove(position);
                    ((ViewHolder) holder).delete.setVisibility(View.INVISIBLE);
                    this.notifyDataSetChanged();
                });
            });

            builder.setNegativeButton("Cancel",((dialogInterface, i) ->
                    dialogInterface.cancel()));
            builder.create().show();});
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
