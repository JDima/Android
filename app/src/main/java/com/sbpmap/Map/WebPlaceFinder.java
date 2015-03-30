package com.sbpmap.Map;


import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.sbpmap.Foursquare.FoursquareAPI;
import com.sbpmap.MainActivity;
import com.sbpmap.Ostrovok.OstrovokAPI;
import com.sbpmap.Restoclub.RestoclubAPI;
import com.sbpmap.Utils.APIRequest;
import com.sbpmap.Utils.AlertDialogManager;
import com.sbpmap.Utils.HttpRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbpmap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebPlaceFinder {
    Map<String, ArrayList<Place>> venuesList = new HashMap<>();
    Map<String, ArrayList<PlaceFinder>> tasksList = new HashMap<>();
    Map<String, Integer> imgMarkers = new HashMap<>();

    private GoogleMap googleMap;
    private AssetManager assetManager;
    AlertDialogManager alert = new AlertDialogManager();

    public static final String HOTEL = "Hotel";
    public static final String HOSTEL = "Hostel";
    public static final String MINI_HOTEL = "Mini-hotel";
    public static final String MUSEUM = "Museum";
    public static final String RESTAURANT = "Restaurant";

    public static final String[] VENUES = {RESTAURANT, HOTEL, MUSEUM, HOSTEL, MINI_HOTEL};

    public WebPlaceFinder(GoogleMap googleMap, AssetManager assetManager) {
        for (String query : VENUES) {
            venuesList.put(query, new ArrayList<Place>());
            tasksList.put(query, new ArrayList<PlaceFinder>());
        }

        imgMarkers.put(HOSTEL, R.drawable.hostel);
        imgMarkers.put(HOTEL, R.drawable.hotel);
        imgMarkers.put(RESTAURANT, R.drawable.restaurant);
        imgMarkers.put(MUSEUM, R.drawable.museum);
        imgMarkers.put(MINI_HOTEL, R.drawable.minihotel);

        this.assetManager = assetManager;
        this.googleMap = googleMap;
    }

    public void execute(double lat, double lng, String query, int radius) {
        API api;
        if (query.equals(RESTAURANT)) {
            api = new RestoclubAPI(googleMap.getProjection().getVisibleRegion().latLngBounds);
        } else if(query.equals(HOSTEL) || query.equals(HOTEL) || query.equals(MINI_HOTEL)) {
          api = new OstrovokAPI(assetManager);
        }
        else {
            api = new FoursquareAPI();
        }

        new PlaceFinder().execute(new APIRequest(api, api.getPlacesRequest(query, radius, lat, lng), query));
    }

    private class PlaceFinder extends AsyncTask<APIRequest, Void, String> {
        String response;
        String query;
        API api;

        @Override
        protected String doInBackground(APIRequest... params) {
            api = params[0].api;
            query = params[0].query;
            tasksList.get(query).add(this);
            response = api.getResponse(params[0].httpUriRequest); //HttpRequest.SEND(params[0].httpUriRequest);
            return "";
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<Place> venues = new ArrayList<>();
            if (response != null) {
                venues = api.parseResponse(response);
                venuesList.get(query).addAll(venues);
                addMarkersToMap(venues, query);
            }
        }
    }

    private void addMarkersToMap(ArrayList<Place> venues, String query) {
        if (venues != null) {
            for (Place fv : venues) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(fv.getLat(), fv.getLng())).title(fv.getName());

                marker.snippet(fv.getId());
                marker.icon(BitmapDescriptorFactory.fromResource(imgMarkers.get(query)));

                googleMap.addMarker(marker);

            }
        }
    }

    public void remove(String query) {
        googleMap.clear();
        venuesList.get(query).clear();
        for (PlaceFinder task : tasksList.get(query)) {
            task.cancel(true);
        }
        for (Map.Entry<String, ArrayList<Place>> entry : venuesList.entrySet()) {
            addMarkersToMap(entry.getValue(), query);
        }
    }

    public void removeAll() {
        googleMap.clear();
        for (Map.Entry<String, ArrayList<PlaceFinder> >  entry : tasksList.entrySet()) {
            ArrayList<PlaceFinder> placeFinders = entry.getValue();
            if (placeFinders != null) {
                for (PlaceFinder placeFinder : placeFinders) {
                    placeFinder.cancel(true);
                }
            }
        }
        for (Map.Entry<String, ArrayList<Place>> entry : venuesList.entrySet()) {
            ArrayList<Place> places = entry.getValue();
            places.clear();
            if (places != null) {
                places.clear();
            }
        }
    }
}

