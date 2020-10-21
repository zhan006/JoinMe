package com.example.joinme.objects;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.security.spec.ECField;
import java.util.HashMap;

public class Event implements Serializable {
    private String eventName,description,eventCategory,organizerid,address,id;
    private location location;
    private DateTime datetime;
    public Event(String name,location location,DateTime datetime,String category,String userid,String description,String id){
        this.eventName = name;
        this.location = location;
        this.datetime = datetime;
        this.eventCategory = category;
        this.organizerid = userid;
        this.description = description;
        this.id = id;
    }
    public Event(String name,location location,DateTime datetime,String category,String userid,String description){
        this.eventName = name;
        this.location = location;
        this.datetime = datetime;
        this.eventCategory = category;
        this.organizerid = userid;
        this.description = description;
        this.id = id;
    }
    public Event(HashMap data){
        this.eventName = (String)data.get("name");
        try{
            this.datetime = (DateTime)data.get("time");
        }catch (Exception e){
        }

        this.description = (String)data.get("description");
        this.eventCategory = (String)data.get("eventCategory");
        this.organizerid = (String)data.get("organizerID");

    }

    public Event() {

    }

    public Event(String name, String address, DateTime time) {
        eventName = name;
        this.address = address;
        this.datetime = time;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){this.id = id;}
    public DateTime getDatetime() {
        return datetime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getOrganizerid() {
        return organizerid;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public location getLocation() {
        return location;
    }
    public void setOrganizerid(String organizerid) {
        this.organizerid = organizerid;
    }

    public void setDescription(String description){this.description = description;}
    public void setDatetime(DateTime datetime) {
        this.datetime = datetime;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public void setLocation(location location) {
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
