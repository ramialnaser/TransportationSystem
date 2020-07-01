package com.github.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;


public class VehicleSimulation {
    private ArrayList<Vehicle> vehicles;
    private BreakNews breakNews;

    int timeToUpdate;

    public VehicleSimulation() {
        callScheduledRoute();
    }

    public void callScheduledRoute(){
        vehicles = Destinations.getInstance().getScheduledRoutes().generateVehicles();
        timeToUpdate =Calendar.getInstance().get(Calendar.MINUTE);
        breakNews = new BreakNews(getNews());
    }

    public BreakNews getBreakNews() {
        return breakNews;
    }

    public void draw(GraphicsContext gc_Region, GraphicsContext gc_City){
        for (Vehicle v: vehicles){
            switch (v.getType()){
                case TRAIN:
                case REGION_BUS:
                    v.draw(gc_Region);
                    break;
                case CITY_BUS:
                    v.draw(gc_City);
                    break;
            }

        }

        if(Calendar.getInstance().get(Calendar.MINUTE)!=timeToUpdate){//Update every one minute
            callScheduledRoute();
        }
    }
    public ArrayList<Text> getNews(){
        ArrayList<Text> news = new ArrayList<>();
        news.add(new Text("Please, note that this simulation is a beta version"));
        news.add(new Text("And it's not considered as a trusted source"));
        news.add(new Text("Call customer's service or application instead"));

        for (Vehicle v: vehicles) {
            news.add(new Text(v.getMove().toString()));
        }

        return news;
    }

}
