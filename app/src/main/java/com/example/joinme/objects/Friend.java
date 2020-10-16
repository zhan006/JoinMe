package com.example.joinme.objects;

import java.time.LocalTime;
import java.util.List;

public class Friend {
    private String name;
    private Message latestMessage;
    private List<Message> messageList;
    public Friend(String name){
        this.name = name;
    };
    public Friend(String name, Message msg){
        this.name = name;
        this.latestMessage = msg;
    };
    public String getName() {
        return name;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }
}
