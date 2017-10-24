package com.example.android.routetesting.decoders;

import android.os.AsyncTask;
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

import static android.content.ContentValues.TAG;

/**
 * Created by AaronEnglerPXL on 10/10/2017.
 */

public abstract class WeatherDecoder {

    //TODO: Prevent multiple calls to the api for multiple dates if the dates are in the same result
    public static ArrayList<WeatherInfo> getMoreWeatherFromApi(final Coord coord, final ArrayList<Date> dates) {
        Log.d("MOREWEATHER", "GotIntoMoreWeather");

        if (dates.isEmpty()) {
            return null;
        }

        Document apiResult = null;

        try {
            apiResult = new AsyncTask<Object, Void, Document>() {
                @Override
                protected Document doInBackground(Object... objects) {
                    Log.d("MOREWEATHER", "doInBackground");
                    Document temp = ApiDocumentBuilder.decode(ApiUrl.METNO, coord.getLat(), coord.getLon());
//                    Log.i("ApiDocumentBuilder: ", "starting decode from api: "+temp.getChildNodes().item(3).getChildNodes().getLength());
                    return temp;

                }
            }.execute(coord, dates.get(0)).get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        if (apiResult == null) {
            Log.e("MOREWEATHER", "ApiResult == null");
            return null;
        } else {
            Log.i("MOREWEATHER", "ApiResult contains data");
            Log.e("DATEZTOSTRING", dates.toString());

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
        try {
            if (infos == null) {
                Log.i("SingleWeatherFromApi: ", "infos == null");
            }

        } catch (Exception e) {
            Log.e("SingleWeatherFromApi: ", "infos.toString() nulled -> exception");
        }
        if (infos.isEmpty()) return null;

        return infos.get(0);
    }


    private static WeatherInfo getWeatherFromDocument(final Coord coord, final Date date, final Document weatherDoc) {
        Log.i("WEATHERDOC", "Got into getWeatherFromDocument");
        WeatherInfo info = new WeatherInfo();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //Remove Minutes and seconds from time
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 0, 0);


        String currentDay = Helper.getGivenDateInFormat(calendar);
        NodeList timeNodes = weatherDoc.getElementsByTagName("time");
        Log.e("WEATHER_AS_FORMAT", currentDay);
        for (int i = 0; i < timeNodes.getLength(); i++) {
            //Log.i("WEATHERDOC", "Running through item: "+(i+1)+"/"+timeNodes.getLength());
            Node node = timeNodes.item(i);
            NamedNodeMap attributes = node.getAttributes();

            //Log.d("WEATHERDOC", attributes.getNamedItem("from").getNodeValue() + "|||||" + currentDay + "TRUE/FALSE: "+attributes.getNamedItem("from").getNodeValue().equals(currentDay));

            if (attributes.getNamedItem("from").getNodeValue().equals(currentDay)) {
                if (attributes.getNamedItem("to").getNodeValue().equals(currentDay)) {

                    node = node.getChildNodes().item(1);//Skip Location node
//                  for (int lol = 1; lol < node.getChildNodes().getLength(); lol++)
//                  Log.i("WEATHERDECODER", "item(" + lol + "): " + node.getChildNodes().item(lol).getNodeName());
                    info.setLocation(coord);
                    info.setTemperature(Float.parseFloat(node.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue()));
                    info.setDirection(node.getChildNodes().item(3).getAttributes().getNamedItem("name").getNodeValue());
                    info.setHumidity(Float.parseFloat(node.getChildNodes().item(7).getAttributes().getNamedItem("value").getNodeValue()));
                    info.setWindspeed(Float.parseFloat(node.getChildNodes().item(5).getAttributes().getNamedItem("mps").getNodeValue()));

                    info.setTime(date);
                    i = timeNodes.getLength();
                }
            }
        }


        Log.e("WEATHERDOC", "newInfo: " + info);
        return info;
    }

    public static ArrayList<WeatherInfo> getWeathersOnRoute(int maxDistance, String firstAddress, String secondAddress) {


        ArrayList<Step> steps = RouteDecoder.routeStepFromApi(maxDistance, firstAddress, secondAddress);
        Log.e("GETWEATHERSONROUTE", steps.toString());
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
            Log.e(TAG, "getWeathersOnRoute: StepInfo " + s.toString());
            weatherInfos.add(WeatherDecoder.getSingleWeatherFromApi(s.getCoord(), deltaTime));
            Log.e("getWeatherOnRoute", "Weatherinfo: " + weatherInfos.get(weatherInfos.size() - 1));
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

            date.setTime(date.getTime() + 86400000);
            dates.add(new Date(date.getTime()));
            Log.d("DatezLoop", date.toString() + "||" + dates.size());
        }

        Log.e("NEXTWEEK", dates.toString());


        ArrayList<WeatherInfo> infos = getMoreWeatherFromApi(coord, dates);
        Log.e("WEATHERWEATHER", infos.toString());
        return infos;

    }


}
