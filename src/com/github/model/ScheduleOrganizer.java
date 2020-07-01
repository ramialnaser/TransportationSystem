package com.github.model;

import com.github.controller.Booking;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.*;


public class ScheduleOrganizer {
    HashMap<Integer, ArrayList<ScheduledRoute>> items = new HashMap<>();
    HashMap<Integer, ArrayList<ScheduledRoute>> searchResults;
    HashMap<Integer, Integer> price = new HashMap<>();


    public synchronized void addToList(Integer special_ID, ScheduledRoute myItem) {
        List<ScheduledRoute> itemsList = items.get(special_ID);
        // if list does not exist create it
        if(itemsList == null) {
            itemsList = new ArrayList<>();
            itemsList.add(myItem);
            items.put(special_ID, (ArrayList<ScheduledRoute>) itemsList);
        } else {
            // add if item is not already in list
            if(!itemsList.contains(myItem)) itemsList.add(myItem);
        }
    }

    public void findRouteContain2Value(int from, int to){ //Search for route contains 2 value
        searchResults = new HashMap<>();
        for(Map.Entry<Integer, ArrayList<ScheduledRoute>> entry : items.entrySet()) {
            int a = entry.getKey();
            for (int i = 0; i < entry.getValue().size() ; i++) {
                if(entry.getValue().get(i).getStation_from()== from){
                    for (int j = i; j < entry.getValue().size() ; j++) {
                        if(entry.getValue().get(j).getStation_to() == to){
                            ArrayList<ScheduledRoute> scheduledRoutes = new ArrayList<>(entry.getValue().subList(i,j+1));
                            int sum = sumOfArray(scheduledRoutes);
                            searchResults.put(a, scheduledRoutes);
                            price.put(a,sum);
                            break;
                        }
                    }
                }

            }
        }
    }

    private int sumOfArray(ArrayList<ScheduledRoute> arrayList){
        int a = 0;
        for (ScheduledRoute s: arrayList ) {
            a += s.getPrice();
        }
        return a;
    }

    public GridPane getText(int from, int to){
        int rowNo = 0;
        GridPane gridPane = new GridPane();
        gridPane.add(new Label("From"),0,rowNo);
        gridPane.add(new Label("Time"),1,rowNo);
        gridPane.add(new Label("To"),2,rowNo);
        gridPane.add(new Label("Time"),3,rowNo);
        gridPane.add(new Label("Price"),4,rowNo);
        gridPane.add(new Label(""),5,rowNo);
        gridPane.add(new Label(""),6,rowNo);
        gridPane.add(new Label("Note"),7,rowNo);
        rowNo++;
        findRouteContain2Value(from, to);
        for(Map.Entry<Integer, ArrayList<ScheduledRoute>> entry : searchResults.entrySet()) {
            gridPane.add(new Label(Destinations.getInstance().getStations().get(entry.getValue().get(0).getStation_from()).toString()),0,rowNo);
            gridPane.add(new Label(entry.getValue().get(0).getStartTime().toString()),1,rowNo);
            gridPane.add(new Label(Destinations.getInstance().getStations().get(entry.getValue().get(entry.getValue().size()-1).getStation_to()).toString()),2,rowNo);
            //if()
            gridPane.add(new Label(entry.getValue().get(entry.getValue().size()-1).getEndTime().toString()),3,rowNo);
            int thisBookingPrice = price.get(entry.getKey());
            gridPane.add(new Label( thisBookingPrice+ " GD"),4,rowNo);


            Button bookButton = new Button("Book");
            if(Account.getInstance().getBalance()<thisBookingPrice) {
                bookButton = new Button("Book (insufficient funds)");
                bookButton.setDisable(true);
                Button finalBookButton = bookButton;
                bookButton.setOnMouseEntered(event -> {
                    if(Account.getInstance().getBalance()>thisBookingPrice){
                        finalBookButton.setDisable(false);
                    }
                });
            }
            Button printButton = new Button("Print");
            printButton.setDisable(true);

            printButton.setOnAction(event -> print(((Label)getNodeFromGridPane(gridPane, 0, 1)).getText(),
                    ((Label)getNodeFromGridPane(gridPane, 1, 1)).getText(),
                    ((Label)getNodeFromGridPane(gridPane, 2, 1)).getText(),
                    ((Label)getNodeFromGridPane(gridPane, 3, 1)).getText(),
                    ((Label)getNodeFromGridPane(gridPane, 4, 1)).getText()));

            Button finalBookButton1 = bookButton;
            bookButton.setOnAction(event -> {
                makeBooking(
                        price.get(entry.getKey()),
                        Account.getInstance().getAccountId(),
                        entry.getValue().get(0).getStation_from(),
                        entry.getValue().get(entry.getValue().size() - 1).getStation_to(),
                        entry.getKey());
                printButton.setDisable(false);
                finalBookButton1.setDisable(true);
                finalBookButton1.setText("Booked");
            });
            gridPane.add(bookButton,5,rowNo);
            gridPane.add(printButton,6,rowNo);
            if(entry.getValue().get(0).getDelayMessage() != null){
                gridPane.add(new Label(entry.getValue().get(0).getDelay()), 7, rowNo);
            }





            rowNo++;


        }
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(22);
        return gridPane;
    }

    public void makeBooking(int amount, String accountId,int stationFrom, int stationTo, int route_Id){
            DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
            dbConnection.makeBooking(amount,accountId,stationFrom,stationTo,route_Id);
            //if done successfully
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Confirmed", ButtonType.OK);
            a.showAndWait();


    }




    public ArrayList<Vehicle> generateVehicles(){
        int minuteNow = Calendar.getInstance().get(Calendar.MINUTE);
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        for(Map.Entry<Integer, ArrayList<ScheduledRoute>> entry : items.entrySet()) {
            for (ScheduledRoute t: entry.getValue()) {
                int startTime = t.getStartTime().getMinute();
                int endTime = t.getEndTime().getMinute();
                if (endTime < startTime) {
                    endTime += 60;
                }
                if (minuteNow >= startTime) {
                    if (minuteNow < endTime) {
                        vehicles.add(new Vehicle(t));
                    }
                }else if (minuteNow< endTime- 60) {
                    vehicles.add(new Vehicle(t));
                }

            }
        }
        return vehicles;
    }

    private void print(String from, String timeFrom, String to, String timeTo, String price) {
        Booking[] bookingList = new Booking[1];
        Booking booking = new Booking(
                Account.getInstance().getAccountId(),
                from + " " + timeFrom,
                to +  " " + timeTo,
                price);
        bookingList[0] = booking;
        try {
            booking.printToPdf(bookingList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}
