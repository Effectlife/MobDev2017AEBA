package com.example.android.routetesting;

import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;


/**
 * Created by Effectlife on 5/11/2017.
 */

public abstract class Session {

    public static ArrayList<WeatherInfo> weekInfo;
    public static WeatherInfo currentSelectedInfo;
    public static ArrayList<WeatherInfo> routeInfo;
    public static String selectedDay;
    public static int detailScreen;

}
