package com.example.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.joinme.database.FirebaseAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        // get database info
        ref = FirebaseDatabase.getInstance().getReference("user");

        // get the basic information from the view
        mAuth = FirebaseAuth.getInstance();
        EditText account = (EditText)findViewById(R.id.register_account_text);
        EditText email = (EditText)findViewById(R.id.register_email_text);
        EditText password = (EditText)findViewById(R.id.register_password_text);
        EditText confirm = (EditText)findViewById(R.id.register_confirm_text);

        // disable the register button until the checkbox is checked
        Button registerButton = (Button)findViewById(R.id.register_button);
        CheckBox checkBox = (CheckBox)findViewById(R.id.register_checkbox);
        if (!checkBox.isChecked()){
            registerButton.setEnabled(false);
        }else {
            registerButton.setEnabled(true);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    registerButton.setEnabled(true);
                }else {
                    registerButton.setEnabled(false);
                }
            }
        });

        // register a new user to this APP
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get all the input string
                String accountString = account.getText().toString();
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String confirmString = confirm.getText().toString();

                if (passwordString.equals(confirmString)){
                    // password is conformed
                    FirebaseAPI.signUp(emailString,passwordString).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "sign up successful.",
                                        Toast.LENGTH_SHORT).show();

                                user = mAuth.getCurrentUser();
                                String UID = user.getUid();

                                // create user in the database
                                String userPath = "User/" + UID;
                                Map<String, Object> messagePush = new HashMap<>();
                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put("username", accountString);
                                messagePush.put(userPath, userInfo);
                                DatabaseReference.CompletionListener batchCompletionListener = (error, ref) -> {
                                    if (error != null){
                                        Log.d("SEND_CHAT_MESSAGE_ERROR", error.getMessage().toString());
                                    }
                                };
                                FirebaseAPI.updateBatchData(messagePush, batchCompletionListener);

                                // turn to the main activity
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.putExtra("UID", UID);
                                startActivity(intent);
                            }else {
                                Toast.makeText(RegisterActivity.this, "sign up failed. Password should be more than 5 letters",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check the two password is same
        EditText passwordText1 = (EditText)findViewById(R.id.register_password_text);
        passwordText1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
        EditText passwordText2 = (EditText)findViewById(R.id.register_confirm_text);
        passwordText2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);

        // set the password visible ans invisable
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