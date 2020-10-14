package com.example.joinme.fragments;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePageFragment extends Fragment {
    private ImageView icon;
    private TextView welcome;
    private Button organise, search;
    private final String WELCOME = "Welcome ";
    private final int UPDATE_MSG = 1;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_MSG:
                    Bundle bd = msg.getData();
                    User user = (User) bd.getSerializable("user");
                    welcome.setText(WELCOME+user.getLastName()+" "+user.getFirstName());
                    icon.setImageResource(R.drawable.photo);

            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.activity_home,container,false);
        icon = view.findViewById(R.id.icon);
        welcome = view.findViewById(R.id.welcome_name);
        organise = view.findViewById(R.id.organize_event);
        search = view.findViewById(R.id.search_event);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(() -> {
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    //String username = dataSnapshot.getValue(String.class);
                    HashMap data = (HashMap) dataSnapshot.getValue();
                    User user = new User((String) data.get("firstName"),
                            (String) data.get("lastName"),
                            (String) data.get("profileImage"),
                            (String) data.get("username"),(String)data.get("about"), new ArrayList<String>());
                    Message msg = new Message();
                    msg.what = UPDATE_MSG;
                    Bundle bd = new Bundle();
                    bd.putSerializable("user",user);
                    msg.setData(bd);
                    handler.sendMessage(msg);
                    //Log.d(TAG, "onDataChange: username = "+username);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("home", "loadPost:onCancelled", databaseError.toException());
                }
            };

            //FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43/username", userListener);
            FirebaseAPI.getFirebaseData("User/qa6KACdJ0RYZfVDXLtpKL2HcxJ43", userListener);
        }).start();

    }
}
