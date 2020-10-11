package com.example.joinme.objects;

public class Time {
    private int hour,minute;
    public Time(int hour,int minute){
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public String toString() {
        return hour +":"+minute;
    }
}
