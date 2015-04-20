package com.sbpmap.Ostrovok;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.Html;
import android.widget.TextView;

import com.sbpmap.MainActivity;
import com.sbpmap.Map.API;
import com.sbpmap.Map.Place;
import com.sbpmap.R;
import com.sbpmap.SinglePlaceActivity;
import com.sbpmap.Utils.LatLngBounds;
import com.sbpmap.Utils.TextViewUtil;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by JDima on 29/03/15.
 */
public class OstrovokAPI implements API {
    private double lat;
    private double lng;
    private LatLngBounds latLngBounds;
    private String query;
    private String id;
    private static final String OSTROVOK_DB = "OstrovokDB/ostrovok.json";
    private AssetManager assetManager;

    //public static final String[] ADDS = {"has_fitness", "has_meal", "has_internet",
    //                                     "has_airport_transfer", "has_breakfast", "has_spa",
     //                                    "has_parking", "has_pool", "has_bathroom"};

    private final static Map<String, Integer> ADDS = new LinkedHashMap<String, Integer>()
    {
        {
            put("has_fitness", R.string.has_fitness);
            put("has_meal", R.string.has_meal);
            put("has_internet",R.string.has_internet);
            put("has_airport_transfer",R.string.has_airport_transfer);
            put("has_breakfast",R.string.has_breakfast);
            put("has_spa",R.string.has_spa);
            put("has_parking",R.string.has_parking);
            put("has_pool",R.string.has_pool);
            put("has_bathroom",R.string.has_bathroom);

        }
    };

    public OstrovokAPI(AssetManager assetManager, LatLngBounds latLngBounds, double lat, double lng) {
        this.latLngBounds = latLngBounds;
        this.lat = lat;
        this.lng = lng;
        this.assetManager = assetManager;
    }

    public OstrovokAPI(AssetManager assetManager) {
        this.lng = 0;
        this.lat = 0;
        this.assetManager = assetManager;
    }

    @Override
    public HttpUriRequest getPlacesRequest(String query, int radius, double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.query = query;

        return null;
    }

    @Override
    public HttpUriRequest getSinglePlaceRequest(String query) {
        return null;
    }

    @Override
    public ArrayList<Place> parseResponse(String response) {
        return OstrovokParser.parseResponse(response, query, lat, lng, latLngBounds);
    }

    @Override
    public void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response) {
        OstrovokInfoPlace oip = OstrovokParser.parseSinglePlaceResponse(response, id);
        if (oip != null) {
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.hotel_name), oip.getName());
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.hotel_address), oip.getAddress());
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.hotel_definition), oip.getDefinition());
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.hotel_price), oip.getCost());


            StringBuilder sb = new StringBuilder();
            for (String filter : oip.getAdds()) {
                sb.append("- " + singlePlaceActivity.getString(ADDS.get(filter)) + "\n");
            }
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.price_include), sb.toString());
        }
    }

    @Override
    public String getSinglePlace(String id) {
        this.id = id;
        return getResponse(null);
    }

    @Override
    public String getResponse(HttpUriRequest httpUriRequest) {
        byte[] buffer = null;
        InputStream is;
        try {
            is = assetManager.open(OSTROVOK_DB);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = new String(buffer);
        return response;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLng() {
        return lng;
    }
}
