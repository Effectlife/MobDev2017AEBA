package com.example.android.routetesting;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.routetesting.adapters.CustomListItemAdapter;
import com.example.android.routetesting.adapters.CustomMenuItemAdapter;
import com.example.android.routetesting.decoders.RouteDecoder;
import com.example.android.routetesting.decoders.WeatherDecoder;
import com.example.android.routetesting.generators.MenuItemGenerator;
import com.example.android.routetesting.listeners.CustomDrawerClickListener;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    //WeatherIcons come from
    //https://vclouds.deviantart.com/art/VClouds-Weather-Icons-179152045


    private static Context context;
    private SwipeRefreshLayout swipeContainer;


    public static Context getAppContext() {
        return MainActivity.context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();


        setContentView(R.layout.activity_main);
        setupFirstView();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.content_frame);
        swipeContainer.setOnRefreshListener(this);
        onRefresh();

    }


    private void setupFirstView() {
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new CustomMenuItemAdapter(getApplicationContext(), MenuItemGenerator.generate()));
        drawerList.setOnItemClickListener(new CustomDrawerClickListener());

    }


    private boolean getNotificationGPS() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        return sharedPrefs.getBoolean("pref_notificationGPS", false);
    }





    public void populateWeekList(final ArrayList<WeatherInfo> weatherInfos) {
        Log.i("POPULATING", "1");


        for (WeatherInfo info : weatherInfos) {
            Log.i("POPULATING", "AllInfos: " + info);

        }
        Log.i("POPULATING", "2");

        ListView weathersList = (ListView) findViewById(R.id.forecastListView);

        Log.i("POPULATING", "3");
        weathersList.setAdapter(new CustomListItemAdapter(MainActivity.getAppContext(), WeatherInfo.convertListWeatherToListCLI(weatherInfos, false, false)));
        Log.i("POPULATING", "4");
        Log.i("POPULATING", "Adapter is set");

        try {
            Log.i("POPULATING", "5");
            weathersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Session.currentSelectedInfo = weatherInfos.get(position);
                    startActivity(new Intent(parent.getContext(), DetailActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("POPULATING", "6");

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


        TextView dayTv = (TextView) findViewById(R.id.weatherDetailDay);
        TextView tempTv = (TextView) findViewById(R.id.weatherDetailTemp);
        TextView cityTv = (TextView) findViewById(R.id.weatherDetailCity);
        ImageView statusImage = (ImageView) findViewById(R.id.weatherDetailStatusImage);
        TextView humidityTv = (TextView) findViewById(R.id.weatherDetailHumiditySmall);
        TextView lowTempTv = (TextView) findViewById(R.id.weatherDetailTempLowSmall);
        TextView highTempTv = (TextView) findViewById(R.id.weatherDetailTempHighSmall);
        TextView windTv = (TextView) findViewById(R.id.weatherDetailWindSmall);

        dayTv.setVisibility(GONE);
        tempTv.setText((temperature != null ? temperature : "NoTempFound") + "");
        lowTempTv.setText((minTemp != null ? minTemp : "NoTempFound") + "");
        highTempTv.setText((maxTemp != null ? maxTemp : "NoTempFound") + "");


        int iconResId = getResources().getIdentifier("weathericon" + info.getSymbol(), "drawable", getPackageName());
        Log.i("IMGSYMBOL", iconResId + " " + info.getSymbol());

        if (iconResId == 0) {
            iconResId = getResources().getIdentifier("na", "drawable", getPackageName());
        }

        statusImage.setImageResource(iconResId);


        cityTv.setText(info != null ? info.getLocation().getCityName() : "NoCityFound");
        humidityTv.setText((info != null ? info.getHumidity() : "NoHumidityFound") + "");
        windTv.setText((info != null ? info.getWindspeed() : "NoWindspeedFound") + " " + (info != null ? info.getDirection() : "NoDirectionFound"));


    }


    private static String getPrefLocation() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        String pref = sharedPrefs.getString("pref_location", "DEFAULT");
        Log.e("PREFPREF", "pref: " + pref);
        return pref;
    }

    //TODO: fix geolocation
    public static Coord loadLocationInfo(boolean gpsOverride) {
        String location = getPrefLocation();
        if (location.equals("GPS") || gpsOverride) {
            GPSTracker tracker = new GPSTracker(getAppContext(), new Activity());
            return new Coord(tracker.getLatitude(), tracker.getLongitude());
        }


        return RouteDecoder.geoLocator(location);
    }

    private void addNotification(float temperature, String locationName) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.main_icon)
                .setContentTitle("GeoWeather")
                .setContentText("The temp in " + locationName + " is " + temperature);

        NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(1, mBuilder.build());


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

//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        WeatherInfo currentWeatherGPS = WeatherDecoder.getSingleWeatherFromApi(loadLocationInfo(MainActivity.this, getNotificationGPS()), Calendar.getInstance().getTime());
//
//                        addNotification(currentWeatherGPS.getTemperature(), currentWeatherGPS.getLocation().getCityName());
//
//
//                    }
//                });

                Object[] obj = new Object[2];
                obj[0] = fetchWeatherDetails(coord);
                obj[1] = fetchWeekInfo(coord);
                return obj;

            }


            @Override
            protected void onPostExecute(Object[] obj) {
                super.onPostExecute(obj);
                formatWeatherDetail((WeatherInfo)obj[0]);
                populateWeekList((ArrayList<WeatherInfo>)obj[1]);
//                bar.setVisibility(GONE);
                swipeContainer.setRefreshing(false);
            }
        };

        task.execute();
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
}
