package com.example.android.routetesting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.routetesting.adapters.CustomListItemAdapter;
import com.example.android.routetesting.adapters.CustomMenuItemAdapter;
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


        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new CustomMenuItemAdapter(getApplicationContext(), MenuItemGenerator.generate()));
        drawerList.setOnItemClickListener(new CustomDrawerClickListener());

        formatWeatherDetail();
        populateWeekList();

        Button reloadButton = (Button) findViewById(R.id.reloadBtn);




        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ClickListener", "Clicked");
                formatWeatherDetail();
                populateWeekList();
            }
        });

    }


    public void populateWeekList() {
        Log.i("POPULATING", "WeekList");
        GPSTracker tracker = new GPSTracker(this, this);
        ArrayList<WeatherInfo> weathers = WeatherDecoder.getNextWeekInfo(new Coord(tracker.getLatitude(), tracker.getLongitude()));


        ListView weathersList = (ListView) findViewById(R.id.forecastListView);
        weathersList.setAdapter(new CustomListItemAdapter(this, WeatherInfo.convertListWeatherToListCLI(weathers), false));

    }


    private void formatWeatherDetail() {
        Log.i("FORMATTING", "WeatherDetail");

        GPSTracker tracker = new GPSTracker(this, this);


        WeatherInfo info = WeatherDecoder.getSingleWeatherFromApi(new Coord(tracker.getLatitude(), tracker.getLongitude()), Calendar.getInstance().getTime());

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
