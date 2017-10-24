package com.example.android.routetesting.models;

/**
 * Created by Effectlife on 1/10/2017.
 */
public class Step {
    private int Distance;
    private Coord coord;
    private int duration;



    private boolean requestWeather =false;



    public Step(int Distance, float lat, float lon, int duration) {
        this.Distance = Distance;
        this.coord = new Coord(lat, lon);
        this.duration = duration;

    }

    public int getDistance() {
        return Distance;
    }

    public void setDistance(int distance) {
        this.Distance = distance;
    }

    public float getLat() {
        return coord.getLat();
    }

    public void setLat(float lat) {
        this.coord.setLat(lat);
    }

    public float getLon() {
        return coord.getLon();
    }

    public void setLon(float lon) {
        this.coord.setLon(lon);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public boolean isRequestWeather() {
        return requestWeather;
    }

    public void setRequestWeather(boolean requestWeather) {
        this.requestWeather = requestWeather;
    }

    @Override
    public String toString() {
        return "Distance: " + Distance + "m; Coords: "+coord+"; Duration: "+duration+"sec";
    }

    public int getMinutes(){
        return duration%60;
    }

    public int getHours(){
        return duration%3600;
    }


}
