package com.sbpmap.Foursquare;

import android.widget.TextView;

import com.sbpmap.Map.API;
import com.sbpmap.Map.Place;
import com.sbpmap.R;
import com.sbpmap.SinglePlaceActivity;
import com.sbpmap.Utils.HttpRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;

/**
 * Created by JDima on 22/03/15.
 */
public class FoursquareAPI implements API {

    private final static String CLIENT_ID = "5WKHRU5SHZF0HMGPW1CHWW1FVAMYJH5X1UC1LI0CWZ2NSVP1";
    private final static String CLIENT_SECRET = "HIU1W3V2KIDZE2Y2JW3MML4BDLVHHMJAXDBGO5QWQVRLY5QB";

    @Override
    public HttpUriRequest getPlacesRequest(String query, int radius, double lat, double lng) {
        String url = "https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID
                + "&query=" + query + "&radius=" + String.valueOf(radius)
                + "&limit=700&client_secret=" + CLIENT_SECRET
                + "&v=20150215%20&ll=" + String.valueOf(lat) + "," + String.valueOf(lng);
        StringBuffer buffer_string = new StringBuffer(url);
        return new HttpGet(buffer_string.toString());
    }

    @Override
    public HttpUriRequest getSinglePlaceRequest(String id) {
        String url = "https://api.foursquare.com/v2/venues/" + id + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&v=20150309";
        StringBuffer buffer_string = new StringBuffer(url);
        return new HttpGet(buffer_string.toString());
    }

    @Override
    public ArrayList<Place> parseResponse(String response) {
        return FoursquareParser.parseResponse(response);
    }

    @Override
    public void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response) {
        FoursquareInfoPlace fv =  FoursquareParser.parseSinglePlaceResponse(response);
        if (fv != null) {
            TextView lbl_name = (TextView) singlePlaceActivity.findViewById(R.id.name);
            TextView lbl_location = (TextView) singlePlaceActivity.findViewById(R.id.location);
            TextView lbl_address = (TextView) singlePlaceActivity.findViewById(R.id.address);
            TextView lbl_phone = (TextView) singlePlaceActivity.findViewById(R.id.phone);
            TextView lbl_hours = (TextView) singlePlaceActivity.findViewById(R.id.hours);


            lbl_name.setText(fv.getName() != null ? fv.getName() : "Not present");
            lbl_address.setText(fv.getAddress() != null ? fv.getAddress() : "Not present");
            lbl_phone.setText(fv.getPhone() != null ? fv.getPhone() : "Not present");
            lbl_hours.setText(fv.getHours() != null ? fv.getHours() : "Not present");
            //lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + fv.getLat() + ", <b>Longitude:</b> " + fv.getLng()));
        }
    }

    @Override
    public String getSinglePlace(String venueId) {
        return HttpRequest.SEND(getSinglePlaceRequest(venueId));
    }
}