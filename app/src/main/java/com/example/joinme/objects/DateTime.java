package com.example.joinme.objects;

import android.util.Log;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

public class DateTime implements Comparable<DateTime>{
    String date,time;
    Long timeStamp;
    private Calendar cal_date;
    private DateFormat dateformatter = new SimpleDateFormat("MMM dd, yyyy");
    private DateFormat timeformmatter = new SimpleDateFormat("hh:mm a");
    public DateTime(){
        cal_date = Calendar.getInstance();
        timeStamp = cal_date.getTimeInMillis();
    }
    public DateTime(int year, int month, int day, int hour,int minute){
        cal_date = Calendar.getInstance();
        cal_date.set(year,month,day,hour,minute);
        timeStamp = cal_date.getTimeInMillis();
    }
    public DateTime(String date,String time,Long timeStamp){
        this.date = date;
        this.time = time;
        this.timeStamp = timeStamp;
        cal_date.setTimeInMillis(timeStamp);
    }

    public DateTime(HashMap datetime) {
        cal_date = Calendar.getInstance();
        timeStamp = (Long) datetime.get("timeStamp");
        cal_date.setTimeInMillis(timeStamp);
    }

    public String getDate(){ return dateformatter.format(cal_date.getTime());}
    public String getTime(){return timeformmatter.format(cal_date.getTime());}
    public Long getTimeStamp(){return timeStamp;}
    public void setDate(String date){this.date = date;}
    public void setTime(String time){this.time = time;}
    public void setTimeStamp(Long timeStamp){this.timeStamp = timeStamp;}
    public String toString(){
        return getDate()+", "+getTime();
    }

    @Override
    public int compareTo(DateTime o) {
        if(this.getTimeStamp().intValue()<o.getTimeStamp().intValue()){
            return -1;
        }else if(this.getTimeStamp().intValue()==o.getTimeStamp().intValue()){
            return 0;
        }else{
            return 1;
        }
    }
}
