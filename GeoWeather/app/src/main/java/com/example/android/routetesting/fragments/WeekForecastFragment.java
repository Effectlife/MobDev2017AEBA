package com.example.android.routetesting.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.routetesting.DetailActivity;
import com.example.android.routetesting.GPSTracker;
import com.example.android.routetesting.MainActivity;
import com.example.android.routetesting.R;
import com.example.android.routetesting.Session;
import com.example.android.routetesting.adapters.CustomListItemAdapter;
import com.example.android.routetesting.decoders.RouteDecoder;
import com.example.android.routetesting.decoders.WeatherDecoder;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.CustomListItem;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;
import static com.example.android.routetesting.MainActivity.getAppContext;

/**
 * Created by 11500399 on 08-Nov-17.
 */


public class WeekForecastFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeContainer;
    public WeekForecastFragment() {

    }

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_week_forecast, container, false);


        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.week_swipe);
        swipeContainer.setOnRefreshListener(this);

        onRefresh();

        return rootView;

        //return super.onCreateView(inflater, container, savedInstanceState);
    }
    private static String getPrefLocation() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        String pref = sharedPrefs.getString("pref_location", "DEFAULT");
        Log.e("WeekPREFPREF", "pref: " + pref);
        return pref;
    }

    public static Coord loadLocationInfo(boolean gpsOverride) {
        String location = getPrefLocation();
        Log.i("WEEKFORECAST", location);
        if (location.equals("GPS") || gpsOverride) {
            GPSTracker tracker = new GPSTracker(getAppContext(), new Activity());
            return new Coord(tracker.getLatitude(), tracker.getLongitude());
        }
        Log.i("WEEKFORECAST", "starting RouteDecoder.Geolocator");
        return RouteDecoder.geoLocator(location);
    }

    private WeatherInfo fetchWeatherDetails(Coord coord) {
        Log.i("FORMATTING", "WeatherDetail");
        WeatherInfo info;

        if (coord.equals(new Coord(1000, 1000))) {

            info = new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0);

        } else {
            info = WeatherDecoder.getSingleWeatherFromApi(coord, Calendar.getInstance().getTime());
        }
        if (info == null) {
            Log.i("FORMATDETAIL", "info == null, Creating default weather");
            info = new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0);
        }

        return info;

    }

    private ArrayList<WeatherInfo> fetchWeekInfo(Coord coord) {
        if (coord.equals(new Coord(1000, 1000))) {
            Log.i("Coordinate", "TRUE  " + coord.toString());
            return null;
        } else {
            Log.i("Coordinate", "FALSE " + coord.toString());
            return WeatherDecoder.getNextWeekInfo(coord);
        }
    }

    @Override
    public void onRefresh() {
        AsyncTask<Object, Void, Object[]> task = new AsyncTask<Object, Void, Object[]>() {
//            ProgressBar bar = findViewById(R.id.mainProgressBar);

            private Coord coord;

            @Override
            protected void onPreExecute() {
                coord = loadLocationInfo(false);
                swipeContainer.setRefreshing(true);
            }

            @Override
            protected Object[] doInBackground(Object[] params) {

                Object[] obj = new Object[2];
                obj[0] = fetchWeatherDetails(coord);
                obj[1] = fetchWeekInfo(coord);
                return obj;
            }

            @Override
            protected void onPostExecute(Object[] obj) {
                super.onPostExecute(obj);
                populateWeekList((ArrayList<WeatherInfo>)obj[1]);
                swipeContainer.setRefreshing(false);
            }
        };

        task.execute();
    }


    public void populateWeekList(final ArrayList<WeatherInfo> weatherInfos) {
        Log.i("POPULATING", "1");


        for (WeatherInfo info : weatherInfos) {
            Log.i("POPULATING", "AllInfos: " + info);

        }
        Log.i("POPULATING", "2");

        ListView weathersList = (ListView) rootView.findViewById(R.id.week_forecast_list_view);

        Log.i("POPULATING", "3");
        final ArrayList<CustomListItem> customListItems = WeatherInfo.convertListWeatherToListCLI(weatherInfos, false, false);

        weathersList.setAdapter(new CustomListItemAdapter(getAppContext(), customListItems));
        Log.i("POPULATING", "4");
        Log.i("POPULATING", "Adapter is set");

        try {
            Log.i("POPULATING", "5");
            weathersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Session.currentSelectedInfo = weatherInfos.get(position);
                    Session.selectedDay = weatherInfos.get(position).getDay();

                    startActivity(new Intent(parent.getContext(), DetailActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("POPULATING", "6");

    }
}
