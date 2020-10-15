package com.example.joinme;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        Button registerButton = (Button)findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        EditText passwordText1 = (EditText)findViewById(R.id.register_password_text);
        passwordText1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
        EditText passwordText2 = (EditText)findViewById(R.id.register_confirm_text);
        passwordText2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);

        ImageView visibleButton1 = (ImageView)findViewById(R.id.visible_button_2);
        visibleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordText1.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    passwordText1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                    visibleButton1.setImageResource(R.drawable.invisible);
                }else {
                    passwordText1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    visibleButton1.setImageResource(R.drawable.visible);
                }
            }
        });
        ImageView visibleButton2 = (ImageView)findViewById(R.id.visible_button_3);
        visibleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordText2.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    passwordText2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                    visibleButton2.setImageResource(R.drawable.invisible);
                }else {
                    passwordText2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    visibleButton2.setImageResource(R.drawable.visible);
                }
            }
        });
    }
}