package com.sbpmap.Map;

import com.sbpmap.SinglePlaceActivity;

import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;

/**
 * Created by JDima on 22/03/15.
 */
public interface API {

    HttpUriRequest getPlacesRequest(String query, int radius, double lat, double lng);

    HttpUriRequest getSinglePlaceRequest(String id);

    ArrayList<Place> parseResponse(String response);

    void createSinglePage(SinglePlaceActivity singlePlaceActivity, String response);

    String getSinglePlace(String query);

    String getResponse(HttpUriRequest httpUriRequest);
}
