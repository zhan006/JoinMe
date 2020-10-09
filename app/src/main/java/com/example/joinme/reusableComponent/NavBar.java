package com.example.joinme.reusableComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.joinme.R;
import com.example.joinme.fragments.HomePageFragment;
import com.example.joinme.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavBar extends LinearLayout {
    private BottomNavigationView nav;
    private int selected,prev_selected;
    public NavBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.navbar,this);
        nav = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        FragmentManager fm = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        setNavigationItemListener(fm);
    }
    public void setNavigationItemListener(FragmentManager manager){
        nav.setOnNavigationItemSelectedListener(item -> {
            prev_selected = selected;
            switch (item.getItemId()) {
                case R.id.tab_account:
                    selected = R.id.tab_account;
                    ProfileFragment pro_frag = new ProfileFragment();
                    manager.beginTransaction().replace(R.id.main_fragment_container,pro_frag).commit();

                    break;
                case R.id.tab_home:
                    HomePageFragment home_frag = new HomePageFragment();
                    manager.beginTransaction().replace(R.id.main_fragment_container,home_frag).commit();
                    selected = R.id.tab_home;
                    break;
                case R.id.tab_event:
                    selected=R.id.tab_event;
                    break;
                case R.id.tab_friend:
                    selected=R.id.tab_friend;
                    break;

            }
            return true;
        });
    }
    public void setSelectedItem(int id){
        nav.findViewById(id).setSelected(true);
        selected = id;
    }
    public int getPrevSelected(){
        return prev_selected;
    }
}
