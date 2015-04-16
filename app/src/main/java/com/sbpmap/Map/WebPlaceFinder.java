package com.sbpmap.Map;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.sbpmap.EtovidelAPI.EtovidelAPI;

import com.sbpmap.MainActivity;
import com.sbpmap.Ostrovok.OstrovokAPI;
import com.sbpmap.Restoclub.RestoclubAPI;
import com.sbpmap.Utils.APIRequest;
import com.sbpmap.Utils.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebPlaceFinder {

    Map<String, ArrayList<PlaceFinder>> tasksList = new HashMap<>();
    Map<String, String> imgMarkers = new HashMap<>();


    Context mContext;
    private AssetManager assetManager;

    public static final String HOTEL = "Hotel";
    public static final String HOSTEL = "Hostel";
    public static final String MINI_HOTEL = "Minihotel";
    public static final String LANDMARK = "Landmark";
    public static final String BRIDGE = "Bridge";
    public static final String PARK = "Park";
    public static final String MONUMENT = "Monument";
    public static final String RESTAURANT = "Restaurant";

    public static final String[] VENUES = {RESTAURANT, HOTEL, LANDMARK, HOSTEL, MINI_HOTEL, MONUMENT, BRIDGE, PARK};

    public WebPlaceFinder(Context context, AssetManager assetManager) {
        mContext = context;
        for (String query : VENUES) {
            tasksList.put(query, new ArrayList<PlaceFinder>());
        }

        for (String venue : VENUES) {
            imgMarkers.put(venue, venue.toLowerCase() + ".png");
        }

        this.assetManager = assetManager;
    }

    public void searchPlaces(double lat, double lng, ArrayList<Integer> seletedItems, LatLngBounds curLatLngBounds) {
        MainActivity.callWebView("javascript:mapZoom('" + lat +
                "','" + lng + "')");

        for (int selectedItem : seletedItems) {
            Log.d("Java log", "searchPlaces(): " + lat + " " + lng);
            execute(curLatLngBounds, lat, lng, WebPlaceFinder.VENUES[selectedItem], 1000);
        }
    }

    public void execute(LatLngBounds curLatLngBounds, double lat, double lng, String query, int radius) {
        API api;
        Log.d("Java log", "execute(): query - " + query);
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
        ProgressDialog pDialog;

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
            pDialog = new ProgressDialog(mContext);
            String msg = MainActivity.isEnglish ? "Searching ..." : "Поиск ...";
            pDialog.setMessage(msg);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<Place> venues = new ArrayList<>();
            if (response != null) {
                venues = api.parseResponse(response);
                addMarkersToMap(venues, query, api.getLat(), api.getLng());
            }
            pDialog.dismiss();
        }
    }

    private void addMarkersToMap(ArrayList<Place> venues, String query, double locLat, double locLng) {
        if (venues != null) {
            for (Place fv : venues) {
                MainActivity.callWebView("javascript:addMarker('" + fv.getLat() +
                        "','" + fv.getLng() +
                        "','" + locLat +
                        "','" + locLng +
                        "','" + fv.getId()  +
                        "','" + query  +
                        "','" + fv.getName()  +
                        "','" + imgMarkers.get(query) + "')");
            }
            MainActivity.callWebView("javascript:isFound('" + query + "')");
        }
    }

    public void removeAll() {
        Log.d("Java log", "removeAll()");
        MainActivity.callWebView("javascript:clearAll()");
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

