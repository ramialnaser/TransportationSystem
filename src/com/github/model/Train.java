package com.github.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Train {
    private Image image;
    private Vector2D startPosition;


    private Vector2D position;
    private Vector2D variable;
    private int stopTimer;
    private boolean isStop;



    public Train(float x, float y, float xDirection, float yDirection, String imagePath) {
        image = new Image(imagePath);
        if(y==0){
            y -= image.getHeight();
        }
        if(x==0){
            x -= image.getWidth();
        }
        startPosition = new Vector2D(x,y);
        position = new Vector2D(x,y);
        variable = new Vector2D(xDirection,yDirection);
        stopTimer = 0;
        isStop = false;
    }

    private void update(){
        if(!isStop){
            position.add(variable);
            //Stop for new Passengers
            if(position.getX() <410 && position.getX()> 408.8f){
                isStop = true;
                stopTimer = 500;
            }




        }else{
            stopTimer--;
            if(stopTimer == 0){
                isStop = false;
            }
        }


    }
    public void draw(GraphicsContext gc){
        gc.drawImage(image,position.getX(),position.getY());
        update();
    }
}
