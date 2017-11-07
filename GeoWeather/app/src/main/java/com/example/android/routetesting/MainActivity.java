package com.example.android.routetesting;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class MainActivity extends AppCompatActivity {


    //WeatherIcons come from
    //https://vclouds.deviantart.com/art/VClouds-Weather-Icons-179152045


    private static Context context;


    public static Context getAppContext() {
        return MainActivity.context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        setupFirstView();


    }


    private void setupFirstView() {
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new CustomMenuItemAdapter(getApplicationContext(), MenuItemGenerator.generate()));
        drawerList.setOnItemClickListener(new CustomDrawerClickListener());

        update();


        Button reloadButton = (Button) findViewById(R.id.reloadBtn);


        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
    }

    private void update() {


        AsyncTask task = new AsyncTask() {
            ProgressBar bar = findViewById(R.id.mainProgressBar);


            @Override
            protected void onPreExecute() {
                bar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] params) {

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        WeatherInfo currentWeatherGPS = WeatherDecoder.getSingleWeatherFromApi(loadLocationInfo(MainActivity.this, getNotificationGPS()), Calendar.getInstance().getTime());

                        addNotification(currentWeatherGPS.getTemperature(), currentWeatherGPS.getLocation().getCityName());

                        formatWeatherDetail();
                        populateWeekList();
                    }
                });


                return null;

            }

            @Override
            protected void onPostExecute(Object o) {
                bar.setVisibility(GONE);
                super.onPostExecute(o);
            }
        };

        task.execute();


    }


    private boolean getNotificationGPS() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        return sharedPrefs.getBoolean("pref_notificationGPS", false);
    }


    public void populateWeekList() {
        Log.i("POPULATING", "WeekList");


        Coord locinfo = loadLocationInfo(new Activity(), false);
        if (locinfo.equals(new Coord(1000, 1000))) {
            Log.i("Coordinate", "TRUE  " + locinfo.toString());
            return;
        } else {
            Log.i("Coordinate", "FALSE " + locinfo.toString());
            Session.weekInfo = WeatherDecoder.getNextWeekInfo(locinfo);
        }


        for (WeatherInfo info : Session.weekInfo) {
            Log.i("POPULATING", "AllInfos: " + info);

        }


        ListView weathersList = (ListView) findViewById(R.id.forecastListView);


        weathersList.setAdapter(new CustomListItemAdapter(MainActivity.getAppContext(), WeatherInfo.convertListWeatherToListCLI(Session.weekInfo, false, false)));
        Log.i("POPULATING", "Adapter is set");

        try {
            weathersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Session.currentSelectedInfo = Session.weekInfo.get(position);
                    startActivity(new Intent(parent.getContext(), DetailActivity.class));
                }
            });
        } catch (Exception e) {
           e.printStackTrace();
        }


    }


    private void formatWeatherDetail() {


        Log.i("FORMATTING", "WeatherDetail");
        WeatherInfo info;
        Coord coord = loadLocationInfo(this, false);
        if (coord.equals(new Coord(1000, 1000))) {

            info = new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0);

        } else {
            info = WeatherDecoder.getSingleWeatherFromApi(coord, Calendar.getInstance().getTime());
        }
        if (info == null) {
            Log.i("FORMATDETAIL", "info == null, Creating default weather");
            info = new WeatherInfo(new Coord(0, 0), 0f, 0f, 0f, 0f, 0f, "NONE", Calendar.getInstance().getTime(), 0);
        }
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
    public static Coord loadLocationInfo(Activity activity, boolean gpsOverride) {
        String location = getPrefLocation();
        if (location.equals("GPS") || gpsOverride) {
            GPSTracker tracker = new GPSTracker(getAppContext(), activity);
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

}
