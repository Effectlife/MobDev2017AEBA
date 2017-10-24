package com.example.android.routetesting.decoders;


import android.util.Log;

import com.example.android.routetesting.lookups.ApiUrl;

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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Effectlife on 1/10/2017.
 */
public abstract class ApiDocumentBuilder {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder dBuilder;


    public static Document decode(int urlType, Object firstArg, Object secondArg) {
        Log.d("APIDOCBUILDER", "Starting callApi");
        Document temp = callApi(urlFormat(urlType, firstArg, secondArg));
        Log.d("APIDOCBUILDER","Finished callApi");
        return temp;
    }

    public static Document decode(int urlType, Object firstArg) {
        return callApi(urlFormat(urlType, firstArg, null));
    }


    private static void trustAllHosts() {

        X509TrustManager easyTrustManager = new X509TrustManager() {

            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{easyTrustManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Document callApi(String url) {
        Log.d("APIDOCBUILDER","callApi: url: "+ url);


        try {
            dBuilder = dbFactory.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            System.out.println("DocumentBuilder initialisation failed: " + e.getStackTrace());
        }

        try {
            URL link = new URL(url);
            URLConnection connection = link.openConnection();
            InputStream source = connection.getInputStream();
            Document parsed = dBuilder.parse(source);
            //Log.e("PRINTED_XML", getStringFromDocument(parsed));
            return parsed;


        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String urlFormat(int urlType, Object firstArg, Object secondArg) {
        try {
            switch (urlType) {
                case ApiUrl.YAHOOWEATHER:
                    return null; //TODO: Not Yet Implemented

                case ApiUrl.GOOGLELOC:
                    return "https://maps.googleapis.com/maps/api/geocode/xml?address=" + firstArg + "&key=" + "AIzaSyDEGUJCH82cBmRRdaWIbp8_BbjZ7noQ23k";

                case ApiUrl.GOOGLEDIR:
                    return "https://maps.googleapis.com/maps/api/directions/xml?origin=" + firstArg + "&destination=" + secondArg + "&key=" + "AIzaSyDt3_qXntcjfW6lxv0uv_gQlXKOZxX03ek";

                case ApiUrl.METNO:
                    return "https://api.met.no/weatherapi/locationforecast/1.9/?lat=" + firstArg + "&lon=" + secondArg;


                case ApiUrl.MAPQUEST:
                    return "https://www.mapquestapi.com/geocoding/v1/reverse?key=JYFF5aW2u906EZwyCpfS2QAiqgg6AWUk&location="+firstArg+"%2C"+secondArg+"&outFormat=xml&thumbMaps=false";


                default:
                    System.out.println("wrong urlType entered");


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}
