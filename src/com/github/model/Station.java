package com.github.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Station{
    String name;
    String city;
    Vector2D position;


    public Station(ResultSet rs) {
        try {
            name = rs.getString(2);
            city = rs.getString(3);;
            position = new Vector2D(rs.getFloat(4),rs.getFloat(5));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public Vector2D getPosition() {
        return position;
    }


    @Override
    public String toString() {
        return name;
    }

}
