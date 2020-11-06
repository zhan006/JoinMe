package com.example.joinme.objects;

import java.time.LocalTime;
import java.util.List;

public class Conversation {
//    private boolean seen;
    private Time time;
    private String username;

    public Conversation() {
    }

    public Conversation(Time time, String username) {
//        this.seen = seen;
        this.time = time;
        this.username = username;
    }

//    public boolean isSeen() {
//        return seen;
//    }
//
//    public void setSeen(boolean seen) {
//        this.seen = seen;
//    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
