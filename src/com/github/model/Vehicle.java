package com.github.model;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.github.model.Enumeration.*;

import java.util.Calendar;

public class Vehicle {
    private VehicleType type;
    private TwoPointsMoving move;
    private Image image;


    public Vehicle(ScheduledRoute t) {
        move = new TwoPointsMoving(t);
        int routeID = t.getRouteID();
        if (routeID ==1) {
            image = new Image("resources/img/TrainImage.png");
            type = VehicleType.TRAIN;
        }else if(routeID< 9) {
            image = new Image("resources/img/cityBus.png");
            type = VehicleType.CITY_BUS;
        }else{
            image = new Image("resources/img/regionBus.png");
            type= VehicleType.REGION_BUS;
        }
    }

    public Enumeration.VehicleType getType() {
        return type;
    }

    public void setType(Enumeration.VehicleType type) {
        this.type = type;
    }



    public TwoPointsMoving getMove() {
        return move;
    }


    public void draw(GraphicsContext gc){
        gc.drawImage(image, move.getPosition().getX(), move.getPosition().getY(),15,15);
        move.calculatePosition();
    }

}
