package com.example.joinme.reusableComponent;

import android.app.Activity;
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

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.profileActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class NavBar extends LinearLayout {
    private ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
    private ImageButton home,profile,event,friend;
    public NavBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.navbar,this);
        home = (ImageButton) findViewById(R.id.home);
        profile = (ImageButton) findViewById(R.id.profile);
        event = (ImageButton) findViewById(R.id.event);
        friend = (ImageButton) findViewById(R.id.friend);
        buttons.add(home);
        buttons.add(profile);
        buttons.add(event);
        buttons.add(friend);
        for(ImageButton b:buttons){
            setButtonListener(b,(View v)->{
                switch (v.getId()){
                    case R.id.profile:

                        getContext().startActivity(new Intent((Activity)getContext(),
                                profileActivity.class));
                        break;
                    case R.id.home:
                        getContext().startActivity(new Intent((Activity)getContext(),
                                MainActivity.class));
                        break;

                }
            });
        }
    }
    public void setButtonListener(ImageButton btn, OnClickListener listener){
        btn.setOnClickListener(listener);
    }
    public void setBtnSelected(String btn,boolean b){
        switch(btn){
            case "home":
                home.setSelected(b);
                break;
            case "profile":
                profile.setSelected(b);
                break;
            case "event":
                event.setSelected(b);
                break;
            case "friend":
                event.setSelected(b);
        }
    }
}
