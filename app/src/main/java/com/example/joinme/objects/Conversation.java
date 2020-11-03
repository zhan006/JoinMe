package com.example.joinme.objects;

import java.time.LocalTime;
import java.util.List;

public class Conversation {
    private String name;
    private Message latestMessage;
    private List<Message> messageList;
    public Conversation(String name){
        this.name = name;
    };
    public Conversation(String name, Message msg){
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
