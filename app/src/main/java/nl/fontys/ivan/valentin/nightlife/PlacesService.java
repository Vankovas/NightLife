package nl.fontys.ivan.valentin.nightlife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.OpeningHours;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.util.ArrayList;


class Place implements Serializable {
    public String place_id;
    public String name;


}

class PlaceDetail implements Serializable {

    public Double rating;
    public String placeId;
    public String name;
    public LocalTime openTime;
    public LocalTime closeTime;
    public boolean alwaysOpen;
    public ArrayList<String> photoReference;
    public String phoneNumber;

    public PlaceDetail() {
        photoReference = new ArrayList<>();
    }
}

public class PlacesService implements Serializable {

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String PHOTOS = "/photo?maxwidth=400";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json?";

    // KEY!
    private static final String API_KEY = "AIzaSyDSbsYlKXUjSUCN1G6bfDM3npr1rcokkx0";

    public static String searchTest(double lat, double lng, int radius, String type) {

        StringBuilder sb = new StringBuilder(PLACES_API_BASE);
        sb.append(TYPE_SEARCH);
        sb.append(OUT_JSON);

        sb.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
        sb.append("&radius=" + String.valueOf(radius));
        sb.append("&type=" + String.valueOf(type));

        //sb.append("?sensor=false");
        //sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
        sb.append("&key=" + API_KEY);
        return sb.toString();

    }

    public static ArrayList<Place> search(double lat, double lng, int radius, String type) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);

            sb.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
            sb.append("&type=" + String.valueOf(type));

            //sb.append("?sensor=false");
            //sb.append("&keyword=" + URLEncoder.encode(keyword, "utf8"));
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.place_id = predsJsonArray.getJSONObject(i).getString("place_id");
                place.name = predsJsonArray.getJSONObject(i).getString("name");
                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return resultList;
    }


    public static PlaceDetail getDetails(String placeId) {


        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_DETAILS);
            sb.append(OUT_JSON);
            sb.append("placeid=" + placeId);
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        PlaceDetail placeDetail = null;
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject test = jsonObj.getJSONObject("result");


            placeDetail = new PlaceDetail();

            placeDetail.placeId = test.getString("place_id");
            placeDetail.rating = test.getDouble("rating");
            placeDetail.name = test.getString("name");
            placeDetail.phoneNumber = test.getString("formatted_phone_number");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return placeDetail;
    }

    public static ArrayList<Bitmap> getPhoto(ArrayList<String> arrayPhotoRef) {

        HttpURLConnection conn = null;
        ArrayList<Bitmap> bitMapTemp = new ArrayList<>();

        for (int i = 0; i < arrayPhotoRef.size(); i++) {

            Bitmap photo = null;

            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE);
                sb.append(PHOTOS);
                // sb.append(OUT_JSON);
                sb.append("&photoreference=" + arrayPhotoRef.get(i));
                sb.append("&key=" + API_KEY);

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                photo = BitmapFactory.decodeStream(conn.getInputStream());

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error processing Places API URL", e);
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to Places API", e);
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            bitMapTemp.add(photo);
        }
        return bitMapTemp;
    }
}