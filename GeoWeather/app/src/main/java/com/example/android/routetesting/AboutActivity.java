package com.example.android.routetesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.android.routetesting.R;

import java.util.Calendar;
import java.util.Locale;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element versionElement = new Element();
        versionElement.setTitle("Version 0.5");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getResources().getString(R.string.geoweather_about_message))
                .setImage(R.drawable.main_icon)
                .addItem(versionElement)
                .addGitHub("Effectlife/MobDev2017AEBA")
                .addWebsite("https://www.pxl.be")
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }

    private Element createCopyright() {
        final Element copyright = new Element();

        final String copyrightString = String.format(Locale.getDefault(),"Copyright %d by AE and BA", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutActivity.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}
