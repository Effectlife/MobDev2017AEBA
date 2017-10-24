package com.example.android.routetesting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupFirstView();

    }

    private void setupFirstView() {
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new CustomMenuItemAdapter(getApplicationContext(), MenuItemGenerator.generate()));
        drawerList.setOnItemClickListener(new CustomDrawerClickListener());

        //formatWeatherDetail();
        //populateWeekList();

        Button reloadButton = (Button) findViewById(R.id.reloadBtn);


        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formatWeatherDetail();
                populateWeekList();
            }
        });
    }


    public void populateWeekList() {
        Log.i("POPULATING", "WeekList");




        ArrayList<WeatherInfo> weathers = WeatherDecoder.getNextWeekInfo(loadLocationInfo());

        ListView weathersList = (ListView) findViewById(R.id.forecastListView);


        weathersList.setAdapter(new CustomListItemAdapter(this, WeatherInfo.convertListWeatherToListCLI(weathers, false)));

    }

    private String getPrefLocation(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String pref = sharedPrefs.getString("pref_location","DEFAULT");
        Log.e("PREFPREF", "pref: "+pref);
        return pref;
    }

    //TODO: fix geolocation
    private Coord loadLocationInfo(){
       String location = getPrefLocation();
        if(location.equals("GPS")){
            GPSTracker tracker = new GPSTracker(this, this);
            return new Coord(tracker.getLatitude(), tracker.getLongitude());
        }


        return RouteDecoder.geoLocator(location);
    }


    private void formatWeatherDetail() {
        Log.i("FORMATTING", "WeatherDetail");

        WeatherInfo info = WeatherDecoder.getSingleWeatherFromApi(loadLocationInfo(), Calendar.getInstance().getTime());

        TextView dayTv = (TextView) findViewById(R.id.weatherDetailDay);
        TextView tempTv = (TextView) findViewById(R.id.weatherDetailTemp);
        TextView cityTv = (TextView) findViewById(R.id.weatherDetailCity);
        ImageView statusImage = (ImageView) findViewById(R.id.weatherDetailStatusImage);
        TextView humidityTv = (TextView) findViewById(R.id.weatherDetailHumiditySmall);
        TextView lowTempTv = (TextView) findViewById(R.id.weatherDetailTempLowSmall);
        TextView highTempTv = (TextView) findViewById(R.id.weatherDetailTempHighSmall);
        TextView windTv = (TextView) findViewById(R.id.weatherDetailWindSmall);

        dayTv.setVisibility(GONE);
        tempTv.setText((info != null ? info.getTemperature() : "NoTempFound") + "");
        cityTv.setText(info != null ? info.getLocation().getCityName() : "NoCityFound");
        humidityTv.setText((info != null ? info.getHumidity() : "NoHumidityFound") + "");
        windTv.setText((info != null ? info.getWindspeed() : "NoWindspeedFound") + " " + (info != null ? info.getDirection() : "NoDirectionFound"));


    }


}
