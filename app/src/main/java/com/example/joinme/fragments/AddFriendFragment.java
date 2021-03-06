package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.AddFriendAdapter;
import com.example.joinme.adapter.SearchConditionAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class AddFriendFragment extends Fragment {
    private static final String TAG = "AddFriendFragment";
    private List<User> userList = new ArrayList<>();

    private FirebaseRecyclerAdapter<User, AddFriendAdapter.ViewHolder> addFriendAdapter;
    private RecyclerView userRecyclerView;
    private AddFriendAdapter adapter;
    private User user;
    private EditText searchCondition;
    private ImageButton searchBtn;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar)getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_friend, container, false);
        searchCondition = v.findViewById(R.id.search_text);
        searchCondition.setHint("search by username");
        searchBtn = v.findViewById(R.id.search_button);
        TitleBar bar = v.findViewById(R.id.add_friend_title);
        bar.setOnClickBackListener((view)->{
            getActivity().getSupportFragmentManager().popBackStack();
        });

        initUserList(v);
//        initGenderList(v);
//        initAgeList(v);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByName(searchCondition.getText().toString());
            }
        });
        return v;
    }

    private void initUserList(View v) {
        userRecyclerView = v.findViewById(R.id.add_friends_list);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user = ((MainActivity) getContext()).getUser();
        adapter = new AddFriendAdapter(getContext(), user);
        addFriendAdapter = adapter.addFriendAdaptor(FirebaseAPI.rootRef.child("User"));
        userRecyclerView.setAdapter(addFriendAdapter);
        addFriendAdapter.startListening();
    }

//    private void initGenderList(View v) {
//        RecyclerView genderRecyclerView = v.findViewById(R.id.gender_search_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        genderRecyclerView.setLayoutManager(layoutManager);
//        genderRecyclerView.setAdapter(new SearchConditionAdapter(initGender()));
//    }
//
//    private void initAgeList(View v) {
//        RecyclerView ageRecyclerView = v.findViewById(R.id.age_search_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        ageRecyclerView.setLayoutManager(layoutManager);
//        ageRecyclerView.setAdapter(new SearchConditionAdapter(initAge()));
//    }

    void loadUsers() {
        FirebaseAPI.rootRef.child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                Log.d(TAG, "onDataChange: User => "+user.toString());
                userList.add(user);
                addFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    List<String> initGender() {
        ArrayList<String> genders = new ArrayList<>();
        genders.add("Girl");
        genders.add("Boy");

        return genders;
    }

    List<String> initAge() {
        ArrayList<String> age = new ArrayList<>();
        age.add("16 - 18");
        age.add("19 - 20");
        age.add("21 - 22");
        age.add("> 22");
        return age;
    }

    private void searchByName(String target) {
        Query userQuery;
        if (target.equals("")) {
            userQuery = FirebaseAPI.rootRef.child("User");
        } else {
            userQuery = FirebaseAPI.rootRef.child("User").orderByChild("username").startAt(target).endAt(target+"\uf8ff");
        }
        addFriendAdapter = adapter.addFriendAdaptor(userQuery);
        userRecyclerView.setAdapter(addFriendAdapter);
        addFriendAdapter.startListening();
    }
}
