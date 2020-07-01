package com.github.model;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class ScheduledRoute {
    private int scheduledID;
    private TimeProcess startTime;
    private TimeProcess endTime;
    private TimeProcess duration;
    private int organizer;
    private float distance;
    private TimeProcess delay;
    private String delayMessage;
    private int price;
    private int station_from;
    private int station_to;
    private int routeID;
    private int vehicle_Id;

    private String driver;



    public int getRouteID() {
        return routeID;
    }

    public TimeProcess getStartTime() {
        return startTime;
    }

    public TimeProcess getEndTime() {
        return endTime;
    }

    public TimeProcess getDuration() {
        return duration;
    }

    public float getDistance() {
        return distance;
    }

    public int getPrice() {
        return price;
    }

    public int getStation_from() {
        return station_from;
    }

    public int getStation_to() {
        return station_to;
    }

    public ScheduledRoute(ResultSet rs) {

        try {
            scheduledID = rs.getInt(1);
            startTime = new TimeProcess(rs.getTime(2));
            endTime = new TimeProcess(rs.getTime(3));;
            duration = new TimeProcess(rs.getTime(4));;
            organizer = rs.getInt(5);;
            distance = rs.getFloat(6);
            if(rs.getTime(7)!= null){
                delay = new TimeProcess(rs.getTime(7));
                delayMessage = rs.getString(8);
            }
            price = rs.getInt(9);
            station_from = rs.getInt(10);
            station_to =rs.getInt(11);
            routeID = rs.getInt(12);;
            vehicle_Id = rs.getInt(13);;


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public boolean isTimeBetween(Time t){

        return t.before(endTime) && t.after(startTime);
    }

    public String getDelay() {
        return "Late for " +delay.getMinute() + " min: " + delayMessage;
    }

    public String getDelayMessage() {
        return delayMessage;
    }
}
