package com.sbpmap.EtovidelAPI;

import android.content.res.AssetManager;
import android.text.Html;
import android.widget.TextView;

import com.sbpmap.Map.API;
import com.sbpmap.Map.Place;
import com.sbpmap.Map.WebPlaceFinder;
import com.sbpmap.Ostrovok.OstrovokAPI;
import com.sbpmap.R;
import com.sbpmap.SinglePlaceActivity;
import com.sbpmap.Utils.HttpRequest;
import com.sbpmap.Utils.LatLngBounds;
import com.sbpmap.Utils.TextViewUtil;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by JDima on 29/03/15.
 */
public class EtovidelAPI implements API {
    private double lat;
    private double lng;
    private String query;
    private LatLngBounds latLngBounds;
    private static final String ETOVIDEL_DB = "EtovidelDB/etovidel.json";
    private static final String ETOVIDEL = "http://www.etovidel.net/sights/city/saint-petersburg/id/";
    private AssetManager assetManager;

    public EtovidelAPI(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public EtovidelAPI(AssetManager assetManager, LatLngBounds latLngBounds, double lat, double lng) {
        this.latLngBounds = latLngBounds;
        this.lat = lat;
        this.lng = lng;
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
        return EtovidelParser.parseResponse(response, query, lat, lng, latLngBounds);
    }

    @Override
    public void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response) {
        EtovidelInfoPlace eip = EtovidelParser.parseSinglePlaceResponse(response);
        if (eip != null) {
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.landmark_name), Html.fromHtml(eip.getName()));
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.landmark_address), Html.fromHtml(eip.getAddress()));
            TextViewUtil.setTextViewText((TextView) singlePlaceActivity.findViewById(R.id.landmark_definition), Html.fromHtml(eip.getDefinition()));
        }
    }

    @Override
    public String getSinglePlace(String query) {
        for (String venue : WebPlaceFinder.VENUES) {
            if (query.contains(venue)) {
                query = query.substring(venue.length());
            }
        }
        return HttpRequest.gerSourcePage(ETOVIDEL + query);
    }

    @Override
    public String getResponse(HttpUriRequest httpUriRequest) {
        byte[] buffer = null;
        InputStream is;
        try {
            is = assetManager.open(ETOVIDEL_DB);
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
