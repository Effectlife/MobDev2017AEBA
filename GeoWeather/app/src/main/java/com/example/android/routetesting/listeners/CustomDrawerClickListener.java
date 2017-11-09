package com.example.android.routetesting.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.routetesting.AboutActivity;
import com.example.android.routetesting.RouteOnWeatherActivity;
import com.example.android.routetesting.SettingsActivity;

/**
 * Created by AaronEnglerPXL on 19/10/2017.
 */

public class CustomDrawerClickListener implements ListView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {

            case 0:
                intent = new Intent(parent.getContext(), SettingsActivity.class);
                parent.getContext().startActivity(intent);

                break;
            case 1:
                intent = new Intent(parent.getContext(), RouteOnWeatherActivity.class);
                parent.getContext().startActivity(intent);
                break;
            case 2:
                intent = new Intent(parent.getContext(), AboutActivity.class);
                parent.getContext().startActivity(intent);
                break;
            default:
                break;
        }


    }
}
