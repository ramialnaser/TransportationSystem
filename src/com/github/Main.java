package com.github;

import javafx.application.Application;
import javafx.stage.Stage;
import com.github.controller.StageManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
    StageManager.getInstance().getDeveloperMenu();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
