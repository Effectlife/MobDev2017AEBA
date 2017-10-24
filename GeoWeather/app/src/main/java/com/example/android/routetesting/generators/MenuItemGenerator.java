package com.example.android.routetesting.generators;

import com.example.android.routetesting.models.CustomMenuItem;

import java.util.ArrayList;

/**
 * Created by AaronEnglerPXL on 19/10/2017.
 */

public abstract class MenuItemGenerator {
    private static String[] menuEntries = {"Location", "Settings", "Weather on Route", "About", "Exit"};

    public static ArrayList<CustomMenuItem> generate(){
        ArrayList<CustomMenuItem> items = new ArrayList<>();
        for (String s:menuEntries) {
            items.add(new CustomMenuItem(null, s));
        }
        return items;
    }
}
