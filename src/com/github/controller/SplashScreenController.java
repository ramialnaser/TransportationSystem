package com.github.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable{
    @FXML private AnchorPane rootPane;
    @FXML private MediaView mediaView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        MediaPlayer player = new MediaPlayer( new Media(getClass().getResource("..\\..\\..\\resources\\img\\Presentation1.mp4").toExternalForm()));
        MediaPlayer player = new MediaPlayer( new Media(getClass().getResource("../../../resources/img/Presentation1.mp4").toExternalForm()));
        mediaView.setMediaPlayer(player);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(15000));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0.6);
        fadeTransition.setNode(rootPane);
        fadeTransition.setOnFinished(event -> handleAfterFade());
        fadeTransition.play();
        player.play();
    }
    private void handleAfterFade(){
        StageManager.getInstance().getLogin();
        //StageManager.getInstance().getSplashScreen().hide();
    }





}
