package com.example.android.routetesting.generators;

import com.example.android.routetesting.models.CustomMenuItem;

import java.util.ArrayList;

/**
 * Created by AaronEnglerPXL on 19/10/2017.
 */

/**
 * small function to return CustomMenuItems for the drawer, you can easily add a string to the array and it will be
 * displayed in the drawer.
 */

public abstract class MenuItemGenerator {
    private static String[] menuEntries = {"Settings", "Weather on Route", "About"};

    public static ArrayList<CustomMenuItem> generate(){
        ArrayList<CustomMenuItem> items = new ArrayList<>();
        for (String s:menuEntries) {
            items.add(new CustomMenuItem(null, s));
        }
        return items;
    }
}
