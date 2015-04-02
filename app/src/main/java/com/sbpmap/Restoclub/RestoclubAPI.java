package com.sbpmap.Restoclub;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.sbpmap.Map.API;
import com.sbpmap.Map.Place;
import com.sbpmap.R;
import com.sbpmap.SinglePlaceActivity;
import com.sbpmap.Utils.HttpRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JDima on 22/03/15.
 */
public class RestoclubAPI implements API {
    private double lat;
    private double lng;
    private static final String RESTOCLUB = "http://www.restoclub.ru/site/all/main/";
    private LatLngBounds latLngBounds;


    public RestoclubAPI(LatLngBounds latLngBounds, double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.latLngBounds = latLngBounds;
    }

    public RestoclubAPI(LatLngBounds latLngBounds) {
        this.lat = 0;
        this.lng = 0;
        this.latLngBounds = latLngBounds;
    }

    @Override
    public HttpUriRequest getPlacesRequest(String query, int radius, double lat, double lng) {
        HttpPost httppost = new HttpPost("http://www.restoclub.ru/ajax/nmap/get_markers/");

        try {
            List<NameValuePair> params = new ArrayList<>(1);
            params.add(new BasicNameValuePair("xl", String.valueOf(latLngBounds.southwest.longitude)));
            params.add(new BasicNameValuePair("xr", String.valueOf(latLngBounds.northeast.longitude)));
            params.add(new BasicNameValuePair("yl", String.valueOf(latLngBounds.northeast.latitude)));
            params.add(new BasicNameValuePair("yr", String.valueOf(latLngBounds.southwest.latitude)));
            params.add(new BasicNameValuePair("cur_user", "0"));
            httppost.setEntity(new UrlEncodedFormEntity(params));
        } catch (Exception e) {
            e.printStackTrace();

        }
        return httppost;
    }

    @Override
    public HttpUriRequest getSinglePlaceRequest(String venueId) {
        return null;
    }

    @Override
    public ArrayList<Place> parseResponse(String response) {
        return RestoclubParser.parseResponse(response);
    }

    @Override
    public void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response) {
        Map<String, String> rip = RestoclubParser.parseSinglePlaceResponse(response);
        if (rip != null) {
            TextView lbl_name = (TextView) singlePlaceActivity.findViewById(R.id.res_name);
            TextView lbl_address = (TextView) singlePlaceActivity.findViewById(R.id.res_address);
            TextView lbl_phone = (TextView) singlePlaceActivity.findViewById(R.id.res_phone);
            TextView lbl_hours = (TextView) singlePlaceActivity.findViewById(R.id.res_hours);
            TextView lbl_bill = (TextView) singlePlaceActivity.findViewById(R.id.res_bill);


            lbl_name.setText(rip.get(RestoclubParser.names[0]));
            lbl_phone.setText(rip.get(RestoclubParser.names[1]));
            lbl_address.setText(rip.get(RestoclubParser.names[2]));
            lbl_bill.setText(rip.get(RestoclubParser.names[3]));
            lbl_hours.setText(rip.get(RestoclubParser.names[4]));
        }
    }

    @Override
    public String getSinglePlace(String query) {
        return HttpRequest.gerSourcePage(RESTOCLUB + query);
    }

    @Override
    public String getResponse(HttpUriRequest httpUriRequest) {
        return HttpRequest.SEND(httpUriRequest);
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