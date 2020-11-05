package com.example.joinme.activity;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.joinme.R;
import com.example.joinme.adapter.photoAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PhotoActivity extends AppCompatActivity {
    private TitleBar bar;
    private ImageView image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_photo);
        bar = findViewById(R.id.photo_title);
        bar.setOnClickBackListener((v)->{
            this.finish();
        });
        image = findViewById(R.id.photo);
        String imageURL= getIntent().getStringExtra("url");
        Glide.with(this).load(imageURL).into(image);
    }
}
