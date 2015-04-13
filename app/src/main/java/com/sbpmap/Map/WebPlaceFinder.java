package com.sbpmap.Map;



import android.content.res.AssetManager;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SubMenu;
import android.webkit.WebView;
import android.widget.Toast;

import com.sbpmap.EtovidelAPI.EtovidelAPI;

import com.sbpmap.MainActivity;
import com.sbpmap.Ostrovok.OstrovokAPI;
import com.sbpmap.Restoclub.RestoclubAPI;
import com.sbpmap.Utils.APIRequest;
import com.sbpmap.R;
import com.sbpmap.Utils.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebPlaceFinder {

    public static class JavaScriptExtensions
    {
        public void doThis(String lat, String lng)
        {
            Log.d( "Cur pos:" + lat + " and " + lng, "HI!");
            //webView.loadUrl("javascript:loadData()");
        }
    }

    Map<String, ArrayList<Place>> venuesList = new HashMap<>();
    Map<String, ArrayList<PlaceFinder>> tasksList = new HashMap<>();
    Map<String, String> imgMarkers = new HashMap<>();

    private static WebView webView;
    private AssetManager assetManager;

    public static final String HOTEL = "Hotel";
    public static final String HOSTEL = "Hostel";
    public static final String MINI_HOTEL = "Mini-hotel";
    public static final String LANDMARK = "Landmark";
    public static final String BRIDGE = "Bridge";
    public static final String PARK = "Park";
    public static final String MONUMENT = "Monument";
    public static final String RESTAURANT = "Restaurant";

    public static final String[] VENUES = {RESTAURANT, HOTEL, LANDMARK, HOSTEL, MINI_HOTEL, MONUMENT, BRIDGE, PARK};

    public WebPlaceFinder(WebView webView, AssetManager assetManager) {
        for (String query : VENUES) {
            venuesList.put(query, new ArrayList<Place>());
            tasksList.put(query, new ArrayList<PlaceFinder>());
        }

        imgMarkers.put(HOSTEL, "hostel.png");
        imgMarkers.put(HOTEL, "hotel.png");
        imgMarkers.put(RESTAURANT, "restaurant.png");
        imgMarkers.put(LANDMARK, "landmark.png");
        imgMarkers.put(MONUMENT, "monument.png");
        imgMarkers.put(BRIDGE, "bridge.png");
        imgMarkers.put(PARK, "park.png");
        imgMarkers.put(MINI_HOTEL, "minihotel.png");

        this.assetManager = assetManager;
        this.webView = webView;
    }

    public boolean searchPlaces(double lat, double lng, SubMenu subMenu, LatLngBounds curLatLngBounds) {
        for (int id = 0; id < WebPlaceFinder.VENUES.length; id++) {
            if (subMenu.getItem(id).isChecked()) {
                execute(curLatLngBounds, lat, lng, WebPlaceFinder.VENUES[id], 1000);
            }
        }
        return true;
    }

    public void execute(LatLngBounds curLatLngBounds, double lat, double lng, String query, int radius) {
        API api;
        Log.d("Java log", "Query: " + query);
        if (query.equals(RESTAURANT)) {
            api = new RestoclubAPI(curLatLngBounds, lat, lng);
        } else if(query.equals(HOSTEL) || query.equals(HOTEL) || query.equals(MINI_HOTEL)) {
          api = new OstrovokAPI(assetManager, lat, lng);
        }
        else {
            api = new EtovidelAPI(assetManager, lat, lng);
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
                addMarkersToMap(venues, query, api.getLat(), api.getLng());
            }
        }
    }

    private void addMarkersToMap(ArrayList<Place> venues, String query, double locLat, double locLng) {
        float[] delta = new float[1];
        float[] maxDelta = new float[1];

        //LatLng latLng = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        //Location.distanceBetween(latLng.latitude, latLng.longitude, locLat, locLng, maxDelta);
        if (venues != null) {
            for (Place fv : venues) {
                webView.loadUrl("javascript:addMarker('" + fv.getLat() +
                                                   "','" + fv.getLng() +
                                                   "','" + locLat +
                                                   "','" + locLng +
                                                   "','" + fv.getId()  +
                                                   "','" + imgMarkers.get(query) + "')");
                /*MarkerOptions marker = new MarkerOptions().position(new LatLng(fv.getLat(), fv.getLng())).title(fv.getName());
                Location.distanceBetween(locLat, locLng, fv.getLat(), fv.getLng(), delta);

                marker.snippet(fv.getId());
                marker.icon(BitmapDescriptorFactory.fromResource(imgMarkers.get(query)));

                float alpha = delta[0] / maxDelta[0];
                if (alpha > 1) {
                    continue;
                }
                marker.alpha(1 - alpha);

                googleMap.addMarker(marker);*/

            }
        }
    }

    public void remove(String query) {
        webView.loadUrl("javascript:clear()");
        venuesList.get(query).clear();
        for (PlaceFinder task : tasksList.get(query)) {
            task.cancel(true);
        }
        for (Map.Entry<String, ArrayList<Place>> entry : venuesList.entrySet()) {
            addMarkersToMap(entry.getValue(), query, 0, 0);
        }
    }

    public void removeAll() {
        webView.loadUrl("javascript:clear()");
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

