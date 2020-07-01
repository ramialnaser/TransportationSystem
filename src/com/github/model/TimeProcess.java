package com.github.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeProcess extends Time {
    int hour;
    int minute;

    public TimeProcess(Time time) {
        super(time.getTime());
        hour = time.toLocalTime().getHour();
        minute = time.toLocalTime().getMinute();
    }

    public static TimeProcess now(int hour) throws ParseException {
        return new TimeProcess(generateTime(hour,Calendar.getInstance().get(Calendar.MINUTE)));
    }

    public void addTime(TimeProcess otherTime) throws ParseException  {
        minute += otherTime.getMinute();
        hour += otherTime.getHour();
        if(minute>59){
            minute-=60;
            hour+=1;
            if(hour ==24){
                hour =0;
            }
        }
        super.setTime(generateTime().getTime());
    }
    public void subtractTime(TimeProcess otherTime) throws ParseException{
        minute -=otherTime.getMinute();
        hour -=otherTime.getHour();
        if(minute<0){
            minute +=60;
            hour -=1;
            if(hour == -1){
                hour = 23;
            }
        }
        super.setTime(generateTime().getTime());
    }


    public Time generateTime() throws ParseException{

        String t = hour + ":"+minute+":00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        long ms = simpleDateFormat.parse(t).getTime();
        return new Time(ms);
    }

    public static Time generateTime(int hour, int minute) throws ParseException{

        String t = hour +":"+minute+":00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        long ms = simpleDateFormat.parse(t).getTime();
        return new Time(ms);
    }





    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
