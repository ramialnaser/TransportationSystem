package com.github.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Calendar;

public class Clock {

    float x;
    float y;

    public Clock() {
        x = 860;
        y = 150;
    }

    public void timeSet(GraphicsContext gc){
        Calendar calendar = Calendar.getInstance();
        double second = Math.toRadians(calendar.get(Calendar.SECOND)*6);
        double minute = Math.toRadians(calendar.get(Calendar.MINUTE)*6) + second/60;
        double hour = Math.toRadians(calendar.get(Calendar.HOUR)*30) + minute/12;
        int size = 120;
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.35);
        gc.fillOval(5+x-(size/2),5+y-(size/2),size,size);
        gc.setGlobalAlpha(1);
        gc.setFill(Color.WHITE);
        gc.fillOval(x-(size/2),y-(size/2),size,size);
        gc.setLineWidth(0.8);
        gc.strokeLine(x,y, x + size*45/100 * Math.sin(second), y - size*45/100 * Math.cos(second));
        gc.setLineWidth(1.6);
        gc.strokeLine(x,y, x + size*40/100 *Math.sin(minute),y - size*40/100 *Math.cos(minute));
        gc.setLineWidth(2.8);
        gc.strokeLine(x,y, x + size*30/100 *Math.sin(hour),y - size*30/100 *Math.cos(hour));


    }

}
