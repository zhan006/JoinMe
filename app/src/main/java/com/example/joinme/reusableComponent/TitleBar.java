package com.example.joinme.reusableComponent;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.joinme.R;


public class TitleBar extends LinearLayout {
    private static final String TAG = "TitleBar";
    private AttributeSet attrs;
    private ImageButton back;
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        LayoutInflater.from(context).inflate(R.layout.titlebar,this);
        String newTitle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","title");
        String backable = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","backable");

        Drawable icon;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        icon = ta.getDrawable(R.styleable.TitleBar_icon);

        back = this.findViewById(R.id.backBtn);
        ImageButton iconBtn = findViewById(R.id.icon_btn);
        setTitle(newTitle);
        setIcon(iconBtn, icon);

        if (backable.equals("false")){
            back.setVisibility(INVISIBLE);
        }

    }
    public void setTitle(String title){
        TextView text = (TextView)findViewById(R.id.title);
        text.setText(title);
    }
    public void setOnClickBackListener(OnClickListener listener){
        back.setOnClickListener(listener);
    }

    public void setIcon(ImageButton iconBtn, Drawable icon) {
        iconBtn.setImageDrawable(icon);
        // set title bar icon size
        iconBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iconBtn.setPadding(30,15,30,15);
    }
}
