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
    private String id;
    private static final String ETOVIDEL_DB = "EtovidelDB/etovidel.json";
    private static final String ETOVIDEL = "http://www.etovidel.net/sights/city/saint-petersburg/id/";
    private AssetManager assetManager;

    public EtovidelAPI(AssetManager assetManager) {
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
        return EtovidelParser.parseResponse(response, query);
    }

    @Override
    public void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response) {
        EtovidelInfoPlace oip = EtovidelParser.parseSinglePlaceResponse(response);
        if (oip != null) {
            TextView lbl_name = (TextView) singlePlaceActivity.findViewById(R.id.landmark_name);
            TextView lbl_address = (TextView) singlePlaceActivity.findViewById(R.id.landmark_address);
            TextView lbl_definition = (TextView) singlePlaceActivity.findViewById(R.id.landmark_definition);

            lbl_name.setText(oip.getName());
            lbl_address.setText(oip.getAddress());
            lbl_definition.setText(Html.fromHtml(oip.getDefinition()));
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


}
