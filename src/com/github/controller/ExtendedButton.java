package com.github.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

import java.util.Optional;

public class ExtendedButton extends Button {
    enum Type{
        TO_LOGIN, EXIT_PLATFORM
    }
    public static void setFunction(Button button, Type type ) {
        RotateTransition rotation = new RotateTransition(Duration.seconds(0.5), button);
        rotation.setCycleCount(1);
        rotation.setByAngle(360);
        button.setOnMouseEntered(e -> rotation.play());
        switch (type){
            case TO_LOGIN:
                button.setOnAction(event -> signOutButtonPressed());
                break;
            case EXIT_PLATFORM:
                button.setOnAction(event -> Platform.exit());
                break;
        }

    }

    private static void signOutButtonPressed(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sign out!");
        alert.setHeaderText("Do you wish to sign out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get()==ButtonType.OK){
            StageManager.getInstance().getLogin();
        }
    }

}
