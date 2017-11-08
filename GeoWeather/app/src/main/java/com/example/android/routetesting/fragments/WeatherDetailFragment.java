package com.example.android.routetesting.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.routetesting.GPSTracker;
import com.example.android.routetesting.MainActivity;
import com.example.android.routetesting.R;
import com.example.android.routetesting.decoders.RouteDecoder;
import com.example.android.routetesting.decoders.WeatherDecoder;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;
import static com.example.android.routetesting.MainActivity.getAppContext;

/**
 * Created by 11500399 on 08-Nov-17.
 */

public class WeatherDetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeContainer;
    private View rootView;

    private FragmentActivity myContext;

    @Override
    public void onAttach(Context context) {
        myContext = (FragmentActivity) context;
        super.onAttach(context);
    }

    public WeatherDetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_weather_detail,container,false);
        swipeContainer = rootView.findViewById(R.id.wdfRefresh);
swipeContainer.setOnRefreshListener(this);

onRefresh();

        Log.i("WEATHERDETAILFRAG", "Created rootview and found swipecontainer");

        return rootView;
    }

    private static String getPrefLocation() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        String pref = sharedPrefs.getString("pref_location", "DEFAULT");
        Log.e("DetailPREFPREF", "pref: " + pref);
        return pref;
    }

    public static Coord loadLocationInfo(boolean gpsOverride) {
        String location = getPrefLocation();
        if (location.equals("GPS") || gpsOverride) {
            GPSTracker tracker = new GPSTracker(getAppContext(), new Activity());
            return new Coord(tracker.getLatitude(), tracker.getLongitude());
        }

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

    private void formatWeatherDetail(WeatherInfo info) {


        Log.i("FORMATDETAIL", info.toString());
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        boolean fahrenheit = sharedPrefs.getBoolean("pref_fahrenheit", false);
        String temperature = null;
        String maxTemp = null;
        String minTemp = null;


        try {
            if (fahrenheit) {
                temperature = Helper.celsiusToFahrenheit(info.getTemperature()) + " °F";
                minTemp = Helper.celsiusToFahrenheit(info.getMinTemp()) + " °F";
                maxTemp = Helper.celsiusToFahrenheit(info.getMaxTemp()) + " °F";
            } else {
                temperature = info.getTemperature() + " °C";
                minTemp = info.getMinTemp() + " °C";
                maxTemp = info.getMaxTemp() + " °C";
            }
        } catch (
                Exception e) {
        }


        TextView dayTv = (TextView) rootView.findViewById(R.id.weatherDetailDay2);
        TextView tempTv = (TextView) rootView.findViewById(R.id.weatherDetailTemp2);
        TextView cityTv = (TextView) rootView.findViewById(R.id.weatherDetailCity2);
        ImageView statusImage = (ImageView) rootView.findViewById(R.id.weatherDetailStatusImage2);
        TextView humidityTv = (TextView) rootView.findViewById(R.id.weatherDetailHumiditySmall2);
        TextView lowTempTv = (TextView) rootView.findViewById(R.id.weatherDetailTempLowSmall2);
        TextView highTempTv = (TextView) rootView.findViewById(R.id.weatherDetailTempHighSmall2);
        TextView windTv = (TextView) rootView.findViewById(R.id.weatherDetailWindSmall2);

        dayTv.setVisibility(GONE);
        tempTv.setText((temperature != null ? temperature : "NoTempFound") + "");
        lowTempTv.setText((minTemp != null ? minTemp : "NoTempFound") + "");
        highTempTv.setText((maxTemp != null ? maxTemp : "NoTempFound") + "");


//        int iconResId = getResources().getIdentifier("weathericon" + info.getSymbol(), "drawable", getPackageName());
//        Log.i("IMGSYMBOL", iconResId + " " + info.getSymbol());
//
//        if (iconResId == 0) {
//            iconResId = getResources().getIdentifier("na", "drawable", getPackageName());
//        }

        //statusImage.setImageResource(iconResId);


        cityTv.setText(info != null ? info.getLocation().getCityName() : "NoCityFound");
        humidityTv.setText((info != null ? info.getHumidity() : "NoHumidityFound") + "");
        windTv.setText((info != null ? info.getWindspeed() : "NoWindspeedFound") + " " + (info != null ? info.getDirection() : "NoDirectionFound"));
    }

    private boolean getNotificationGPS() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        return sharedPrefs.getBoolean("pref_notificationGPS", false);
    }

    private void addNotification(float temperature, String locationName) {
        Log.i("NOTIFICATION", "starting");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.myContext, "default")
                .setSmallIcon(R.drawable.main_icon)
                .setContentTitle("GeoWeather")
                .setContentText("The temp in " + locationName + " is " + temperature);
        Log.i("NOTIFICATION", "created builder");

        NotificationManager mManager = (NotificationManager) MainActivity.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("NOTIFICATION", "fetched manager");
        mManager.notify(1, mBuilder.build());
        Log.i("NOTIFICATION", "build and notified");

    }



    @Override
    public void onRefresh() {

        Log.i("Weatherdetail", "refreshed");
        final WeatherInfo[] currentWeatherGPS = new WeatherInfo[1];
        AsyncTask<Object, Void, Object[]> task = new AsyncTask<Object, Void, Object[]>() {
//            ProgressBar bar = findViewById(R.id.mainProgressBar);

            private Coord coord;
            private Coord notificationCoord;

            @Override
            protected void onPreExecute() {

                swipeContainer.setRefreshing(true);
                coord = loadLocationInfo(false);

                notificationCoord = loadLocationInfo(getNotificationGPS());
                WeekForecastFragment wfm = (WeekForecastFragment) myContext.getSupportFragmentManager().findFragmentByTag("wfd");
                wfm.onRefresh();
                Log.i("WDF", "preexec-done");
            }

            @Override
            protected Object[] doInBackground(Object[] params) {

Log.i("WDF", "doing in background");

                Object[] obj = new Object[2];
                obj[0] = fetchWeatherDetails(coord);
                Log.i("WDF", "fetched weatherdetails: "+obj[0]);
                currentWeatherGPS[0] = WeatherDecoder.getSingleWeatherFromApi(notificationCoord, Calendar.getInstance().getTime());
                Log.i("WDF", "fetched weatherinfoGPS" + currentWeatherGPS[0]);




                return obj;
            }
            @Override
            protected void onPostExecute(Object[] obj) {
                super.onPostExecute(obj);
                formatWeatherDetail((WeatherInfo)obj[0]);
                String cityname = currentWeatherGPS[0].getLocation().getCityName();
                Log.i("WDF", "recieved cityname "+cityname);
                addNotification(currentWeatherGPS[0].getTemperature(), cityname);
                swipeContainer.setRefreshing(false);
            }
        };

        task.execute();
    }
}
