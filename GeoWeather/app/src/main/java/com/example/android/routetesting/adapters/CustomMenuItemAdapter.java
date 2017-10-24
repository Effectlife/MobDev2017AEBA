package com.example.android.routetesting.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.routetesting.R;
import com.example.android.routetesting.models.CustomMenuItem;

import java.util.ArrayList;

/**
 * Created by AaronEnglerPXL on 19/10/2017.
 */

public class CustomMenuItemAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<CustomMenuItem> items;

    public CustomMenuItemAdapter(Context ctx, ArrayList<CustomMenuItem> items) {
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
                    R.layout.drawer_menu_item, null);
        }


        //TODO: correctly implement images in drawer menu
        //ImageView imageView = (ImageView)convertView.findViewById(R.id.menuImage);
        TextView textView = (TextView) convertView.findViewById(R.id.menuText);



        //imageView.setImageDrawable(items.get(position).getImage());
        textView.setText(items.get(position).getTitle());

        return convertView;
    }
}
