package com.github.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Crownlands {
    int currentMinute;
    private List<Integer> vehicleType;
    private List<Integer> minutes;
    private List<Boolean> isFrom;
    private List<Train> vehicles;


    public Crownlands() {
        currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        vehicleType = Arrays.asList(7,2,12,14,9,13,2,6,1,9,11,3,13,6,3,1,1,8,4,11,1,10,4,10,12,14,5,7,8,5);
        minutes = Arrays.asList(1,5,5,5,10,10,12,15,16,19,20,20,25,26,27,27,29,30,35,35,39,40,42,49,50,50,50,50,54,58);
        isFrom = Arrays.asList(false,true,false,false,true,true,false,true,false,false,true,true,false,false,false,false,true,true,true,false,true,true,false,false,true,true,true,true,false,false);
        vehicles = new ArrayList<>();
        for (int i = 0; i < vehicleType.size(); i++) {
            if (vehicleType.get(i) ==1) {
                if(isFrom.get(i)){
                    vehicles.add(new Train(878,0,-1, 0.5f, "resources/img/WagonDOWN.png"));
                }else{
                    vehicles.add(new Train(0,447,1, -0.5f, "resources/img/WagonUP.png" ));
                }
            }else if(vehicleType.get(i)< 9) {
                if(isFrom.get(i)){
                    vehicles.add(new Train(790,600,-1f, -0.5f, "resources/img/cityBusIn.png"));
                }else{
                    vehicles.add(new Train(-50,200,1, 0.5f, "resources/img/cityBusOut.png" ));
                }
            }else{
                if(isFrom.get(i)){
                    vehicles.add(new Train(890,650,-1f, -0.5f, "resources/img/regionBusIn.png"));
                }else{
                    vehicles.add(new Train(-150,150,1, 0.5f, "resources/img/regionBusOut.png" ));
                }
            }
        }
    }

    public void draw(GraphicsContext gc){
        for (int i = 0; i < vehicles.size() ; i++) {
            if(minutes.get(i) == currentMinute){
                vehicles.get(i).draw(gc);
            }
        }
        if(Calendar.getInstance().get(Calendar.MINUTE) != currentMinute){
            currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
            vehicles = new ArrayList<>();
            for (int i = 0; i < vehicleType.size(); i++) {
                if (vehicleType.get(i) ==1) {
                    if(isFrom.get(i)){
                        vehicles.add(new Train(878,0,-0.5f, 0.25f, "resources/img/WagonDOWN.png"));
                    }else{
                        vehicles.add(new Train(0,447,0.5f, -0.25f, "resources/img/WagonUP.png" ));
                    }
                }else if(vehicleType.get(i)< 9) {
                    if(isFrom.get(i)){
                        vehicles.add(new Train(790,600,-0.5f, -0.25f, "resources/img/cityBusIn.png"));
                    }else{
                        vehicles.add(new Train(-50,200,0.5f, 0.25f, "resources/img/cityBusOut.png" ));
                    }
                }else{
                    if(isFrom.get(i)){
                        vehicles.add(new Train(890,650,-0.5f, -0.25f, "resources/img/regionBusIn.png"));
                    }else{
                        vehicles.add(new Train(-150,150,0.5f, 0.25f, "resources/img/regionBusOut.png" ));
                    }
                }
            }
        }
    }
}
