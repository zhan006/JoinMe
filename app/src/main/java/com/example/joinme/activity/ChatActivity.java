package com.example.joinme.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.MessageAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Message;
import com.example.joinme.objects.Time;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private List<Message> messageList = new ArrayList<>();
    private String friendUid;
    private String friendUsername;
    private String currentUid;

    private MessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;

    private EditText inputText;
    private ImageButton sendBtn;
    private ImageButton imageButton;
    private RecyclerView messageRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        friendUid = getIntent().getStringExtra("friendUid");
        friendUsername = getIntent().getStringExtra("friendUsername");
        currentUid = "qa6KACdJ0RYZfVDXLtpKL2HcxJ43";

        initView();
        initData();

    }

    private void initView() {
        // set friend username in title bar
        TitleBar titleBar = findViewById(R.id.chat_title_bar);
        titleBar.setTitle(this.friendUsername);

        messageRecyclerView = findViewById(R.id.chat_message_list);
        inputText = findViewById(R.id.chat_input_message);
        sendBtn = findViewById(R.id.send_message_btn);
        imageButton = findViewById(R.id.message_send_image_btn);
    }

    private void initData() {
        // set up layout manager for message recycler view
        linearLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        //initMessages();

        // set up adapter for message recycler view
        messageAdapter = new MessageAdapter(messageList, this, currentUid, friendUid);
        messageRecyclerView.setAdapter(messageAdapter);
        loadMessages();
    }

    void initMessages() {
        messageList.add(new Message("hello message 1", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));
        messageList.add(new Message("hello message 1-1", "text",
                "dVPWSkIeVHT3SPDfSMYPbAf52Pz2", new Time(10, 30), false));
        messageList.add(new Message("hello message 2", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));
        messageList.add(new Message("hello message 3", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));

        messageList.add(new Message("hello message 3-3", "text",
                "dVPWSkIeVHT3SPDfSMYPbAf52Pz2", new Time(10, 30), false));

        messageList.add(new Message("hello message 4", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));

        messageList.add(new Message("hello message 5", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));
        messageList.add(new Message("hello message 6", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));
        messageList.add(new Message("hello message 7", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));
        Message message = new Message("hello message 8", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false);
        messageList.add(message);

        Log.d(TAG, "onDataChange: datasnapshot message => "+message.toString());
    }

    void loadMessages() {
        FirebaseAPI.rootRef.child("Chat").child(this.currentUid).child(this.friendUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageID = snapshot.getKey();
                Log.d(TAG, "onDataChange: message key => " + messageID);

                HashMap firebaseMsg = (HashMap) snapshot.getValue();
                Message message = new Message((String) firebaseMsg.get("messageContent"),
                        (String) firebaseMsg.get("type"),
                        (String) firebaseMsg.get("from"),
                        new Time(((Long)firebaseMsg.get("hour")).intValue(), ((Long)firebaseMsg.get("minute")).intValue()),
                        (boolean) firebaseMsg.get("seen"));
                Log.d(TAG, "onDataChange: datasnapshot message => " + message.toString());
                messageList.add(message);
                // notify adapter to update
                messageAdapter.notifyDataSetChanged();

                // make sure user can see latest message even when typing
                messageRecyclerView.scrollToPosition(messageList.size() - 1);
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
}
