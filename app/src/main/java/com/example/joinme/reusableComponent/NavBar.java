package com.example.joinme.reusableComponent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.fragments.ProfileFragment;
import com.example.joinme.profileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class NavBar extends LinearLayout {
    private BottomNavigationView nav;
    public NavBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.navbar,this);
        nav = (BottomNavigationView)findViewById(R.id.bottom_navigation);

    }
    public void setNavigationItemListener(FragmentManager manager){
        nav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tab_account:
                    manager.beginTransaction().replace(R.id.main_fragment_container,new ProfileFragment()).commit();
                    break;
            }
            return true;
        });
    }
}
