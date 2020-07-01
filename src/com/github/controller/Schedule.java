package com.github.controller;


import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Schedule extends RecursiveTreeObject<Schedule> {

    StringProperty scheduleId;
    StringProperty startTime;
    StringProperty endTime;
    StringProperty duration;
    StringProperty price;
    StringProperty from;
    StringProperty to;
    StringProperty routeId;
    StringProperty routeType;
    StringProperty vehicleId;
    StringProperty username;
    StringProperty stationName;
    StringProperty stationId;


    public Schedule(){
        super();
    }

    public Schedule(String scheduleId, String startTime, String endTime, String duration, String price, String from, String to,
                    String routeId, String routeType, String vehicleId, String username, String stationName, String stationId) {
        this.scheduleId = new SimpleStringProperty(scheduleId);
        this.startTime = new SimpleStringProperty(startTime);
        this.endTime = new SimpleStringProperty(endTime);
        this.duration = new SimpleStringProperty(duration);
        this.price = new SimpleStringProperty(price);
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
        this.routeId = new SimpleStringProperty(routeId);
        this.routeType = new SimpleStringProperty(routeType);
        this.vehicleId = new SimpleStringProperty(vehicleId);
        this.username = new SimpleStringProperty(username);
        this.stationName = new SimpleStringProperty(stationName);
        this.stationId = new SimpleStringProperty(stationId);
    }
}
