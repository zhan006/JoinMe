package com.example.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.Script;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joinme.database.FirebaseAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private static String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        EditText passwordText = (EditText)findViewById(R.id.login_password_text);
        passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);

        // set the password visible and invisible
        ImageView visibleButton = (ImageView)findViewById(R.id.visible_button);
        visibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                    visibleButton.setImageResource(R.drawable.invisible);
                }else {
                    passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    visibleButton.setImageResource(R.drawable.visible);
                }
            }
        });

        // turn to register page
        TextView goToRegister = (TextView)findViewById(R.id.without_account_text);
        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // get input information
        EditText account = (EditText)findViewById(R.id.login_account_text);
        EditText password = (EditText)findViewById(R.id.login_password_text);

        /*
        Account and password already filled in, ONLY for test use, MUST DELETE when APP released
         */
//        account.setText("blackpink@gmail.com");
//        password.setText("blackpink");

        // remember the account and password
        rememberMe = (CheckBox)findViewById(R.id.checkbox_1);
        SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
        String strJudge = settings.getString("judgeText", "no");
        String strUserName = settings.getString("userNameText", "");
        String strPassword = settings.getString("passwordText", "");
        if(strJudge.equals("yes")){
            rememberMe.setChecked(true);
            account.setText(strUserName);
            password.setText(strPassword);
        }else{
            rememberMe.setChecked(false);
            account.setText("");
            password.setText("");
        }

        // add checkbox listener
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
                if(isChecked){
                    settings.edit().putString("judgeText", "yes")
                            .putString("userNameText", account.getText().toString())
                            .putString("passwordText", password.getText().toString())
                            .apply();
                }else{
                    settings.edit().putString("judgeText", "no")
                            .putString("UserNameText", "")
                            .putString("passwordText", "")
                            .apply();
                }
            }
        });

        // turn to main activity
        mAuth = FirebaseAuth.getInstance();
        Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountString = account.getText().toString();
                String passwordString = password.getText().toString();
                if(accountString.equals("") || passwordString.equals("")){
                    Toast.makeText(LoginActivity.this,"Please input your account and password",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseAPI.login(accountString,passwordString).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "sign in successful.",
                                        Toast.LENGTH_SHORT).show();

                                // check the remember me setting
                                if (rememberMe.isChecked()){
                                    settings.edit().putString("judgeText", "yes")
                                            .putString("userNameText", account.getText().toString())
                                            .putString("passwordText", password.getText().toString())
                                            .apply();
                                }else {
                                    settings.edit().putString("judgeText", "no")
                                            .putString("UserNameText", "")
                                            .putString("passwordText", "")
                                            .apply();
                                }

                                // get UID from current user
                                currentUser = mAuth.getCurrentUser();
                                String UID = currentUser.getUid();

                                // send uid to the main activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("UID", UID);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Sign in failed. Check the account and password",
                                        Toast.LENGTH_SHORT).show();
                                settings.edit().putString("judgeText", "no")
                                        .putString("UserNameText", "")
                                        .putString("passwordText", "")
                                        .apply();
                            }
                        }
                    });
                }
                // using Firebase API to check a user's Existing status

            }
        });
    }
}