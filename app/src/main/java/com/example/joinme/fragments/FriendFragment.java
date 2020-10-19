package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.FriendAdapter;
import com.example.joinme.objects.Friend;
import com.example.joinme.objects.Message;
import com.example.joinme.objects.Time;
import com.example.joinme.reusableComponent.NavBar;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {
    private EditText search;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.friend_activity,container,false);
        search = v.findViewById(R.id.search_text);
        RecyclerView friends = v.findViewById(R.id.friends);
        friends.setLayoutManager(new LinearLayoutManager(getContext()));
        friends.setAdapter(new FriendAdapter(initFriend(), getContext()));
        ImageButton addFriendBtn = v.findViewById(R.id.icon_btn);
        addFriendBtn.setOnClickListener(v1 -> addFriendFragment());
        return v;
    }

    List<Friend> initFriend(){
        ArrayList<Friend> friendArrayList = new ArrayList<>();
        friendArrayList.add(new Friend("Abby",new Message("Yuema","text","userA",new Time(), false)));
        friendArrayList.add(new Friend("Jinping",new Message("Laqingdan","text","userA",new Time(), false)));
        friendArrayList.add(new Friend("Ming",new Message("gou","text","userA",new Time(),false)));
        friendArrayList.add(new Friend("Shit",new Message("niubiniubi","text","userA",new Time(),false)));
        friendArrayList.add(new Friend("Shino",new Message("Nilaidangzhuxi","text","userA",new Time(),false)));
        return friendArrayList;
    }

    /**
     * Replace by Add Friend Fragment
     */
    private void addFriendFragment() {
        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();

        AddFriendFragment addFriendFragment = new AddFriendFragment();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, addFriendFragment, null)
                .addToBackStack(null)
                .commit();
    }
}
