package com.example.android.routetesting.decoders;


import android.util.Log;

import com.example.android.routetesting.lookups.ApiUrl;
import com.example.android.routetesting.models.Helper;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Effectlife on 1/10/2017.
 */
public abstract class ApiDocumentBuilder {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder dBuilder;


    public static Document decode(int urlType, Object firstArg, Object secondArg) {
        Document temp = callApi(urlFormat(urlType, firstArg, secondArg));
        return temp;
    }

    public static Document decode(int urlType, Object firstArg) {
        return callApi(urlFormat(urlType, firstArg, null));
    }

    public static Document callApi(String url) {
        Log.i("ApiDocumentBuilder", "callApi: url: " + url); //keep, prints all urls to the console

        try {
            dBuilder = dbFactory.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            Log.e("CallApi", "DocumentBuilder initialisation failed: " + e.getStackTrace()); //is error message
        }

        try {
            URL link = new URL(url);
            URLConnection connection = link.openConnection();
            InputStream source = connection.getInputStream();
            Document parsed = dBuilder.parse(source);
            //Log.e("PRINTED_XML", Helper.getStringFromDocument(parsed));
            return parsed;


        } catch (UnknownHostException e) {
            Log.e("ApiDocumentBuilder", "None found..."); //Error message
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String urlFormat(int urlType, Object firstArg, Object secondArg) {

        String t;
        try {
            switch (urlType) {
                case ApiUrl.GOOGLELOC:
                    t = "https://maps.googleapis.com/maps/api/geocode/xml?address=" + firstArg + "&key=" + "AIzaSyCOTpBb1p994BuJfFqEgX6M3vimH7uKbAU";
                    return t;
                case ApiUrl.GOOGLEDIR:
                    t = "https://maps.googleapis.com/maps/api/directions/xml?origin=" + firstArg + "&destination=" + secondArg + "&key=" + "AIzaSyDt3_qXntcjfW6lxv0uv_gQlXKOZxX03ek";
                    return t;
                case ApiUrl.METNO:
                    t = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=" + firstArg + "&lon=" + secondArg;
                    return t;
                case ApiUrl.MAPQUEST:
                    t = "https://www.mapquestapi.com/geocoding/v1/reverse?key=JYFF5aW2u906EZwyCpfS2QAiqgg6AWUk&location=" + firstArg + "%2C" + secondArg + "&outFormat=xml&thumbMaps=false";
                    return t;
                default:
                    Log.e("UrlFormatting", "wrong urlType entered"); //keep, is error message
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
