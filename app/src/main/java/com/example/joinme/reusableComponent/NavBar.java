package com.example.joinme.reusableComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.joinme.R;
import com.example.joinme.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                    ProfileFragment pro_frag = new ProfileFragment();
                    manager.beginTransaction().replace(R.id.main_fragment_container,pro_frag).commit();
                    break;
            }
            return true;
        });
    }
}
