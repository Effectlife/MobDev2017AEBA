package com.example.android.routetesting.models;

import java.util.Date;

/**
 * Created by AaronEnglerPXL on 17/10/2017.
 */

public class CustomListItem {
    private String city, temperature;
    private Date date;

    public CustomListItem(String city, String temperature, Date date) {
        this.city = city;
        this.temperature = temperature;
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
