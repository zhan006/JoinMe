package com.example.joinme.objects;

public class Event {
    private String eventName,location,datetime;
    public Event(String name,String location,String datetime){
        this.eventName = name;
        this.location = location;
        this.datetime = datetime;
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
}
