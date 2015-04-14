package com.sbpmap.Map;



import android.content.Context;
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
import com.sbpmap.Utils.AlertDialogManager;
import com.sbpmap.Utils.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebPlaceFinder {

    Map<String, ArrayList<PlaceFinder>> tasksList = new HashMap<>();
    Map<String, String> imgMarkers = new HashMap<>();


    Context mContext;
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

    public WebPlaceFinder(Context context, WebView webView, AssetManager assetManager) {
        mContext = context;
        for (String query : VENUES) {
            tasksList.put(query, new ArrayList<PlaceFinder>());
        }

        imgMarkers.put(HOSTEL, "hostel.png");
        imgMarkers.put(HOTEL, "hotel.png");
        imgMarkers.put(RESTAURANT, "restaurant.png");
        imgMarkers.put(LANDMARK, "museum.png");
        imgMarkers.put(MONUMENT, "monument.png");
        imgMarkers.put(BRIDGE, "bridge.png");
        imgMarkers.put(PARK, "park.png");
        imgMarkers.put(MINI_HOTEL, "minihotel.png");

        this.assetManager = assetManager;
        this.webView = webView;
    }

    public boolean searchPlaces(double lat, double lng, SubMenu subMenu, LatLngBounds curLatLngBounds) {
        boolean oneIsChecked = false;
        for (int id = 0; id < WebPlaceFinder.VENUES.length; id++) {
            if (subMenu.getItem(id).isChecked()) {
                Log.d("Java log", "LAt: " + lat);
                execute(curLatLngBounds, lat, lng, WebPlaceFinder.VENUES[id], 1000);
                oneIsChecked = true;
            }
        }
        return oneIsChecked;
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
            response = api.getResponse(params[0].httpUriRequest);
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
                addMarkersToMap(venues, query, api.getLat(), api.getLng());
            }
        }
    }

    private void addMarkersToMap(ArrayList<Place> venues, String query, double locLat, double locLng) {
        if (venues != null) {
            for (Place fv : venues) {
                webView.loadUrl("javascript:addMarker('" + fv.getLat() +
                                                   "','" + fv.getLng() +
                                                   "','" + locLat +
                                                   "','" + locLng +
                                                   "','" + fv.getId()  +
                                                   "','" + query  +
                                                   "','" + imgMarkers.get(query) + "')");
            }
            webView.loadUrl("javascript:isFound('" + query + "')");
        }
    }

    public void remove(String query) {
        webView.loadUrl("javascript:clear('" + query + "')");
        for (PlaceFinder task : tasksList.get(query)) {
            task.cancel(true);
        }
    }

    public void removeAll() {
        webView.loadUrl("javascript:clearAll()");
        for (Map.Entry<String, ArrayList<PlaceFinder> >  entry : tasksList.entrySet()) {
            ArrayList<PlaceFinder> placeFinders = entry.getValue();
            if (placeFinders != null) {
                for (PlaceFinder placeFinder : placeFinders) {
                    placeFinder.cancel(true);
                }
            }
        }

    }
}

