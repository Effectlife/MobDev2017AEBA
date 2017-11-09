package com.example.android.routetesting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.routetesting.adapters.CustomListItemAdapter;
import com.example.android.routetesting.decoders.WeatherDecoder;
import com.example.android.routetesting.models.CustomListItem;
import com.example.android.routetesting.models.WeatherInfo;

import java.util.ArrayList;

public class RouteOnWeatherActivity extends AppCompatActivity {

    private TextView addressOneText;
    private TextView addressTwoText;
    private Button calculateButton;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_on_weather);
        addressOneText = (TextView) findViewById(R.id.addressOneText);
        addressTwoText = (TextView) findViewById(R.id.addressTwoText);
        calculateButton = (Button) findViewById(R.id.calculateButton);
        listView = (ListView) findViewById(R.id.listView);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

    }

    private void update() {
        final ArrayList<WeatherInfo>[] infos = new ArrayList[1];
        final String[] addressOne = new String[1];
        final String[] addressTwo = new String[1];
        final ArrayList<CustomListItem>[] clis = new ArrayList[1];
        infos[0] = new ArrayList<>();
        clis[0] = new ArrayList<>();

        @SuppressLint("StaticFieldLeak") AsyncTask task = new AsyncTask() {
            ProgressBar bar = findViewById(R.id.routeProgressBar);
            Button button = findViewById(R.id.calculateButton);

            @Override
            protected void onPreExecute() {
                bar.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                addressOne[0] = addressOneText.getText().toString();
                addressTwo[0] = addressTwoText.getText().toString();
                Log.i("RouteOnWeatherActivity", "PreExec");
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                Log.i("RouteOnWeatherActivity", "doInBack");

                infos[0] = WeatherDecoder.getWeathersOnRoute(50000, addressOne[0], addressTwo[0]);
                Session.routeInfo = infos[0];
                clis[0] = WeatherInfo.convertListWeatherToListCLI(infos[0], true, true);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.i("RouteOnWeatherActivity", "PostExec");
                listView.setAdapter(new CustomListItemAdapter(getApplicationContext(), clis[0]));
                Log.i("RouteOnWeatherActivity", "adapter set");
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Session.currentSelectedInfo = Session.routeInfo.get(position);
                        startActivity(new Intent(parent.getContext(), DetailActivity.class));
                    }
                });
                bar.setVisibility(View.GONE);
                button.setEnabled(true);
            }
        };
        task.execute();
    }
}