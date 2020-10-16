package com.example.joinme.objects;

import android.util.Log;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Time {
    private static final String TAG = "Time";
    private String date, time;
    private Map<String, String> timestamp;

    public Time() {
        this.date = getDate();
        this.time = getTime();
        this.timestamp = getTimestamp();
    }

    public Time(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public Time(String date, String time,Long timestamp) {
        this.date = date;
        this.time = time;
        setTimestamp(timestamp);
    }

    /**
     * Generate current date
     * @return current date
     */
    public String getDate() {
        // get current date in following format
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDateFormat.format(calendar.getTime());
        return date;
    }

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        // in 12-hour format with am/pm

        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        time = currentTimeFormat.format(calendar.getTime());
        return time;
    }

    public Map<String, String> getTimestamp() {
        // timestamp created by firebase is a map by default
        return ServerValue.TIMESTAMP;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = new HashMap<>();
        // firebase will convert timestamp to long
        // but store string for consistency
        this.timestamp.put("timestamp", timestamp.toString());
    }

    @Override
    public String toString() {
        return date +", "+time;
    }
}
