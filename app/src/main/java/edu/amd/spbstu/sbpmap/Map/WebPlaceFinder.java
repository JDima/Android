package edu.amd.spbstu.sbpmap.Map;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import edu.amd.spbstu.sbpmap.EtovidelAPI.EtovidelAPI;

import edu.amd.spbstu.sbpmap.MainActivity;
import edu.amd.spbstu.sbpmap.Ostrovok.OstrovokAPI;
import edu.amd.spbstu.sbpmap.R;
import edu.amd.spbstu.sbpmap.Restoclub.RestoclubAPI;
import edu.amd.spbstu.sbpmap.Utils.APIRequest;
import edu.amd.spbstu.sbpmap.Utils.AlertDialogManager;
import edu.amd.spbstu.sbpmap.Utils.LatLngBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private static AlertDialogManager alert = new AlertDialogManager();

    public static final String HOTEL = "Hotel";
    public static final String HOSTEL = "Hostel";
    public static final String MINI_HOTEL = "Minihotel";
    public static final String LANDMARK = "Landmark";
    public static final String BRIDGE = "Bridge";
    public static final String PARK = "Park";
    public static final String MONUMENT = "Monument";
    public static final String RESTAURANT = "Restaurant";
    public static final String[] VENUES = {RESTAURANT, HOTEL, LANDMARK, HOSTEL, MINI_HOTEL, MONUMENT, BRIDGE, PARK};

    public class WebPlaceFinderJS {
        @JavascriptInterface
        public void isMarkersAdded(String addedQuery) {
            Log.d("Java log", "isMarkersAdded: " + addedQuery);

            try {
                JSONArray streamer = new JSONArray(addedQuery);
                for (int i = 0; i < streamer.length(); i++) {
                    String query = streamer.getString(i);
                    requestList.put(query, requestList.get(query) + 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringBuilder sb = new StringBuilder();
            for (String key : requestList.keySet()) {
                String queryMsg = MainActivity.isEnglish ? key : AlertDialogManager.RU_VENUES[Arrays.asList(WebPlaceFinder.VENUES).indexOf(key)];
                if (requestList.get(key) != -1) {
                    sb.append(queryMsg).append(" - ").append(Integer.toString(requestList.get(key))).append(MainActivity.isEnglish ? " objects" : " объектов").append("\n");
                } else {
                    sb.append("Request timeout!\n");
                }
            }

            requestList.clear();
            Log.d("Java log", "addIsFinished " + sb.toString());
            Log.d("Java log", sb.toString());
            pDialog.dismiss();
            alert.showAlertDialog(mContext,
                    MainActivity.isEnglish ? "Results" : "Результаты",
                    sb.toString().substring(0, sb.toString().length() - 1),
                    edu.amd.spbstu.sbpmap.R.drawable.find, false);
            initProgressDialog();
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
        initProgressDialog();
        this.assetManager = assetManager;
    }

    synchronized void incrementProgressDialog() {
        pDialog.incrementProgressBy(1);
    }

    synchronized protected void addIsFinished(String query, int count) {
        requestList.put(query, count);
        incrementProgressDialog();
        Log.d("Java log", "addIsFinished: " + requestList.size() + " requestcount " + requestCount);
        if (requestList.size() == requestCount) {
            loadUrl("javascript:addMarkers()");
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
        pDialog = new ProgressDialog(mContext, R.style.CustomDialog);
        String msg = MainActivity.isEnglish ? "Searching ..." : "Поиск ...";
        pDialog.setMessage(msg);
        pDialog.setProgress(0);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.setIcon(R.drawable.find);
    }



    public void searchPlaces(double lat, double lng, ArrayList<Integer> seletedItems, LatLngBounds curLatLngBounds) {
        requestCount = seletedItems.size();

        pDialog.setMax(requestCount);
        pDialog.show();
        TextView tv1 = (TextView) pDialog.findViewById(android.R.id.message);
        tv1.setTextColor(Color.parseColor("#ffffffff"));
        tv1.setBackgroundColor(Color.parseColor("#ff426088"));

        //alertDialog = alert.alertDialog(mContext, "", "", edu.amd.spbstu.sbpmap.R.drawable.find);
        loadUrl("javascript:mapZoom()");

        //SystemClock.sleep(2000);
        for (int selectedItem : seletedItems) {
            Log.d("Java log", "searchPlaces(): " + lat + " " + lng);
            execute(curLatLngBounds, lat, lng, WebPlaceFinder.VENUES[selectedItem], 1000);
        }
    }

    public void execute(LatLngBounds curLatLngBounds, double lat, double lng, String query, int radius) {
        API api;
        Log.d("Java log", "execute(): query - " + query);
        switch (query) {
            case RESTAURANT:
                api = new RestoclubAPI(curLatLngBounds, lat, lng);
                break;
            case HOSTEL:
            case HOTEL:
            case MINI_HOTEL:
                api = new OstrovokAPI(assetManager, curLatLngBounds, lat, lng);
                break;
            default:
                api = new EtovidelAPI(assetManager, curLatLngBounds, lat, lng);
                break;
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
            ArrayList<Place> venues;
            Log.d("Java log", "onPostExecute(): query - " + query);
            if (response != null) {
                venues = api.parseResponse(response);
                addMarkersToMap(venues, query, api.getLat(), api.getLng());
            } else {
                addIsFinished(query, -1);
            }
        }
    }

    private void addMarkersToMap(ArrayList<Place> venues, String query, double locLat, double locLng) {
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
            addIsFinished(query, 0);
            //loadUrl("javascript:addMarkers('" + query +"')");
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

