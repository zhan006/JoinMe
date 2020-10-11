package com.example.joinme.objects;
import java.util.Date;

public class Message {
    private String content,from,to;
    private Time time;
    private int ID,fromID,toID;
    public Message(String content,Time time,int fromID,int toID){
        this.content = content;
        this.time = time;
        this.fromID = fromID;
        this.toID = toID;
    }
    public Message(String content,Time time){
        this.content = content;
        this.time = time;
    }

    public int getFromID() {
        return fromID;
    }

    public int getToID() {
        return toID;
    }

    public Time getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
