package com.example.android.routetesting.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.routetesting.R;
import com.example.android.routetesting.lookups.Weekday;
import com.example.android.routetesting.models.CustomListItem;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by AaronEnglerPXL on 17/10/2017.
 */

public class CustomListItemAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<CustomListItem> items;
    private boolean city;

    public CustomListItemAdapter(Context ctx, ArrayList<CustomListItem> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (View) inflater.inflate(
                    R.layout.custom_list_item, null);
        }

        TextView temperatureTextView = (TextView) convertView.findViewById(R.id.temperature);
        TextView cityTextView = (TextView) convertView.findViewById(R.id.day);

        temperatureTextView.setText(items.get(position).getTemperature() + "");

        cityTextView.setText(items.get(position).getCity());

        return convertView;
    }
}
