package com.example.android.routetesting.decoders;

import android.os.AsyncTask;
import android.util.IntProperty;
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
        //Log.d("MOREWEATHER", "GotIntoMoreWeather");


        if (coord.equals(new Coord(1000, 1000))) {

            ArrayList<WeatherInfo> infos = new ArrayList<>();
            infos.add(new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0));
            return infos;
        }


        if (dates.isEmpty()) {
            return null;
        }

        Document apiResult = null;
        Log.i("BEFORE", "before");


        apiResult = ApiDocumentBuilder.decode(ApiUrl.METNO, coord.getLat(), coord.getLon());
        //Log.i("DOCUMENT", Helper.getStringFromDocument(apiResult));



//        try {
//            apiResult = new AsyncTask<Object, Void, Document>() {
//                @Override
//                protected Document doInBackground(Object... objects) {
//                    //Log.d("MOREWEATHER", "doInBackground");
//
//
//                    Document temp = ApiDocumentBuilder.decode(ApiUrl.METNO, coord.getLat(), coord.getLon());
//                    if (temp == null) {
//                        Log.i("DOCUMENT", "NULL");
//                        return null;
//                    }
//                    Log.i("DOCUMENT", Helper.getStringFromDocument(temp));
//                    return temp;
//
//                }
//            }.execute(coord, dates.get(0)).get();
//
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }


        if (apiResult == null) {
            //Log.e("MOREWEATHER", "ApiResult == null");
            return null;
        } else {
            //Log.i("MOREWEATHER", "ApiResult contains data");
            //Log.e("DATEZTOSTRING", dates.toString());
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
            Log.i("SINGLEWEATHERFROMAPI", "infos == null or empty");
            return null;
        }


        return infos.get(0);
    }


    private static WeatherInfo getWeatherFromDocument(final Coord coord, final Date date, final Document weatherDoc) {

        try {
            WeatherInfo info = new WeatherInfo();


            //Log.i("WEATHERDOC", "Got into getWeatherFromDocument");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String currentDay12;

            if (calendar.get(Calendar.HOUR_OF_DAY) > 12) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 0, 0);
            } else {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 12, 0, 0);

            }
            currentDay12 = Helper.getGivenDateInFormat(calendar);
            //Remove Minutes and seconds from time


            Log.e("CurrentDay12", currentDay12);

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
            String currentDay13 = Helper.getGivenDateInFormat(calendar);
            Log.e("CurrentDay13", currentDay13);

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
            String currentDay06 = Helper.getGivenDateInFormat(calendar);
            Log.e("CurrentDay06", currentDay06);

            NodeList timeNodes = weatherDoc.getElementsByTagName("time");

            for (int i = 0; i < timeNodes.getLength(); i++) {

                Node node = timeNodes.item(i);
                NamedNodeMap attributes = node.getAttributes();


                if (attributes.getNamedItem("from").getNodeValue().equals(currentDay12)) {
                    if (attributes.getNamedItem("to").getNodeValue().equals(currentDay12)) {
                        Log.i("DECODE", "From and To = " + currentDay12 + "; loop: " + i);
                        node = node.getChildNodes().item(1);//Skip Location node

                        info.setLocation(coord);

                        info.setTemperature(Float.parseFloat(node.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue()));

                        info.setDirection(node.getChildNodes().item(3).getAttributes().getNamedItem("name").getNodeValue());

                        info.setHumidity(Float.parseFloat(node.getChildNodes().item(7).getAttributes().getNamedItem("value").getNodeValue()));

                        info.setWindspeed(Float.parseFloat(node.getChildNodes().item(5).getAttributes().getNamedItem("mps").getNodeValue()));


                        info.setTime(date);
                        Log.i("TEMPINFO_1212", info.toString());
                    }
                    if ((!currentDay12.equals(currentDay13)) && attributes.getNamedItem("to").getNodeValue().equals(currentDay13)) {
                        Log.i("DECODE", "From = " + currentDay12 + ";To = " + currentDay13 + "; loop: " + i);
                        node = node.getChildNodes().item(1);//Skip Location node

                        String tem = node.getChildNodes().item(3).getAttributes().getNamedItem("number").getNodeValue();
                        Log.i("TEM", tem);
                        info.setSymbol(Integer.parseInt(tem));
                        Log.i("TEMPINFO_1213", info.toString());

                    }

                }

                if (attributes.getNamedItem("from").getNodeValue().equals(currentDay06)) {
                    if (attributes.getNamedItem("to").getNodeValue().equals(currentDay12)) {

                        Log.i("DECODE", "From = " + currentDay06 + ";To = " + currentDay12 + "; loop: " + i);

                        node = node.getChildNodes().item(1);//Skip Location node
                        info.setMinTemp(Float.parseFloat(node.getChildNodes().item(3).getAttributes().getNamedItem("value").getNodeValue()));
                        info.setMaxTemp(Float.parseFloat(node.getChildNodes().item(5).getAttributes().getNamedItem("value").getNodeValue()));
                        Log.i("TEMPINFO_0612", info.toString());
                    }
                }


            }

            Log.e("TEMPINFO_FINI", info.toString());
            return info;
        } catch (Exception e) {
            Log.e("WEATHERDOC_ERROR", e.getLocalizedMessage());
        }
        return null;


    }

    public static ArrayList<WeatherInfo> getWeathersOnRoute(int maxDistance, String firstAddress, String secondAddress) {


        ArrayList<Step> steps = RouteDecoder.routeStepFromApi(maxDistance, firstAddress, secondAddress);
        //Log.e("GETWEATHERSONROUTE", steps.toString());
        //ArrayList<Coord> coords = new ArrayList<>();

        ArrayList<WeatherInfo> weatherInfos = new ArrayList<>();

//        for (Step s : steps) {
//            coords.add(s.getCoord());
//        }
//        Coord.calculateConnectionCoords((int) maxDistance, coords);


        Date deltaTime = Calendar.getInstance().getTime();


//TODO: Fix time for extra coords;
        for (int i = 0; i < steps.size(); i++) {
            Step s = steps.get(i);
            //Log.e("GETWEATHERSONROUTE", "StepInfo " + s.toString());
            weatherInfos.add(WeatherDecoder.getSingleWeatherFromApi(s.getCoord(), deltaTime));
            //Log.e("GETWEATHERSONROUTE", "Weatherinfo: " + weatherInfos.get(weatherInfos.size() - 1));
            deltaTime.setTime(deltaTime.getTime() + s.getDuration() * 1000);
        }
        return weatherInfos;

    }


    public static ArrayList<WeatherInfo> getNextWeekInfo(Coord coord) {
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //Remove Minutes and seconds from time
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
        date = calendar.getTime();

        ArrayList<Date> dates = new ArrayList<>();


        for (int i = 0; i < 7; i++) {

            date.setTime(date.getTime() + 86400000); //adds a day


            //region Calculates summer/wintertime
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour == 11) {
                date.setTime(date.getTime() + 3600000); //adds an hour
            } else if (hour == 13) {
                date.setTime(date.getTime() - 3600000); //subtracts and hour
            }
            //endregion


            dates.add(new Date(date.getTime()));
            //Log.d("DatezLoop", date.toString() + "||" + dates.size());
        }

        //Log.e("NEXTWEEK", dates.toString());


        ArrayList<WeatherInfo> infos = getMoreWeatherFromApi(coord, dates);
        //Log.e("WEATHERWEATHER", infos.toString());
        return infos;

    }


}
