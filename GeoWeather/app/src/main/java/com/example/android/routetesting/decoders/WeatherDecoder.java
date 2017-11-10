package com.example.android.routetesting.decoders;


import android.util.Log;

import com.example.android.routetesting.lookups.ApiUrl;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.Step;
import com.example.android.routetesting.models.WeatherInfo;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by AaronEnglerPXL on 10/10/2017.
 */

public abstract class WeatherDecoder {

    //TODO: Prevent multiple calls to the api for multiple dates if the dates are in the same result
    public static ArrayList<WeatherInfo> getMoreWeatherFromApi(final Coord coord, final ArrayList<Date> dates) {
        if (coord.equals(new Coord(1000, 1000))) {

            ArrayList<WeatherInfo> infos = new ArrayList<>();
            infos.add(new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0));
            return infos;
        }

        if (dates.isEmpty()) {
            return null;
        }

        Document apiResult = null;


        apiResult = ApiDocumentBuilder.decode(ApiUrl.METNO, coord.getLat(), coord.getLon());

        if (apiResult == null) {
            return null;
        }

        ArrayList<WeatherInfo> infos = new ArrayList<>();

        for (Date date : dates) {
            infos.add(getWeatherFromDocument(coord, date, apiResult));
        }
        return infos;
    }

    public static WeatherInfo getSingleWeatherFromApi(final Coord coord, final Date date) {

        ArrayList<Date> dates = new ArrayList<>();
        dates.add(date);

        ArrayList<WeatherInfo> infos = getMoreWeatherFromApi(coord, dates);

        if (infos == null || infos.isEmpty()) {
            Log.e("getSingleWeather", "infos == null or empty"); //keep, is kind of error
            return null;
        }

        return infos.get(0);
    }

    private static WeatherInfo getWeatherFromDocument(final Coord coord, final Date date, final Document weatherDoc) {

        try {
            WeatherInfo info = new WeatherInfo();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String currentDay12;

            if (calendar.get(Calendar.HOUR_OF_DAY) > 12) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 0, 0);
            } else {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 12, 0, 0);

            }
            currentDay12 = Helper.getGivenDateInFormat(calendar);

            //Log.e("CurrentDay12", currentDay12);

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
            String currentDay13 = Helper.getGivenDateInFormat(calendar);
            //Log.e("CurrentDay13", currentDay13);

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
            String currentDay06 = Helper.getGivenDateInFormat(calendar);
            //Log.e("CurrentDay06", currentDay06);

            NodeList timeNodes = weatherDoc.getElementsByTagName("time");

            for (int i = 0; i < timeNodes.getLength(); i++) {

                Node node = timeNodes.item(i);
                NamedNodeMap attributes = node.getAttributes();


                if (attributes.getNamedItem("from").getNodeValue().equals(currentDay12)) {
                    if (attributes.getNamedItem("to").getNodeValue().equals(currentDay12)) {
                        //Log.i("DECODE", "From and To = " + currentDay12 + "; loop: " + i);
                        node = node.getChildNodes().item(1);//Skip Location node

                        info.setLocation(coord);
                        info.setTemperature(Float.parseFloat(node.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue()));
                        info.setDirection(node.getChildNodes().item(3).getAttributes().getNamedItem("name").getNodeValue());
                        info.setHumidity((Float.parseFloat(node.getChildNodes().item(7).getAttributes().getNamedItem("value").getNodeValue())));
                        info.setWindspeed(Float.parseFloat(node.getChildNodes().item(5).getAttributes().getNamedItem("mps").getNodeValue()));
                        info.setTime(date);
                    }
                    if ((!currentDay12.equals(currentDay13)) && attributes.getNamedItem("to").getNodeValue().equals(currentDay13)) {
                        node = node.getChildNodes().item(1);//Skip Location node

                        String tem = node.getChildNodes().item(3).getAttributes().getNamedItem("number").getNodeValue();
                        info.setSymbol(Integer.parseInt(tem));
                    }
                }

                if (attributes.getNamedItem("from").getNodeValue().equals(currentDay06)) {
                    if (attributes.getNamedItem("to").getNodeValue().equals(currentDay12)) {
                        node = node.getChildNodes().item(1);//Skip Location node
                        info.setMinTemp(Float.parseFloat(node.getChildNodes().item(3).getAttributes().getNamedItem("value").getNodeValue()));
                        info.setMaxTemp(Float.parseFloat(node.getChildNodes().item(5).getAttributes().getNamedItem("value").getNodeValue()));
                    }
                }
            }
            return info;
        } catch (Exception e) {
            Log.e("getWeatherFromDocument", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;


    }

    public static ArrayList<WeatherInfo> getWeathersOnRoute(int maxDistance, String firstAddress, String secondAddress) {

        ArrayList<Step> steps = RouteDecoder.routeStepFromApi(maxDistance, firstAddress, secondAddress);
        ArrayList<WeatherInfo> weatherInfos = new ArrayList<>();

        Date deltaTime = Calendar.getInstance().getTime();

        for (int i = 0; i < steps.size(); i++) {
            Step s = steps.get(i);

            weatherInfos.add(WeatherDecoder.getSingleWeatherFromApi(s.getCoord(), deltaTime));
            deltaTime.setTime(deltaTime.getTime() + s.getDuration() * 1000);
        }
        return weatherInfos;
    }

    public static ArrayList<WeatherInfo> getNextWeekInfo(Coord coord) {
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
        date = calendar.getTime();

        ArrayList<Date> dates = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            date.setTime(date.getTime() + 86400000); //adds a day

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour == 11) {
                date.setTime(date.getTime() + 3600000); //adds an hour
            } else if (hour == 13) {
                date.setTime(date.getTime() - 3600000); //subtracts and hour
            }

            dates.add(new Date(date.getTime()));
        }
        ArrayList<WeatherInfo> infos = getMoreWeatherFromApi(coord, dates);

        return infos;
    }
}
