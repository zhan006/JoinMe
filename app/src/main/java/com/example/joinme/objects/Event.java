package com.example.joinme.objects;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

public class Event implements Serializable {
    private String eventName,location,datetime,description,eventCategory,organizerid;
    public Event(String name,String location,String datetime){
        this.eventName = name;
        this.location = location;
        this.datetime = datetime;
    }
    public Event(HashMap data){
        this.eventName = (String)data.get("name");
        this.datetime = (String)data.get("time");
        this.location = new location((HashMap)data.get("location")).address;
        this.description = (String)data.get("description");
        this.eventCategory = (String)data.get("eventCategory");
        this.organizerid = (String)data.get("organizerID");

    }

    public String getDatetime() {
        return datetime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @NonNull
    @Override
    public String toString() {
        return eventName+" "+location+" "+organizerid+" "+datetime;
    }
}
