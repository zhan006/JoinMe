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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private static String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    CheckBox rememberMe;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

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
                    Snackbar.make(findViewById(R.id.login_layout),"Please input your account and password",
                            Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Snackbar.make(findViewById(R.id.login_layout),"Pending Login Process",Snackbar.LENGTH_SHORT).show();
                    FirebaseAPI.login(accountString,passwordString).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Snackbar.make(findViewById(R.id.login_layout),"Success!",Snackbar.LENGTH_SHORT).show();

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
                                Snackbar.make(findViewById(R.id.login_layout), "Sign in failed. Check the account and password",
                                        Snackbar.LENGTH_SHORT).show();
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
        /*
        Google account sign in
         */
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // set img click listener
        ImageView imageViewGoogle = (ImageView) findViewById(R.id.login_google);
        imageViewGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this, "you clicked google",
//                        Toast.LENGTH_SHORT).show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(LoginActivity.this, "on result",
//                Toast.LENGTH_SHORT).show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//                Toast.makeText(LoginActivity.this, "xia yi bu sign in",
//                        Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, "sign in with google successful.",
                                    Toast.LENGTH_SHORT).show();

                            // get UID from current user
                            currentUser = mAuth.getCurrentUser();
                            String UID = currentUser.getUid();
                            String name = currentUser.getDisplayName();
//                            Toast.makeText(LoginActivity.this, "name: " + name,
//                                    Toast.LENGTH_SHORT).show();

                            // create user in the database
                            String userPath = "User/" + UID;
                            FirebaseAPI.getFirebaseData(userPath, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild("username")){
//                                        Toast.makeText(LoginActivity.this, "Don't have",
//                                                Toast.LENGTH_SHORT).show();
                                        Map<String, Object> messagePush = new HashMap<>();
                                        Map<String, String> userInfo = new HashMap<>();
                                        userInfo.put("username", name);
                                        messagePush.put(userPath, userInfo);
                                        DatabaseReference.CompletionListener batchCompletionListener = (error, ref) -> {
                                            if (error != null){
                                                Log.d("SEND_CHAT_MESSAGE_ERROR", error.getMessage().toString());
                                            }
                                        };
                                        FirebaseAPI.updateBatchData(messagePush, batchCompletionListener);
                                    }else {
//                                        Toast.makeText(LoginActivity.this, "Already have",
//                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            // send uid to the main activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("UID", UID);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Sign in failed. Check the google account",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String UID = currentUser.getUid();

            // send uid to the main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("UID", UID);
            startActivity(intent);
        }
    }
    // [END on_start_check_user]
}