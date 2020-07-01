package com.github.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Destinations {
    private static Destinations ourInstance = new Destinations();
    private HashMap<Integer, Station> stations;

    private ScheduleOrganizer scheduledRoutes;

    public static Destinations getInstance() {
        return ourInstance;
    }

    public ScheduleOrganizer getScheduledRoutes() {
        return scheduledRoutes;
    }

    private Destinations() {
        DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        stations = dbConnection.getStations();
        dbConnection = new DBConnection(DBConnection.ConnectionType.ADMIN);
        scheduledRoutes = dbConnection.getRoutesFFF();

    }

    public HashMap<Integer, Station> getStations() {
        return stations;
    }

    public Collection<Station> getStationsName(){
        return stations.values();
    }
    public Collection<String> getRegionName(){
        Collection<String> regionsName = new ArrayDeque<>();
        for (Station s: stations.values()
             ) {
            if(!regionsName.contains(s.getCity())){
                regionsName.add(s.getCity());
            }
        }
        return regionsName;
    }
    public int getStationID(String stationName){
        for (Map.Entry<Integer, Station> entry : stations.entrySet()) {
            if (entry.getValue().getName().equals(stationName)) {
                return entry.getKey();
            }
        }
        return -1;
    }



}
