package com.github.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class BreakNews {
    ArrayList<Text> news;
    int currentAds;
    boolean updateAds;



    public BreakNews(ArrayList<Text> news){
        this.news = news;
        currentAds = 0;
        updateAds = true;
    }

    public void draw(GraphicsContext gc){
        if(news.size()>0) {
            gc.setFill(Color.web("800000ff"));
            gc.setGlobalAlpha(0.6);
            gc.fillRect(0, 640 - 70, 960, 640);
            gc.setGlobalAlpha(1);
            int seconds = Calendar.getInstance().get(Calendar.SECOND);
            gc.setFill(Color.WHITESMOKE);
            if (seconds % 3 == 0 && updateAds) {
                currentAds++;
                if (currentAds == news.size()) {
                    currentAds = 0;
                }
                updateAds = false;
            } else if (seconds % 3 == 1) {
                updateAds = true;
            }
            String textToShow = news.get(currentAds).getText();
            double textWidth = news.get(currentAds).getLayoutBounds().getWidth();
            gc.fillText(textToShow, (960 - textWidth) / 2, 640 - 45);
            gc.setFill(Color.BLACK);
        }



        //Update
//        if(rectPosition.getX()>-1000){
//            textPosition.addToX(-1);
//            rectPosition.addToX(-1);
//        }else{
//            textPosition.addToY(1);
//            rectPosition.addToY(1);
//        }
    }
}
