package com.example.joinme.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.MessageAdapter;
import com.example.joinme.objects.Message;
import com.example.joinme.objects.Time;
import com.example.joinme.reusableComponent.TitleBar;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

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
        initMessages();
        // set up adapter for message recycler view
        messageAdapter = new MessageAdapter(messageList, this, currentUid, friendUid);
        messageRecyclerView.setAdapter(messageAdapter);
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
        messageList.add(new Message("hello message 8", "text",
                "qa6KACdJ0RYZfVDXLtpKL2HcxJ43", new Time(10, 30), false));

    }
}
