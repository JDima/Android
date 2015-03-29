package com.sbpmap.Map;


import android.os.AsyncTask;

import com.sbpmap.Foursquare.FoursquareAPI;
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
    AlertDialogManager alert = new AlertDialogManager();

    public static final String VENUE_ID = "venue_id";
    public static final String HOTEL = "hotel";
    public static final String HOSTEL = "hostel";
    public static final String MUSEUM = "museum";
    public static final String RESTAURANT = "restaurant";

    public static final String[] VENUES = {RESTAURANT, HOTEL, MUSEUM, HOSTEL};

    public WebPlaceFinder(GoogleMap googleMap) {
        String[] queries = {"hostel", "hotel", "museum", "restaurant"};
        for (String query : queries) {
            venuesList.put(query, new ArrayList<Place>());
            tasksList.put(query, new ArrayList<PlaceFinder>());
        }

        imgMarkers.put(HOSTEL, R.drawable.hostel);
        imgMarkers.put(HOTEL, R.drawable.hotel);
        imgMarkers.put(RESTAURANT, R.drawable.restaurant);
        imgMarkers.put(MUSEUM, R.drawable.museum);

        this.googleMap = googleMap;
    }

    public void execute(double lat, double lng, String query, int radius) {
        /*for (int i = -5; i <= 5; i++) {
			for (int j = -5; j <= 5; j++) {
				new PlaceFinder().execute(query, String.valueOf(radius), String.valueOf(lat + 0.01 * i), String.valueOf(lng + 0.01 * j));
			}
		}*/
        API api;
        if (query.equals(RESTAURANT)) {
            api = new RestoclubAPI();
        } else {
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
            response = HttpRequest.SEND(params[0].httpUriRequest);

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

