package com.example.joinme.objects;
import java.util.Date;

public class Message {

    // message position type
    public static final int LEFT_MSG_TYPE = 0;
    public static final int RIGHT_MSG_TYPE = 1;
    private String messageContent, type, from;
    private Time timestamp;
    private Boolean seen;

    public Message() {
    }

    public Message(String content, String type, String from, Time time, boolean seen) {
        this.messageContent = content;
        this.type = type;
        this.from = from;
        this.timestamp = time;
        this.seen = seen;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String content) {
        this.messageContent = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Time getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Time time) {
        this.timestamp = time;
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

    @Override
    public String toString() {
        return "Message{" +
                "messageContent='" + messageContent + '\'' +
                ", type='" + type + '\'' +
                ", from='" + from + '\'' +
                ", timestamp=" + timestamp +
                ", seen=" + seen +
                '}';
    }
}
