package com.example.android.routetesting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.routetesting.adapters.CustomListItemAdapter;
import com.example.android.routetesting.adapters.CustomMenuItemAdapter;
import com.example.android.routetesting.decoders.RouteDecoder;
import com.example.android.routetesting.decoders.WeatherDecoder;
import com.example.android.routetesting.fragments.WeatherDetailFragment;
import com.example.android.routetesting.fragments.WeekForecastFragment;
import com.example.android.routetesting.generators.MenuItemGenerator;
import com.example.android.routetesting.listeners.CustomDrawerClickListener;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.CustomListItem;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {


    //WeatherIcons come from
    //https://vclouds.deviantart.com/art/VClouds-Weather-Icons-179152045

    private static Context context;
    FragmentManager fragmentManager;
    //SwipeRefreshLayout swipeContainer;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();


        setContentView(R.layout.activity_main);

        WeekForecastFragment weekForecastFragment = new WeekForecastFragment();
        WeatherDetailFragment weatherDetailFragment = new WeatherDetailFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.week_forecast_container, weekForecastFragment, "wfd")
                .add(R.id.weather_detail_container, weatherDetailFragment, "wdfd")
                .commit();


        setupFirstView();

    }

    //Setup the drawer
    private void setupFirstView() {
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new CustomMenuItemAdapter(getApplicationContext(), MenuItemGenerator.generate()));
        drawerList.setOnItemClickListener(new CustomDrawerClickListener());
    }



//    public void onRefresh() {
//        Log.i("MainRefresh", "main");
//
//
//        AsyncTask<Object, Void, Object[]> task = new AsyncTask<Object, Void, Object[]>() {
////            ProgressBar bar = findViewById(R.id.mainProgressBar);
//
//            private Coord coord;
//
//            @Override
//            protected void onPreExecute() {
//
//                swipeContainer.setRefreshing(true);
//            }
//
//            @Override
//            protected Object[] doInBackground(Object[] params) {
//                WeekForecastFragment wfm = (WeekForecastFragment) getSupportFragmentManager().findFragmentByTag("wfd");
//                wfm.onRefresh();
//                WeatherDetailFragment wdfm = (WeatherDetailFragment) getSupportFragmentManager().findFragmentByTag("wdfd");
//                wdfm.onRefresh();
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object[] obj) {
//                super.onPostExecute(obj);
//                swipeContainer.setRefreshing(false);
//            }
//        };
//
//        task.execute();
//    }

}
