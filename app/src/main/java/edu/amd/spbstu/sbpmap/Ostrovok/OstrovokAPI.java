package edu.amd.spbstu.sbpmap.Ostrovok;

import android.content.res.AssetManager;
import android.widget.TextView;

import edu.amd.spbstu.sbpmap.Map.API;
import edu.amd.spbstu.sbpmap.Map.Place;
import edu.amd.spbstu.sbpmap.SinglePlaceActivity;
import edu.amd.spbstu.sbpmap.Utils.LatLngBounds;
import edu.amd.spbstu.sbpmap.Utils.TextViewUtil;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
            put("has_fitness", edu.amd.spbstu.sbpmap.R.string.has_fitness);
            put("has_meal", edu.amd.spbstu.sbpmap.R.string.has_meal);
            put("has_internet", edu.amd.spbstu.sbpmap.R.string.has_internet);
            put("has_airport_transfer", edu.amd.spbstu.sbpmap.R.string.has_airport_transfer);
            put("has_breakfast", edu.amd.spbstu.sbpmap.R.string.has_breakfast);
            put("has_spa", edu.amd.spbstu.sbpmap.R.string.has_spa);
            put("has_parking", edu.amd.spbstu.sbpmap.R.string.has_parking);
            put("has_pool", edu.amd.spbstu.sbpmap.R.string.has_pool);
            put("has_bathroom", edu.amd.spbstu.sbpmap.R.string.has_bathroom);

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
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(edu.amd.spbstu.sbpmap.R.id.hotel_name), oip.getName());
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(edu.amd.spbstu.sbpmap.R.id.hotel_address), oip.getAddress());
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(edu.amd.spbstu.sbpmap.R.id.hotel_definition), oip.getDefinition());
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(edu.amd.spbstu.sbpmap.R.id.hotel_price), oip.getCost());


            StringBuilder sb = new StringBuilder();
            for (String filter : oip.getAdds()) {
                sb.append("- ").append(singlePlaceActivity.getString(ADDS.get(filter))).append("\n");
            }
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(edu.amd.spbstu.sbpmap.R.id.price_include), sb.toString());
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

        if (buffer != null) {
            return new String(buffer);
        }
        return null;
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
