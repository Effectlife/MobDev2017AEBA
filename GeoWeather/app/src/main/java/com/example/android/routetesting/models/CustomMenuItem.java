package com.example.android.routetesting.models;


import android.graphics.drawable.Drawable;

/**
 * Created by AaronEnglerPXL on 19/10/2017.
 */

public class CustomMenuItem {

    private Drawable image;
    private String title;

    public CustomMenuItem(Drawable image, String title) {
        this.image = image;
        this.title = title;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {



        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
