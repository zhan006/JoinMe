package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.AddFriendAdapter;
import com.example.joinme.adapter.SearchConditionAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.NavBar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AddFriendFragment extends Fragment {
    private static final String TAG = "AddFriendFragment";
    private List<User> userList = new ArrayList<>();

    private FirebaseRecyclerAdapter<User, AddFriendAdapter.ViewHolder> addFriendAdapter;

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
        EditText searchBar = v.findViewById(R.id.search_text);
        searchBar.setHint("search by name or ID");

        initUserList(v);
//        loadUsers();
        initHobbyList(v);
        initGenderList(v);
        initAgeList(v);

        return v;
    }

    private void initUserList(View v) {
        RecyclerView userRecyclerView = v.findViewById(R.id.add_friends_list);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addFriendAdapter = new AddFriendAdapter(getContext()).addFriendAdaptor();
        userRecyclerView.setAdapter(addFriendAdapter);
        addFriendAdapter.startListening();
    }

    private void initHobbyList(View v) {
        RecyclerView hobbyRecyclerView = v.findViewById(R.id.hobby_search_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hobbyRecyclerView.setLayoutManager(layoutManager);
        hobbyRecyclerView.setAdapter(new SearchConditionAdapter(initHobbies()));
    }

    private void initGenderList(View v) {
        RecyclerView genderRecyclerView = v.findViewById(R.id.gender_search_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        genderRecyclerView.setLayoutManager(layoutManager);
        genderRecyclerView.setAdapter(new SearchConditionAdapter(initGender()));
    }

    private void initAgeList(View v) {
        RecyclerView ageRecyclerView = v.findViewById(R.id.age_search_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ageRecyclerView.setLayoutManager(layoutManager);
        ageRecyclerView.setAdapter(new SearchConditionAdapter(initAge()));
    }

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

    List<String> initHobbies() {
        ArrayList<String> hobbies = new ArrayList<>();
        hobbies.add("Guitar");
        hobbies.add("Movie");
        hobbies.add("Basketball");
        hobbies.add("Singing");
        return hobbies;
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
}
