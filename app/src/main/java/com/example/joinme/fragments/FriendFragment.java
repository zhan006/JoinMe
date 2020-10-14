package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
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
        int prev = ((NavBar)getActivity().findViewById(R.id.navbar)).getPrevSelected();
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
        friends.setAdapter(new FriendAdapter(initFriend()));
        return v;
    }

    List<Friend> initFriend(){
        ArrayList<Friend> friendArrayList = new ArrayList<>();
        friendArrayList.add(new Friend("Abby",new Message("Yuema",new Time(23,33))));
        friendArrayList.add(new Friend("Jinping",new Message("Laqingdan",new Time(22,33))));
        friendArrayList.add(new Friend("Ming",new Message("gou",new Time(12,33))));
        friendArrayList.add(new Friend("Shit",new Message("niubiniubi",new Time(23,33))));
        friendArrayList.add(new Friend("Shino",new Message("Nilaidangzhuxi",new Time(13,32))));
        return friendArrayList;
    }
}
