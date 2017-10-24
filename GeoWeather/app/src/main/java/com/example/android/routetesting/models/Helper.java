package com.example.android.routetesting.models;


import com.example.android.routetesting.models.Coord;

import org.w3c.dom.Document;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Effectlife on 2/10/2017.
 */
public abstract class Helper {


    public static float avgDst(float dist1, float dist2) {
        return (dist1 + dist2) / 2;
    }

    /**
     * returns a coordinate exactly in between the two inputs
     *
     * @param coord1
     * @param coord2
     * @return coord
     */
    public static Coord avgDst(Coord coord1, Coord coord2) {
        return new Coord(
                (coord1.getLat() + coord2.getLat()) / 2,
                (coord1.getLon() + coord2.getLon()) / 2);
    }

    /**
     * returns the distance in meters between the two inputs
     *
     * @param coord1
     * @param coord2
     * @return
     */
    public static float distance(Coord coord1, Coord coord2) {
        return distance(coord1.getLat(), coord1.getLon(), coord2.getLat(), coord2.getLon());
    }

    /**
     * returns the distance in meters between 2 coords created by using
     * lat1 and lon1 for the first, and lat2 and lon2 for the second
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static float distance(float lat1, float lon1, float lat2, float lon2) {
        float p = 0.017453292519943295f;    // Math.PI / 180

        double a = 0.5 - Math.cos((lat2 - lat1) * p) / 2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p)) / 2;

        return (float) (12742000 * Math.asin(Math.sqrt(a))); // 2 * R; R = 6371 km
    }


    public static String getGivenDateInFormat(Calendar dt) {
        return String.format(Locale.GERMAN,
                "%04d-%02d-%02dT%02d:%02d:%02dZ",
                dt.get(Calendar.YEAR),
                dt.get(Calendar.MONTH) + 1, //for whatever reason, this returns 0-11 instead 1-12...
                dt.get(Calendar.DAY_OF_MONTH),
                dt.get(Calendar.HOUR_OF_DAY),
                dt.get(Calendar.MINUTE),
                dt.get(Calendar.SECOND));
    }


    public static String getCurrentDateInFormat() {
        return getGivenDateInFormat(Calendar.getInstance());
        

    }



    public static String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}
