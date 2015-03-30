package com.sbpmap.Ostrovok;

import android.app.Activity;
import android.content.res.AssetManager;
import android.widget.TextView;

import com.sbpmap.Map.API;
import com.sbpmap.Map.Place;
import com.sbpmap.R;
import com.sbpmap.SinglePlaceActivity;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by JDima on 29/03/15.
 */
public class OstrovokAPI implements API {
    private double lat;
    private double lng;
    private String query;
    private String id;
    private static final String OSTROVOK_DB = "OstrovokDB/ostrovok.json";
    private AssetManager assetManager;

    public OstrovokAPI(AssetManager assetManager) {
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
        return OstrovokParser.parseResponse(response, query);
    }

    @Override
    public void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response) {
        OstrovokInfoPlace oip = OstrovokParser.parseSinglePlaceResponse(response, id);
        if (oip != null) {
            TextView lbl_name = (TextView) singlePlaceActivity.findViewById(R.id.hotel_name);
            TextView lbl_address = (TextView) singlePlaceActivity.findViewById(R.id.hotel_address);
            TextView lbl_definition = (TextView) singlePlaceActivity.findViewById(R.id.hotel_definition);
            TextView lbl_price = (TextView) singlePlaceActivity.findViewById(R.id.hotel_price);


            lbl_name.setText(oip.getName());
            lbl_address.setText(oip.getAddress());
            lbl_definition.setText(oip.getDefinition());
            String price = oip.getCost() + " RUB";
            lbl_price.setText(price);
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


}
