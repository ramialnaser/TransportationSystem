package com.github.controller;

import com.github.model.Account;
import com.github.model.DBConnection;
import com.github.model.Destinations;
import com.github.model.TimeProcess;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Driver implements Initializable{
    @FXML private Button signOutButton;
    @FXML private JFXComboBox comboLate;
    @FXML private JFXButton confirmBtn;
    @FXML private Label driverStatus;
    @FXML private JFXTextField delayMessage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ExtendedButton.setFunction(signOutButton, ExtendedButton.Type.TO_LOGIN);
        comboLate.getItems().addAll("02 min","04 min","06 min","08 min","10 min", "12 min","14 min","16 min","18 min","20 min","22 min","24 min","26 min","28 min","30 min", "32 min","34 min","36 min","38 min","40 min", "42 min","44 min","46 min","48 min","50 min", "52 min","54 min","56 min","58 min","60 min");
        comboLate.valueProperty().addListener((observableValue, oldString, newString) -> {
            if(!comboLate.getSelectionModel().isEmpty()){
                confirmBtn.setDisable(false);
            }else{
                confirmBtn.setDisable(true);
            }
        });
        setDriverSchedule();



    }
    @FXML
    private void saveDelayAndMessage() {
        DBConnection db = new DBConnection(DBConnection.ConnectionType.ADMIN);

        String delayString = "00:" + comboLate.getSelectionModel().getSelectedItem().toString().substring(0,2) + ":00";
        db.updateDelayAndMessage(delayString,delayMessage.getText());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Confirmed", ButtonType.OK);
        alert.showAndWait();
        setDriverSchedule();


    }

    private void setDriverSchedule(){
        DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        driverStatus.setText(dbConnection.getCurrentRouteForDriver());
    }




}