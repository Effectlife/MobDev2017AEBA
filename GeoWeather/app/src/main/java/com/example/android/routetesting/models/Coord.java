package com.example.android.routetesting.models;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.routetesting.decoders.ApiDocumentBuilder;
import com.example.android.routetesting.lookups.ApiUrl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Effectlife on 2/10/2017.
 */
public class Coord {
    private float lat, lon;

    public Coord(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Coord(double latitude, double longitude) {
        this((float)latitude, (float)longitude);
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public static void calculateConnectionCoords(int maxdistance, ArrayList<Coord> coords) {
        boolean longerFound = true;
        boolean failure = false;
        int loopcounter = 0;
        while (longerFound && !failure) {
            loopcounter++;
            longerFound = false;
            for (int i = 0; i < coords.size() - 1; i++) {
                if (i > 200) {
                    System.out.println("Something went wrong... breaking out of while");
                    failure = true;
                    i = Integer.MAX_VALUE;
                    break;
                }
                if (Helper.distance(coords.get(i), coords.get(i + 1)) > maxdistance) {
                    longerFound = true;
                    coords.add(i + 1, Helper.avgDst(coords.get(i), coords.get(i + 1)));
                    //i++;
                }

            }
        }
    }

    public String toShortString() {
        float lat = getLat();
        float lon = getLon();


        lat = ((float) ((int) (lat * 100))) / 100;
        lon = ((float) ((int) (lon * 100))) / 100;
        return "(" + lat + "," + lon + ")";
    }


    public String getCityName() {
        //return "API-OK,DISABLED";

        Document apiResult = null;

        try {
            apiResult = new AsyncTask<Object, Void, Document>() {
                @Override
                protected Document doInBackground(Object... objects) {
                    return ApiDocumentBuilder.decode(ApiUrl.MAPQUEST, getLat(), getLon());
                }
            }.execute(getLat(), getLon()).get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Log.d("MAPQUEST", Helper.getStringFromDocument(apiResult));

        NodeList locations = apiResult.getElementsByTagName("location");
        Node location = locations.item(0);
        //Log.e("DECODECITY", "Locationstag: "+location.getNodeName());
        NodeList children = location.getChildNodes();
        Node hopeType = null;
        Node node = null;
        for (int i = 0; i < children.getLength(); i++ ){
            node = children.item(i);

            try {
                hopeType = node.getAttributes().getNamedItem("type");
                //Log.e("NODETYPE", hopeType.getNodeValue());
            }catch (Exception e){
                continue;
            }

            if(node != null && hopeType != null && hopeType.getNodeValue().equals("City")){
                //Log.d("DECODECITY", node.getTextContent()+" ");
                return node.getTextContent();
            }


        }


        return "Unknown";

    }

    @Override
    public String toString() {
        return "(" + lat + "," + lon + ")";
    }
}
