package com.example.android.routetesting.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AaronEnglerPXL on 10/10/2017.
 */

public class WeatherInfo {
    private Coord location;
    private float temperature;
    private float humidity;
    private float windspeed;
    private String direction;
    private Date time;

    public WeatherInfo() {
    }

    public WeatherInfo(Coord location, float temperature, float humidity, float windspeed, String direction, Date time) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.direction = direction;
        this.time = time;
    }

    public Coord getLocation() {
        return location;
    }

    public void setLocation(Coord location) {
        this.location = location;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(float windspeed) {
        this.windspeed = windspeed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public static ArrayList<CustomListItem> convertListWeatherToListCLI(ArrayList<WeatherInfo> infos) {
        ArrayList<CustomListItem> items = new ArrayList<>();
        for (WeatherInfo info : infos) {
            items.add(new CustomListItem(info.getLocation().getCityName(), info.getTemperature() + "",info.getTime()) );


        }
        return items;
    }

    public CustomListItem convertToCustomListItem() {
        return new CustomListItem(getLocation().getCityName(), getTemperature() + "", getTime());
    }

    @Override
    public String toString() {
        return "location: " + location + "; temp: " + temperature + "; humid: " + humidity + "; windsp: " + windspeed + "; direction:" + direction;
    }
}
