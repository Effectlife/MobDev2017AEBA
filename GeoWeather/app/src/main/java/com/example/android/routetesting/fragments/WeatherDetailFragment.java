package com.example.android.routetesting.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.example.android.routetesting.Session;
import com.example.android.routetesting.decoders.RouteDecoder;
import com.example.android.routetesting.decoders.WeatherDecoder;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.WeatherInfo;

import java.util.Calendar;

import static android.view.View.GONE;
import static com.example.android.routetesting.MainActivity.getAppContext;

/**
 * Created by 11500399 on 08-Nov-17.
 */

public class WeatherDetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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

        rootView = inflater.inflate(R.layout.fragment_weather_detail, container, false);
        swipeContainer = rootView.findViewById(R.id.wdfRefresh);
        swipeContainer.setOnRefreshListener(this);

        onRefresh();

        return rootView;
    }

    private static String getPrefLocation() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        String pref = sharedPrefs.getString("pref_location", "DEFAULT");
        Log.e("DetailPREFPREF", "pref: " + pref);
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

    private WeatherInfo fetchWeatherDetails(Coord coord) {

        WeatherInfo info = Session.currentSelectedInfo;
        int detail = Session.detailScreen;

        if (info == null) {
            if (coord.equals(new Coord(1000, 1000))) {
                return new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0);
            }
        }
        if (detail == 1) {
            return Session.currentSelectedInfo;
        } else {
            return WeatherDecoder.getSingleWeatherFromApi(coord, Calendar.getInstance().getTime());
        }
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

        TextView dayTv = rootView.findViewById(R.id.weatherDetailDay2);
        TextView tempTv = rootView.findViewById(R.id.weatherDetailTemp2);
        TextView cityTv = rootView.findViewById(R.id.weatherDetailCity2);
        ImageView statusImage = rootView.findViewById(R.id.weatherDetailStatusImage2);
        TextView humidityTv = rootView.findViewById(R.id.weatherDetailHumiditySmall2);
        TextView lowTempTv = rootView.findViewById(R.id.weatherDetailTempLowSmall2);
        TextView highTempTv = rootView.findViewById(R.id.weatherDetailTempHighSmall2);
        TextView windTv = rootView.findViewById(R.id.weatherDetailWindSmall2);

        dayTv.setVisibility(GONE);
        if (Session.selectedDay == null || Session.selectedDay.equals("")) {

        } else {
            dayTv.setVisibility(View.VISIBLE);
            dayTv.setText(Session.selectedDay);
        }

        tempTv.setText((temperature != null ? temperature : "NoTempFound") + "");
        lowTempTv.setText((minTemp != null ? minTemp : "NoTempFound") + "");
        highTempTv.setText((maxTemp != null ? maxTemp : "NoTempFound") + "");

        int iconResId = getResources().getIdentifier("weathericon" + info.getSymbol(), "drawable", getActivity().getPackageName());

        if (iconResId == 0) {
            iconResId = getResources().getIdentifier("na", "drawable", getActivity().getPackageName());
        }

        statusImage.setImageResource(iconResId);

        cityTv.setText(info != null ? info.getLocation().getCityName(true) : "NoCityFound");
        humidityTv.setText((info != null ? (info.getHumidity()) : "NoHumidityFound") + "");
        windTv.setText((info != null ? info.getWindspeed() : "NoWindspeedFound") + " " + (info != null ? info.getDirection() : "NoDirectionFound"));
        Session.selectedDay = null;
    }

    private boolean getNotificationGPS() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        return sharedPrefs.getBoolean("pref_notificationGPS", false);
    }

    private void addNotification(float temperature, String locationName) {
        Log.i("NOTIFICATION", "starting");

        Intent intent = new Intent(MainActivity.getAppContext(), MainActivity.class); // Here pass your activity where you want to redirect.

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), (int) (Math.random() * 100), intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.myContext, "default")
                .setSmallIcon(R.drawable.main_icon)
                .setContentTitle("GeoWeather")
                .setContentText("The temp in " + locationName + " is " + temperature).setContentIntent(contentIntent);

        NotificationManager mManager = (NotificationManager) MainActivity.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(1, mBuilder.build());
    }

    @Override
    public void onRefresh() {

        Log.i("Weatherdetail", "refreshed Session.detail: "+ Session.detailScreen);
        final WeatherInfo[] currentWeatherGPS = new WeatherInfo[1];
        @SuppressLint("StaticFieldLeak") AsyncTask<Object, Void, Object[]> task = new AsyncTask<Object, Void, Object[]>() {

            private Coord coord;
            private Coord notificationCoord;

            @Override
            protected void onPreExecute() {

                swipeContainer.setRefreshing(true);
                coord = loadLocationInfo(false);

                notificationCoord = loadLocationInfo(getNotificationGPS()); //boolean = true if always gps

                WeekForecastFragment wfm;
                if (Session.detailScreen == 0) {
                    wfm = (WeekForecastFragment) myContext.getSupportFragmentManager().findFragmentByTag("wfd");
                    try {
                        wfm.onRefresh();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }

            @Override
            protected Object[] doInBackground(Object[] params) {

                Log.i("WDF", "dodoing " + coord + "; notifcoord: " + notificationCoord);

                Object[] obj = new Object[2];
                obj[0] = fetchWeatherDetails(coord);
                Log.i("WDF", "fetched weatherdetails: " + obj[0]);
                currentWeatherGPS[0] = WeatherDecoder.getSingleWeatherFromApi(notificationCoord, Calendar.getInstance().getTime());
                Log.i("WDF", "fetched weatherinfoGPS" + currentWeatherGPS[0]);

                return obj;
            }

            @Override
            protected void onPostExecute(Object[] obj) {
                super.onPostExecute(obj);
                formatWeatherDetail((WeatherInfo) obj[0]);
                String cityname = currentWeatherGPS[0].getLocation().getCityName(true);
                Log.i("WDF", "recieved cityname " + cityname);
                addNotification(currentWeatherGPS[0].getTemperature(), cityname);
                swipeContainer.setRefreshing(false);
                Session.detailScreen = 0;
            }
        };
        task.execute();

        WeatherInfo currentSelected = Session.currentSelectedInfo;

        Log.i("DEBUG_DEBUG", (currentSelected == null ? "Current Null" : currentSelected).toString());

    }
}
