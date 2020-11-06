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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.adapter.ChatListAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Conversation;
import com.example.joinme.objects.Message;
import com.example.joinme.objects.Time;
import com.example.joinme.objects.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private String currentUid;
    private RecyclerView chatListRecyclerView;
    private FirebaseRecyclerAdapter<Conversation, ChatListAdapter.ChatListViewHolder> chatListAdapter;
    private ImageButton addFriendBtn;
    private EditText searchCondition;
    private ImageButton searchBtn;
    private DatabaseReference conversationListRef;
    private ChatListAdapter adapter;
    private User user;

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
        conversationListRef = FirebaseAPI.rootRef.child("ConversationList").child(currentUid);

        initView(v);
        initData();

        addFriendBtn.setOnClickListener(v1 -> addFriendFragment());
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target = searchCondition.getText().toString();
               searchConversation(target);
            }
        });
        return v;
    }

    private void initView(View v) {
        searchBtn = v.findViewById(R.id.search_button);
        searchCondition = v.findViewById(R.id.search_text);
        chatListRecyclerView = v.findViewById(R.id.friends);
        addFriendBtn = v.findViewById(R.id.icon_btn);
    }

    private void initData() {
        searchCondition.setHint(" search by username");

        // set up linear layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);

        // order all messages by timestamp
        Query chatListQuery = conversationListRef.orderByChild("time/timestamp");
        user = ((MainActivity) getContext()).getUser();
        adapter = new ChatListAdapter(getContext(), user);
        chatListAdapter = adapter.chatListAdaptor(chatListQuery);
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

    private void searchConversation(String target) {
        Log.d(TAG, "searchConversation: search on => "+target);
        // order all messages by timestamp
        Query chatListQuery;
        if (target.equals("")) {
            // display all conversations
            chatListQuery = conversationListRef.orderByChild("time/timestamp");
        } else {
            // order by search conditions
            chatListQuery = conversationListRef.orderByChild("username").startAt(target).endAt(target+"\uf8ff");
        }
        Log.d(TAG, "searchConversation: chatListQuery => "+chatListQuery.toString());
        chatListAdapter = adapter.chatListAdaptor(chatListQuery);
        chatListRecyclerView.setAdapter(chatListAdapter);
        chatListAdapter.startListening();
    }

}
