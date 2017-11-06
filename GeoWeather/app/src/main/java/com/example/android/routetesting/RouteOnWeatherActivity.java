package com.example.android.routetesting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.routetesting.adapters.CustomListItemAdapter;
import com.example.android.routetesting.decoders.WeatherDecoder;
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
                ArrayList<WeatherInfo> infos;
                infos = WeatherDecoder.getWeathersOnRoute(50000, addressOneText.getText().toString(), addressTwoText.getText().toString());
                Session.routeInfo=infos;
                listView.setAdapter(new CustomListItemAdapter(getApplicationContext(), WeatherInfo.convertListWeatherToListCLI(infos, true, true)));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Session.currentSelectedInfo=Session.routeInfo.get(position);
                        startActivity(new Intent(parent.getContext(), DetailActivity.class));
                    }
                });

            }
        });


    }

}
