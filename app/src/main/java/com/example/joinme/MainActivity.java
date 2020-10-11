package com.example.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        FragmentManager fm = getSupportFragmentManager();
        if(actionbar != null) actionbar.hide();
        NavBar nav = (NavBar) findViewById(R.id.navbar);
        nav.setSelectedItem(R.id.tab_home);

        /*
         * Firebase testing demo
         */
        FirebaseAPI.setFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43/username", "Giovanna")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String username = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: username = "+username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43/username", userListener);
    }
}