package com.example.android.routetesting.fragments;

import android.annotation.SuppressLint;
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


public class WeekForecastFragment extends Fragment {

    public WeekForecastFragment() {
    }
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_week_forecast, container, false);

        return rootView;
    }

    private static String getPrefLocation() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        String pref = sharedPrefs.getString("pref_location", "DEFAULT");
        return pref;
    }

    public static Coord loadLocationInfo(boolean gpsOverride) {
        String location = getPrefLocation().toLowerCase();
        if (location.equals("gps") || gpsOverride) {
            GPSTracker tracker = new GPSTracker(getAppContext(), new Activity());
            return new Coord(tracker.getLatitude(), tracker.getLongitude());
        }
        return RouteDecoder.geoLocator(location);
    }

    private ArrayList<WeatherInfo> fetchWeekInfo(Coord coord) {
        if (coord.equals(new Coord(1000, 1000))) {
            return null;
        } else {
            return WeatherDecoder.getNextWeekInfo(coord);
        }
    }

    public void onRefresh() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Object, Void, Object[]> task = new AsyncTask<Object, Void, Object[]>() {

            private Coord coord;
            @Override
            protected void onPreExecute() {
                coord = loadLocationInfo(false);
            }

            @Override
            protected Object[] doInBackground(Object[] params) {

                Object[] obj = new Object[1];
                obj[0] = fetchWeekInfo(coord);
                int i = 0;

                return obj;
            }

            @Override
            protected void onPostExecute(Object[] obj) {
                super.onPostExecute(obj);
                populateWeekList((ArrayList<WeatherInfo>) obj[0]);
            }
        };
        task.execute();
    }
    public void populateWeekList(final ArrayList<WeatherInfo> weatherInfos) {

        ListView weathersList = (ListView) rootView.findViewById(R.id.week_forecast_list_view);

        final ArrayList<CustomListItem> customListItems = WeatherInfo.convertListWeatherToListCLI(weatherInfos, false, false);

        weathersList.setAdapter(new CustomListItemAdapter(getAppContext(), customListItems));
        Session.weekInfo = weatherInfos;

        try {

            weathersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Session.currentSelectedInfo = weatherInfos.get(position);
                    Session.selectedDay = weatherInfos.get(position).getDay();
                    Session.detailScreen = 1;
                    startActivity(new Intent(parent.getContext(), DetailActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}