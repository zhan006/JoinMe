package com.example.joinme.objects;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateTime {
    String date,time;
    Long timeStamp;
    private Calendar cal_date;
    private DateFormat dateformatter = new SimpleDateFormat("MMM dd, yyyy");
    private DateFormat timeformmatter = new SimpleDateFormat("hh:mm a");
    public DateTime(int year, int month, int day, int hour,int minute){
        cal_date = Calendar.getInstance();
        cal_date.set(year+1900,month,day,hour,minute);
        timeStamp = cal_date.getTimeInMillis();
    }
    public String getDate(){return dateformatter.format(cal_date);}
    public String getTime(){return timeformmatter.format(cal_date);}
    public Long getTimeStamp(){return timeStamp;}
    public void setDate(String date){this.date = date;}
    public void setTime(String time){this.time = time;}
    public void setTimeStamp(Long timeStamp){this.timeStamp = timeStamp;}

}
