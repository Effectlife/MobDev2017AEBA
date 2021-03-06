package com.example.android.routetesting.decoders;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.routetesting.lookups.ApiUrl;
import com.example.android.routetesting.models.Coord;
import com.example.android.routetesting.models.Helper;
import com.example.android.routetesting.models.Step;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by AaronEnglerPXL on 10/10/2017.
 */

public abstract class RouteDecoder {
    @SuppressLint("StaticFieldLeak")
    public static Coord geoLocator(final String location) {
        Coord coord = null;
        try {
            coord = new AsyncTask<Object, Void, Coord>() {
                @Override
                protected Coord doInBackground(Object... params) {

                    Document doc = ApiDocumentBuilder.decode(ApiUrl.GOOGLELOC, location);

                    if (doc.getElementsByTagName("status").item(0).getTextContent().equals("ZERO_RESULTS")) {
                        return new Coord(1000, 1000); //returns an normally invalid coordinate to inform the caller the location was not found
                    }

                    NodeList lats = doc.getElementsByTagName("lat");
                    NodeList lons = doc.getElementsByTagName("lng");

                    for (int i = 0; i < lats.getLength(); i++) {
                        try {
                            //Log.e("MIEP" + i, lats.item(i).getTextContent() + "," + lons.item(i).getTextContent());
                        } catch (Exception e) {
                            Log.e("RouteDecoder", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                    return new Coord(Float.parseFloat(lats.item(0).getTextContent()), Float.parseFloat(lons.item(0).getTextContent()));
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return new Coord(0, 0);
        }
        return coord;
    }
    public static ArrayList<Step> routeStepFromApi(final int maxDistance, final String firstAddress, final String secondAddress) {

        Document directions = ApiDocumentBuilder.decode(ApiUrl.GOOGLEDIR, (String) firstAddress, (String) secondAddress);
        NodeList stepsnodes = directions.getElementsByTagName("step");
        ArrayList<Step> allSteps = new ArrayList<Step>();
        ArrayList<Step> steps = new ArrayList<>();
        fillSteps(stepsnodes, allSteps);
        return allSteps;

    }

    @SuppressLint("StaticFieldLeak")
    public static ArrayList<Coord> routeCoordFromApi(final int maxDistance, final String firstAddress, final String secondAddress) {

        final ArrayList<Step> allSteps = routeStepFromApi(maxDistance, firstAddress, secondAddress);

        ArrayList<Coord> coords = new ArrayList<>();
        try {
            coords = new AsyncTask<Object, Void, ArrayList<Coord>>() {

                @Override
                protected ArrayList<Coord> doInBackground(Object... objects) {

                    ArrayList<Coord> coords = new ArrayList<Coord>();
                    for (Step s : allSteps) {
                        coords.add(s.getCoord());
                    }
                    Coord.calculateConnectionCoords((int) maxDistance, coords);
                    return coords;
                }

            }.execute(maxDistance, firstAddress, secondAddress).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return coords;
    }

    private static void fillSteps(NodeList steps, ArrayList<Step> allSteps) {
        for (int i = 0; i < steps.getLength(); i++) {
            Node currentStep = steps.item(i);
            allSteps.add(new Step(
                    Integer.parseInt(getDistance(currentStep)),
                    Float.parseFloat(getLat(currentStep)),
                    Float.parseFloat(getLon(currentStep)),
                    Integer.parseInt(getDuration(currentStep))
            ));
        }
    }

    private static String getDistance(Node stepNode) {
        return stepNode.getChildNodes().item(13).getChildNodes().item(1).getTextContent();
    }

    private static String getLat(Node stepNode) {
        return stepNode.getChildNodes().item(3).getChildNodes().item(1).getTextContent();
    }

    private static String getLon(Node stepNode) {
        return stepNode.getChildNodes().item(3).getChildNodes().item(3).getTextContent();
    }

    private static String getDuration(Node stepNode) {
        return stepNode.getChildNodes().item(9).getChildNodes().item(1).getTextContent();
    }
}
