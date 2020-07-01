package com.github.model;

public class Vector2D {
    private double x;
    private double y;


    public Vector2D(float x, float y) {
        this.x = (double)x;
        this.y = (double) y;
    }
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Vector2D(Vector2D vector2D) {
        this.x = vector2D.getX();
        this.y = vector2D.getY();
    }




    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public void addToX(double x){
        this.x += x;
    }
    public void addToY(double y){
        this.y += y;
    }

    public void add(Vector2D point){
        x += point.getX();
        y += point.getY();
    }
    public void add(double x, double y){
        this.x += x;
        this.y += y;
    }

    public boolean inRange(Vector2D point){
        return distance(point)<2;
    }

    //hypotenuse
    public double distance(Vector2D point){
        return Math.pow(Math.pow(point.getY()-y,2) + Math.pow(point.getX()-x,2),0.5);
    }


    public double angle(Vector2D point){
        return Math.asin((point.getX()-x)/distance(point));
    }


    @Override
    public String toString() {
        return "x:" + x +", y:" + y;
    }
}
