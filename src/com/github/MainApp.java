package com.github;

import com.github.controller.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        StageManager.getInstance().getSplashScreen();
    }
}
