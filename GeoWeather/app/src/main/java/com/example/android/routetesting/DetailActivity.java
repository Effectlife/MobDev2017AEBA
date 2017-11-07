package com.example.android.routetesting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.WeatherInfo;

import static android.view.View.GONE;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.i("FORMATTING", "WeatherDetail");

        WeatherInfo info = Session.currentSelectedInfo;
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


        int iconResId = getResources().getIdentifier("weathericon"+info.getSymbol(), "drawable",getPackageName());
        Log.i("IMGSYMBOL", iconResId + " "+info.getSymbol());

        if(iconResId == 0){
            iconResId = getResources().getIdentifier("na", "drawable", getPackageName());
        }

        statusImage.setImageResource(iconResId);



        dayTv.setText((Session.selectedDay));
        tempTv.setText((temperature != null ? temperature : "NoTempFound") + "");
        lowTempTv.setText((minTemp != null ? minTemp : "NoTempFound") + "");
        highTempTv.setText((maxTemp != null ? maxTemp : "NoTempFound") + "");

        cityTv.setText(info != null ? info.getLocation().getCityName() : "NoCityFound");
        humidityTv.setText((info != null ? info.getHumidity() : "NoHumidityFound") + "");
        windTv.setText((info != null ? info.getWindspeed() : "NoWindspeedFound") + " " + (info != null ? info.getDirection() : "NoDirectionFound"));




    }
}
