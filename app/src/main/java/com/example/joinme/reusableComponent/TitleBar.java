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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.joinme.R;

import java.lang.ref.Reference;

public class TitleBar extends LinearLayout {
    private static final String TAG = "TitleBar";
    private AttributeSet attrs;
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        LayoutInflater.from(context).inflate(R.layout.titlebar,this);
        String newTitle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","title");
        String backable = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","backable");

        Drawable icon;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        icon = ta.getDrawable(R.styleable.TitleBar_icon);

        ImageButton back = (ImageButton) this.findViewById(R.id.backBtn);
        ImageButton iconBtn = findViewById(R.id.icon_btn);
        setTitle(newTitle);
        setIcon(iconBtn, icon);

        if (backable.equals("false")){
            back.setVisibility(INVISIBLE);
        } else{
            back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity)getContext()).finish();
                }
            });
        }

    }
    public void setTitle(String title){
        TextView text = (TextView)findViewById(R.id.title);
        text.setText(title);
    }

    public void setIcon(ImageButton iconBtn, Drawable icon) {
        iconBtn.setImageDrawable(icon);
    }
}
