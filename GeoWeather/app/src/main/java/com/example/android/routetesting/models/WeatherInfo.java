package com.example.android.routetesting.models;

import com.example.android.routetesting.lookups.Weekday;

import java.util.ArrayList;
import java.util.Calendar;
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
    private int symbol;

    public WeatherInfo() {
    }

    public WeatherInfo(Coord location, float temperature, float humidity, float windspeed, String direction, Date time, int symbol) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.direction = direction;
        this.time = time;
        this.symbol = symbol;
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

    public static ArrayList<CustomListItem> convertListWeatherToListCLI(ArrayList<WeatherInfo> infos, boolean cityname) {




        ArrayList<CustomListItem> items = new ArrayList<>();
        for (WeatherInfo info : infos) {
            String title="ERRORED";
            if (cityname) {
                title = info.getLocation().getCityName();

            } else {
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(info.getTime());
                    title = Weekday.values()[c.get(Calendar.DAY_OF_WEEK) - 1].getValue();
                    //Log.d("SUCCESS", "Success "+cityTextView.getText());
                } catch (Exception e) {
                    //Log.e("FAILED","Failed "+position);
                    e.printStackTrace();
                }
            }



            items.add(new CustomListItem(title, info.getTemperature() + "",info.getTime()) );


        }
        return items;
    }

    public CustomListItem convertToCustomListItem(boolean city) {
        if(city)
        return new CustomListItem(getLocation().getCityName(), getTemperature() + "", getTime());
        else{
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(getTime());
                return new CustomListItem(Weekday.values()[c.get(Calendar.DAY_OF_WEEK) - 1].getValue(),getTemperature()+"", getTime());
                //Log.d("SUCCESS", "Success "+cityTextView.getText());
            } catch (Exception e) {
                //Log.e("FAILED","Failed "+position);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "location: " + location + "; temp: " + temperature + "; humid: " + humidity + "; windsp: " + windspeed + "; direction:" + direction;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }
}
