package com.github.controller;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaxiStation extends RecursiveTreeObject<TaxiStation> {
    StringProperty taxiId;
    StringProperty taxiStatus;
    StringProperty account_userName;
    StringProperty station_Id;
    StringProperty stationId;
    StringProperty stationName;

    public TaxiStation(){
        super();
    }

    public TaxiStation(String taxiId,String taxiStatus,String account_userName,String station_Id,String stationId, String stationName) {
        this.taxiId = new SimpleStringProperty(taxiId);
        this.taxiStatus = new SimpleStringProperty(taxiStatus);
        this.account_userName = new SimpleStringProperty(account_userName);
        this.station_Id = new SimpleStringProperty(station_Id);
        this.stationId = new SimpleStringProperty(stationId);
        this.stationName = new SimpleStringProperty(stationName);

    }
}
