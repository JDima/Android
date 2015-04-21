package com.sbpmap.Map;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.sbpmap.EtovidelAPI.EtovidelAPI;

import com.sbpmap.MainActivity;
import com.sbpmap.Ostrovok.OstrovokAPI;
import com.sbpmap.R;
import com.sbpmap.Restoclub.RestoclubAPI;
import com.sbpmap.Utils.APIRequest;
import com.sbpmap.Utils.AlertDialogManager;
import com.sbpmap.Utils.LatLngBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WebPlaceFinder {

    private Map<String, ArrayList<PlaceFinder>> tasksList = new HashMap<>();
    private Map<String, String> imgMarkers = new HashMap<>();
    private WebView myWebView;
    private static ProgressDialog pDialog;
    private Context mContext;
    private AssetManager assetManager;
    private static Map<String, Integer> requestList = new HashMap<>();
    private static int requestCount;
    private static AlertDialog alertDialog;

    public static final String HOTEL = "Hotel";
    public static final String HOSTEL = "Hostel";
    public static final String MINI_HOTEL = "Minihotel";
    public static final String LANDMARK = "Landmark";
    public static final String BRIDGE = "Bridge";
    public static final String PARK = "Park";
    public static final String MONUMENT = "Monument";
    public static final String RESTAURANT = "Restaurant";
    public static final String[] VENUES = {RESTAURANT, HOTEL, LANDMARK, HOSTEL, MINI_HOTEL, MONUMENT, BRIDGE, PARK};

    static public class WebPlaceFinderJS {
        @JavascriptInterface
        public void isEnded(String query, int count) {
            Log.d("Java log", "Good: " + query + " Added: " + count);
            addIsFinished(query, count);
        }
    }

    public WebPlaceFinder(Context context, WebView myWebView, AssetManager assetManager) {
        this.myWebView = myWebView;
        mContext = context;
        for (String query : VENUES) {
            tasksList.put(query, new ArrayList<PlaceFinder>());
        }

        for (String venue : VENUES) {
            imgMarkers.put(venue, venue.toLowerCase() + ".png");
        }

        this.assetManager = assetManager;
    }

    synchronized protected static void addIsFinished(String query, int count) {
        requestList.put(query, count);
        pDialog.incrementProgressBy(1);
        Log.d("Java log", "addIsFinished: " + requestList.size() + " requestcount " + requestCount);
        if (requestList.size() == requestCount) {
            Log.d("Java log", "addIsFinished: All requests!");
            pDialog.dismiss();

            alertDialog.setTitle(MainActivity.isEnglish ? "Search results" : "Результаты поиска");

            String nothing = MainActivity.isEnglish ? ": Nothing found!" : ": Ничего не найдено!";
            String error = MainActivity.isEnglish ? ": Request timeout!" : ": Исчерпано время запроса!";
            StringBuilder sb = new StringBuilder();
            for (String key : requestList.keySet()) {
                String queryMsg = MainActivity.isEnglish ? key : AlertDialogManager.RU_VENUES[Arrays.asList(WebPlaceFinder.VENUES).indexOf(key)];
                switch (requestList.get(key)) {
                    case 0:
                        sb.append(queryMsg +  nothing + "\n");
                        break;
                    case -1:
                        sb.append(queryMsg +  error + "\n");
                        break;
                }
            }

            requestList.clear();
            Log.d("Java log", "addIsFinished " + sb.toString());
            if (!sb.toString().isEmpty()) {
                Log.d("Java log", sb.toString());
                alertDialog.setMessage(sb.toString().substring(0, sb.toString().length() - 2));
                alertDialog.show();
            }
        }
    }

    public void loadUrl(final String url) {
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.callWebView(url);
            }
        });
    }

    void initProgressDialog() {
        pDialog = new ProgressDialog(mContext, android.R.attr.progressBarStyleHorizontal);

        String msg = MainActivity.isEnglish ? "Searching ..." : "Поиск ...";
        pDialog.setMessage(msg);
        pDialog.setProgressStyle(pDialog.STYLE_HORIZONTAL);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.setProgress(0);
        pDialog.setMax(2 * requestCount);
        pDialog.show();
    }



    public void searchPlaces(double lat, double lng, ArrayList<Integer> seletedItems, LatLngBounds curLatLngBounds) {
        requestCount = seletedItems.size();
        initProgressDialog();
        alertDialog = AlertDialogManager.alertDialog(mContext, "", "", R.drawable.find);
        loadUrl("javascript:mapZoom('" + lat + "','" + lng + "')");

        //SystemClock.sleep(2000);
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
          api = new OstrovokAPI(assetManager, curLatLngBounds, lat, lng);
        }
        else {
            api = new EtovidelAPI(assetManager, curLatLngBounds, lat, lng);
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
            Log.d("Java log", "onPostExecute(): query - " + query);
            if (response != null) {
                venues = api.parseResponse(response);
                addMarkersToMap(venues, query, api.getLat(), api.getLng());
            }
        }
    }

    private void addMarkersToMap(ArrayList<Place> venues, String query, double locLat, double locLng) {
        pDialog.incrementProgressBy(1);
        if (venues != null) {
            for (Place fv : venues) {
                Log.d("Java log", "addMarkersToMap(): name - " + fv.getName() + " query " + query);
                loadUrl("javascript:createInfoMarker('" + fv.getLat() +
                        "','" + fv.getLng() +
                        "','" + fv.getId() +
                        "','" + query +
                        "','" + fv.getName() +
                        "','" + fv.getAlpha() +
                        "','" + imgMarkers.get(query) + "')");
            }
            loadUrl("javascript:addMarkers('" + query +"')");
        }
    }

    public void removeAll() {
        Log.d("Java log", "removeAll()");
        loadUrl("javascript:clearAll()");
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

