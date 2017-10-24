package com.example.android.routetesting.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.routetesting.RouteOnWeatherActivity;

/**
 * Created by AaronEnglerPXL on 19/10/2017.
 */

public class CustomDrawerClickListener implements ListView.OnItemClickListener{

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position){
            case 0:
                break;
            case 1:
                break;
            case 2:
                Intent intent = new Intent(parent.getContext(), RouteOnWeatherActivity.class);
                parent.getContext().startActivity(intent);
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }




    }
}
