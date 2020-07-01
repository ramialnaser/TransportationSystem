package com.github.controller;

import com.github.model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

public class Simulation {
    @FXML private Button signOutButton;
    @FXML private Canvas canvas_Station_1;
    @FXML private Canvas canvas_City_1;
    @FXML private Canvas canvas_Region_1;

    private GraphicsContext gc_Station_1;
    private GraphicsContext gc_City_1;
    private GraphicsContext gc_Region_1;


    AnimationTimer timer;
    private VehicleSimulation vehicleSimulation;
    private Crownlands crownlands;


    /*private Train train_north;
    private Train train_south;*/
    private Clock clock;
    private Vector2D mouse;

    public void initialize() {
        ExtendedButton.setFunction(signOutButton, ExtendedButton.Type.EXIT_PLATFORM);

        gc_Station_1 = canvas_Station_1.getGraphicsContext2D();
        gc_City_1 = canvas_City_1.getGraphicsContext2D();
        gc_Region_1 = canvas_Region_1.getGraphicsContext2D();







        vehicleSimulation = new VehicleSimulation();
        crownlands = new Crownlands();


        //train_north = new Train(878,0,-1, 0.5f, "resources/img/WagonDOWN.png" );
        //train_south = new Train(0,447,1, -0.5f, "resources/img/WagonUP.png" );
        clock = new Clock();
        mouse = new Vector2D(0,0);



        /*canvas_City_1.setOnMouseMoved(mouseEvent -> mouse.set( mouseEvent.getX(), mouseEvent.getY()));
        canvas_Region_1.setOnMouseMoved(mouseEvent -> mouse.set( mouseEvent.getX(), mouseEvent.getY()));
        canvas_Station_1.setOnMouseMoved(mouseEvent -> mouse.set( mouseEvent.getX(), mouseEvent.getY()));*/

        //render
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc_Station_1.clearRect(0,0, canvas_City_1.getWidth(), canvas_City_1.getHeight());
                gc_City_1.clearRect(0,0, canvas_City_1.getWidth(), canvas_City_1.getHeight());
                gc_Region_1.clearRect(0,0, canvas_City_1.getWidth(), canvas_City_1.getHeight());
                vehicleSimulation.draw(gc_Region_1, gc_City_1);


                /*gc_Region_1.fillText(mouse.toString(),100,100);
                gc_City_1.fillText(mouse.toString(),100,100);
                gc_Station_1.fillText(mouse.toString(),100,100);*/


                //train_north.draw(gc_Station_1);
                //train_south.draw(gc_Station_1);
                crownlands.draw(gc_Station_1);
                gc_Region_1.setLineWidth(0.2);
//                for (int x = 0; x < 1120 ; x+=20) {
//                    //gc_Station_1.strokeLine(x , 0 ,x, 960);
//                    //gc_City_1.strokeLine(x , 0 ,x, 960);
//                    gc_Region_1.strokeLine(x , 0 ,x, 960);
//                }
//                for (int y = 0; y < 960 ; y+=20) {
//                    //gc_Station_1.strokeLine(0 , y ,1120, y);
//                    //gc_City_1.strokeLine(0 , y ,1120, y);
//                    gc_Region_1.strokeLine(0 , y ,1120, y);
//                }
                clock.timeSet(gc_Station_1);

                vehicleSimulation.getBreakNews().draw(gc_Station_1);
                vehicleSimulation.getBreakNews().draw(gc_Region_1);
                vehicleSimulation.getBreakNews().draw(gc_City_1);

            }

        };
        timer.start();

    }
}
