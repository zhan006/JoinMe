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
import com.example.joinme.adapter.ChatListAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Conversation;
import com.example.joinme.objects.Message;
import com.example.joinme.objects.Time;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {
    private EditText search;
    private String currentUid;
    private RecyclerView chatListRecyclerView;
    private FirebaseRecyclerAdapter<Conversation, ChatListAdapter.ChatListViewHolder> chatListAdapter;
    private ImageButton addFriendBtn;

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

        currentUid = FirebaseAPI.getUser().getUid();
        initView(v);
        initData();

        addFriendBtn.setOnClickListener(v1 -> addFriendFragment());

        //        search.setOnClickListener((v1) -> {
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            utils.replaceFragment(fm, new DiscoverEventFragment(target.getText()), "discover_event_2");
//        });
        return v;
    }

    private void initView(View v) {
        search = v.findViewById(R.id.search_text);
//        ImageButton search = v.findViewById(R.id.search_button);
//        EditText target = v.findViewById(R.id.search_text);
        chatListRecyclerView = v.findViewById(R.id.friends);
        addFriendBtn = v.findViewById(R.id.icon_btn);
    }

    private void initData() {
        // set up linear layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);
//        chatList.setAdapter(new FriendAdapter(initFriend(), getContext()));
        chatListAdapter = new ChatListAdapter(getContext()).chatListAdaptor();
        chatListRecyclerView.setAdapter(chatListAdapter);
        chatListAdapter.startListening();
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
