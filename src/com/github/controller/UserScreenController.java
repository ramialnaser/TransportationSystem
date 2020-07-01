package com.github.controller;

import com.github.model.Account;
import com.github.model.DBConnection;
import com.github.model.Destinations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserScreenController implements Initializable {
    @FXML private Button signOutButton;
    @FXML JFXButton printBookingHistory;
    @FXML JFXComboBox fromCombo;
    @FXML JFXComboBox toCombo;
    @FXML Label cost;
    @FXML TextField deposit;
    @FXML JFXButton processBtn;
    @FXML ScrollPane searchResults;
    @FXML
    JFXButton searchButton;
    @FXML Label balance;
    @FXML ScrollPane resultsContainer;
    @FXML Tab balanceTab;
    @FXML
    JFXTextArea complainArea;
    @FXML
    JFXComboBox comboTaxi;
    @FXML JFXButton orderTaxi;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExtendedButton.setFunction(signOutButton, ExtendedButton.Type.TO_LOGIN);
        fromCombo.getItems().addAll(Destinations.getInstance().getStationsName());
        comboTaxi.getItems().addAll(Destinations.getInstance().getRegionName());
        balance.setText(Account.getInstance().getBalance() + " GD");

        fromCombo.valueProperty().addListener((observableValue, oldString, newString) -> {
            toCombo.getItems().removeAll(toCombo.getItems());
            DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
            toCombo.getItems().addAll(dbConnection.getAvailableDestination(Destinations.getInstance().getStationID(newString.toString())));
            searchButton.setDisable(true);
        });

        toCombo.valueProperty().addListener((observableValue, oldString, newString) -> {
            if(fromCombo.getSelectionModel().isEmpty() && toCombo.getSelectionModel().isEmpty()){
                searchButton.setDisable(true);
            }else{
                searchButton.setDisable(false);
            }
        });
        comboTaxi.valueProperty().addListener((observableValue, oldString, newString) -> {
            if(!comboTaxi.getSelectionModel().isEmpty()){
                orderTaxi.setDisable(false);
            }else{
                orderTaxi.setDisable(true);
            }
        });

        balanceTab.setOnSelectionChanged(event -> setBalance());

        deposit.textProperty().addListener((observableValue, oldString, newString)->
        {
            handleDepositAmount(oldString, newString);
        });




    }

    @FXML private void setOrderTaxi(){
        //DBConnection
    }

    @FXML private void processButton(){
        Account.getInstance().addToBalance(Integer.valueOf(deposit.getText()));
        balance.setText(Account.getInstance().getBalance() + " GD");

    }

    @FXML private void transactionHistory(){
        DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        resultsContainer.setContent(dbConnection.getTransaction());
    }
    @FXML private void bookingHistory(){
        DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        resultsContainer.setContent(dbConnection.getBookingHistory());
        printBookingHistory.setDisable(false);
    }

    @FXML
    private void setSearchResult(){
        try {
            int fromSelect = Destinations.getInstance().getStationID(fromCombo.getSelectionModel().getSelectedItem().toString());
            int toSelect = Destinations.getInstance().getStationID(toCombo.getSelectionModel().getSelectedItem().toString());
            searchResults.setContent(Destinations.getInstance().getScheduledRoutes().getText(fromSelect,toSelect));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    private void handleDepositAmount(String oldString , String newString ){
        int length = newString.length();
        int number = 0;
        if(length>0 && length<5){
            try{
                number = Integer.parseInt(newString);
                deposit.setText(String.valueOf(number));
            } catch (NumberFormatException ex){
                deposit.setText(oldString);
            }
        }else {
            deposit.setText(oldString);
            //Alert
        }
        if(number>0 && number<=999999){
            processBtn.setDisable(false);
        }

    }
    @FXML public void makeComplain(){
        DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        dbConnection.makeComplain(complainArea.getText());
        complainArea.setText("");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Confirmed", ButtonType.OK);
        alert.showAndWait();
    }

    public void setBalance() {
        balance.setText(Account.getInstance().getBalance() + " GD");
    }

    @FXML
    private void handlePrintBookingHistoryButtonPressed() {
        GridPane gp = (GridPane)resultsContainer.getContent();
        int count = gp.getRowCount() - 1;
//        ((Label)getNodeFromGridPane(gp, 0, 1)).getText();

        Booking[] bookingList = new Booking[count];
        for (int i = 0; i < count; i++) {
            System.out.println(i);
            Booking booking = new Booking(Account.getInstance().getAccountId(),
                    ((Label)getNodeFromGridPane(gp, 0, i + 1)).getText(),
                    ((Label)getNodeFromGridPane(gp, 1, i + 1)).getText(),
                    ((Label)getNodeFromGridPane(gp, 3, i + 1)).getText(),
                    ((Label)getNodeFromGridPane(gp, 4, i + 1)).getText());
            bookingList[i] = booking;
        }
        try {
            new Booking().printToPdf(bookingList);
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

    @FXML
    private void orderTaxi(){
        DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        dbConnection.checkAvailableTaxi(Destinations.getInstance().getStationID(comboTaxi.getSelectionModel().getSelectedItem().toString()));
    }
}
