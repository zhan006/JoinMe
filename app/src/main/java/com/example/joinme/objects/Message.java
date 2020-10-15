package com.example.joinme.objects;
import java.util.Date;

public class Message {

    // message position type
    public static final int LEFT_MSG_TYPE = 0;
    public static final int RIGHT_MSG_TYPE = 1;
    private String content, type, from;
    private Time time;
    private boolean seen;

    public Message(String content, String type, String from, Time time, boolean seen) {
        this.content = content;
        this.type = type;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
