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


/**
 * This class contains most functions that have something to do with routes and geolocation
 */

public abstract class RouteDecoder {

    /**
     * Returns the Coord from  the given location, if the given location doesn't exist, it returns a Coord(1000,1000)
     *
     * @param location
     * @return
     */
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

    /**
     * Returns an ArrayList containing all steps to get from firstAddress to secondAddress (using the Google Directions API)
     * maxDistance isnt used and is a remnant from a previous idea we had (see Coord)
     *
     * @param maxDistance
     * @param firstAddress
     * @param secondAddress
     * @return
     */
    public static ArrayList<Step> routeStepFromApi(final int maxDistance, final String firstAddress, final String secondAddress) {

        Document directions = ApiDocumentBuilder.decode(ApiUrl.GOOGLEDIR, (String) firstAddress, (String) secondAddress);
        NodeList stepsnodes = directions.getElementsByTagName("step");
        ArrayList<Step> allSteps = new ArrayList<Step>();
        ArrayList<Step> steps = new ArrayList<>();
        fillSteps(stepsnodes, allSteps);
        return allSteps;

    }

    /**
     * This function is not used anymore,
     * It was designed to return an ArrayList of Coord, using the maxDistance parameter
     *
     * @param maxDistance
     * @param firstAddress
     * @param secondAddress
     * @return
     */
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

    /**
     * This function fills a given ArrayList<Step> with the information from the NodeList
     *
     * @param steps
     * @param allSteps
     */
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

    /**
     * Helper function to get the distance from a stepnode (XML is difficult...)
     *
     * @param stepNode
     * @return
     */
    private static String getDistance(Node stepNode) {
        return stepNode.getChildNodes().item(13).getChildNodes().item(1).getTextContent();
    }

    /**
     * Helper function to get the latitude from a stepnode (XML is difficult...)
     *
     * @param stepNode
     * @return
     */
    private static String getLat(Node stepNode) {
        return stepNode.getChildNodes().item(3).getChildNodes().item(1).getTextContent();
    }

    /**
     * Helper function to get the longitude from a stepnode (XML is difficult...)
     *
     * @param stepNode
     * @return
     */
    private static String getLon(Node stepNode) {
        return stepNode.getChildNodes().item(3).getChildNodes().item(3).getTextContent();
    }

    /**
     * Helper function to get the duration from a stepnode (XML is difficult...)
     *
     * @param stepNode
     * @return
     */
    private static String getDuration(Node stepNode) {
        return stepNode.getChildNodes().item(9).getChildNodes().item(1).getTextContent();
    }
}
