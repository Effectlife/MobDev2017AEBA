package com.example.android.routetesting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.routetesting.fragments.WeatherDetailFragment;
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
        WeatherDetailFragment weatherDetailFragment = new WeatherDetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Session.detailScreen = 1;
        fragmentManager.beginTransaction()
                .add(R.id.weather_detail_dContainer,weatherDetailFragment,"wfd2")
                .commit();

    }
}
