package com.example.joinme.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // monitor firebase messages
        loadMessages();

        // send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                // clear input text after sending message
                inputText.setText("");
            }
        });

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
    }

    // hardcode testing data
    void initMessages() {
        messageList.add(new Message("hello message 1", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));
        messageList.add(new Message("hello message 1-1", "text",
                "dVPWSkIeVHT3SPDfSMYPbAf52Pz2", new Time(), false));
        messageList.add(new Message("hello message 2", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));
        messageList.add(new Message("hello message 3", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));

        messageList.add(new Message("hello message 3-3", "text",
                "dVPWSkIeVHT3SPDfSMYPbAf52Pz2", new Time(), false));

        messageList.add(new Message("hello message 4", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));

        messageList.add(new Message("hello message 5", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));
        messageList.add(new Message("hello message 6", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));
        messageList.add(new Message("hello message 7", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false));
        Message message = new Message("hello message 8", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(), false);
        messageList.add(message);

        Log.d(TAG, "onDataChange: datasnapshot message => "+message.toString());
    }

    void loadMessages() {
        FirebaseAPI.rootRef.child("Chat").child(this.currentUid).child(this.friendUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageID = snapshot.getKey();
                Log.d(TAG, "onDataChange: message key => " + messageID);
                Message msg = snapshot.getValue(Message.class);

                Log.d(TAG, "onDataChange: datasnapshot message => " + msg.toString());
                messageList.add(msg);
                // notify adapter to update
                messageAdapter.notifyDataSetChanged();

                // make sure user can see latest message even when typing
                messageRecyclerView.scrollToPosition(messageList.size() - 1);

                // update last message time
                TextView dateTime = findViewById(R.id.chat_time);
                dateTime.setText(msg.getTime().toString());
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

    void sendMessage() {
        String sendText = inputText.getText().toString();

        // empty input cannot be send
        if (TextUtils.isEmpty(sendText)) {
            Toast.makeText(this, "Please enter text first...", Toast.LENGTH_SHORT).show();
        } else {
            String currentUserChatPath = "Chat/" + currentUid + "/" + friendUid;
            String friendChatPath = "Chat/" + friendUid + "/" + currentUid;

            // get unique message ID for current message
            String messageID = FirebaseAPI.pushFirebaseNode(currentUserChatPath);
            Time time = new Time();

            // create current message
            Message message = new Message(sendText, "text", currentUid, time, false);
            Log.d(TAG, "sendMessage: new time => "+ time.toString());

            // Push current message to both current user's chat and friend's chat path
            Map<String, Object> messagePush = new HashMap<>();
            messagePush.put(currentUserChatPath + "/" + messageID, message);
            messagePush.put(friendChatPath + "/" + messageID, message);

            DatabaseReference.CompletionListener batchCompletionListener = (error, ref) -> {
                if (error != null){
                    Log.d("SEND_CHAT_MESSAGE_ERROR", error.getMessage().toString());
                }
            };
            FirebaseAPI.updateBatchData(messagePush, batchCompletionListener);
        }


    }
}
