package com.example.joinme.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.AlbumAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Album_fragment extends Fragment {
    private TitleBar bar;
    private RecyclerView album;
    private ArrayList<String> imageUrls,imageKeys;
    private String uid;
    private AlbumAdapter adapter;
    public Album_fragment(String uid){
        this.uid = uid;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.activity_photo_gallery,container,false);
        imageUrls = new ArrayList<String>();
        imageKeys = new ArrayList<String>();
        initView(view);
        initAlbum();
        return view;
    }
    public void initView(View v){
        bar = v.findViewById(R.id.album_title);
        album = v.findViewById(R.id.album);
        bar.setOnClickBackListener((view)->{
            getActivity().getSupportFragmentManager().popBackStack();
        });
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        album.setLayoutManager(lm);
        Log.d("album_frag",uid);
        boolean isYou = uid.equals(((MainActivity) getActivity()).getUid());
        Log.d("album","isYou"+isYou);
        adapter = new AlbumAdapter(imageUrls,imageKeys,getContext(),uid,isYou);
        album.setAdapter(adapter);
    }
    public void initAlbum(){
        FirebaseAPI.rootRef.child("UserAlbum/"+uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String url = snapshot.getValue(String.class);
                String key = snapshot.getKey();
                Log.d("Profile", "onDataChange: photoUrl => "+url);
                imageUrls.add(url);
                imageKeys.add(key);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
