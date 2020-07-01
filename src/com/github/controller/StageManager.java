package com.github.controller;

import com.github.model.SMS_Manager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class StageManager {
    private static StageManager stageManager = new StageManager();

    private Stage login;
    private Stage developerMenu;
    private Stage simulation;
    private Stage splashScreen;
    private Stage adminScreen;
    private Stage taxiScreen;
    private Stage driverScreen;
    private Stage userScreen;

    private ArrayList<Stage> stages;



    public static StageManager getInstance() {
        return stageManager;
    }

    private StageManager() {
        stages = new ArrayList<>();
    }

    public void hideAllOpen(){
        for (Stage s: stages) {
                s.hide();
        }
    }

    public void getLogin() {
        if(login == null){
            login = createStage("login.fxml");
            stages.add(login);
        }
        hideAllOpen();
        login.show();
    }

    public void getDeveloperMenu() {
        if(developerMenu == null){
            developerMenu = createStage("developerMenu.fxml");
            stages.add(developerMenu);
        }
        hideAllOpen();
        developerMenu.show();
    }

    public void getSimulation() {
        if(simulation == null){
            simulation = createStage("simulation.fxml");
            stages.add(simulation);
        }
        hideAllOpen();
        simulation.show();
    }

    public void getSplashScreen() {
        if(splashScreen == null){
            splashScreen = createStage("splashScreen.fxml");
            stages.add(splashScreen);
        }
        hideAllOpen();
        splashScreen.show();
    }

    public void getAdminScreen() {
        if(adminScreen == null){
            adminScreen = createStage("adminScreen.fxml");
            stages.add(adminScreen);
        }
        hideAllOpen();
        adminScreen.show();
    }

    public void getTaxiScreen() {
        if(taxiScreen == null){
            taxiScreen = createStage("taxiDriver.fxml");
            stages.add(taxiScreen);
        }
        hideAllOpen();
        taxiScreen.show();
    }

    public void getDriverScreen() {
        if(driverScreen == null){
            driverScreen = createStage("driver.fxml");
            stages.add(driverScreen);
        }
        hideAllOpen();
        driverScreen.show();
    }

    public void getUserScreen() {
        if(userScreen == null){
            userScreen = createStage("userScreen.fxml");
            stages.add(userScreen);
        }
        hideAllOpen();
        userScreen.show();
    }

    public Stage createStage(String stageName) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/github/view/" + stageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.TRANSPARENT);
        return stage;
    }

}
