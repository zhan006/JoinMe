package com.example.joinme.objects;

import java.time.LocalTime;
import java.util.List;

public class Conversation {
    private boolean seen;
    private Time time;

    public Conversation() {
    }

    public Conversation(boolean seen, Time time) {
        this.seen = seen;
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
