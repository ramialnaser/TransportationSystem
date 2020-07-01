package com.github.model;


import java.util.Calendar;

public class TwoPointsMoving {
    private ScheduledRoute t;
    private Station from_station;
    private Station to_station;
    private Vector2D from;
    private Vector2D to;
    private Vector2D position;

    public TwoPointsMoving(ScheduledRoute t){
        this.t = t;
        from_station = Destinations.getInstance().getStations().get(t.getStation_from());
        to_station = Destinations.getInstance().getStations().get(t.getStation_to());
        this.from = new Vector2D(from_station.getPosition());
        this.to = new Vector2D(to_station.getPosition());
        calculatePosition();
    }



    public Vector2D getPosition() {
        return position;
    }

    public Vector2D getTo() {
        return to;
    }

    public void setTo(Vector2D to) {
        this.to = to;
    }

    public void calculatePosition() {
        Calendar calendar = Calendar.getInstance();
        double timeFromStart = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND)/60d - t.getStartTime().getMinute());
        if(timeFromStart<0){
            timeFromStart += 60;
        }
        timeFromStart /= t.getDuration().getMinute();
        double distanceFromStart = timeFromStart*from.distance(to);


        double xMove = Math.abs((distanceFromStart * Math.sin(from.angle(to))));
        double yMove = Math.abs((distanceFromStart * Math.cos(from.angle(to))));
        if(from.getX()> to.getX()){
            xMove *=-1d;
        }
        if(from.getY()> to.getY()){
            yMove *=-1d;
        }
        position = new Vector2D(xMove+from.getX(),yMove+from.getY());


    }

    @Override
    public String toString() {
        String a = "From: " + from_station.getName() +
            " To: " + to_station.getName();
        if( t.getDelayMessage()!=null){
            a += ", " + t.getDelay();
        }
        return a;
    }
}
