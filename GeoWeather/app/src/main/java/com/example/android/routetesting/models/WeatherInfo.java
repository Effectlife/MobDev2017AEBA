package com.example.android.routetesting.models;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.routetesting.MainActivity;
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

    private float minTemp, maxTemp;


    private float humidity;
    private float windspeed;
    private String direction;
    private Date time;
    private int symbol;

    public WeatherInfo() {
    }

    public WeatherInfo(Coord location, float temperature, float minTemp, float maxTemp, float humidity, float windspeed, String direction, Date time, int symbol) {
        this.location = location;
        this.temperature = temperature;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
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

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
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

    public String getDay(){
        Calendar c = Calendar.getInstance();
        c.setTime(this.getTime());
        return Weekday.values()[c.get(Calendar.DAY_OF_WEEK) - 1].getValue() +" ";
    }

    public static ArrayList<CustomListItem> convertListWeatherToListCLI(ArrayList<WeatherInfo> infos, boolean cityname, boolean humidity) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        boolean fahrenheit = sharedPrefs.getBoolean("pref_fahrenheit", false);


        ArrayList<CustomListItem> items = new ArrayList<>();
        for (WeatherInfo info : infos) {
            String title = "ERRORED";
            if (cityname) {
                title = info.getLocation().getCityName(false);

            } else {
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(info.getTime());
                    title = Weekday.values()[c.get(Calendar.DAY_OF_WEEK) - 1].getValue()+" ";
                    //Log.d("SUCCESS", "Success "+cityTextView.getText());
                } catch (Exception e) {
                    //Log.e("FAILED","Failed "+position);
                    e.printStackTrace();
                }
            }

    if(humidity){

    items.add(new CustomListItem(title+" ", info.getHumidity()+"%", info.getTime()));

    }else {
    String temperature;
    if (fahrenheit) {
        temperature = Helper.celsiusToFahrenheit(info.getTemperature()) + " °F";
    } else {
        temperature = info.getTemperature() + " °C";
    }
    items.add(new CustomListItem(title+" ", temperature, info.getTime()));
    }

        }
        return items;
    }

    public CustomListItem convertToCustomListItem(boolean city) {


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        boolean fahrenheit = sharedPrefs.getBoolean("pref_fahrenheit", false);
        String temperature;
        if (fahrenheit) {
            temperature = Helper.celsiusToFahrenheit(getTemperature()) + " °F";
        } else {
            temperature = getTemperature() + " °C";
        }
        if (city)
            return new CustomListItem(getLocation().getCityName(false), temperature, getTime());
        else {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(getTime());
                return new CustomListItem(Weekday.values()[c.get(Calendar.DAY_OF_WEEK) - 1].getValue(), getTemperature() + "", getTime());
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
        return "location: " + location
                + "; temp: " + temperature
                + "; minTemp: " + minTemp
                + "; maxTemp: " + maxTemp
                + "; humid: " + humidity
                + "; windsp: " + windspeed
                + "; direction:" + direction
                + "; symbol:" + symbol;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }
}
