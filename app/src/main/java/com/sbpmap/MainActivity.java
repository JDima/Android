package com.sbpmap;

import com.sbpmap.Map.WebPlaceFinder;
import com.sbpmap.Utils.AlertDialogManager;
import com.sbpmap.Utils.ConnectionDetector;
import com.sbpmap.Utils.GPSTracker;
import com.sbpmap.Utils.LatLngBounds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


import java.util.Locale;


public class MainActivity extends Activity {

    class MainActivityJS
    {
        public void startSinglePlaceActivity(String id, double slat, double slng, double nlat, double nlng)
        {
            if (!cd.isConnectingToInternet()) {
                alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to Internet!", false);
            }
            Log.d("Java log", "Marker: " + id + "Coordinates:" + slat + "\n" + slng + "\n" + nlat + "\n" + nlng + "\n");
            Intent in = new Intent(getApplicationContext(), SinglePlaceActivity.class);

            in.putExtra(SinglePlaceActivity.BOUNDS_XL, slng);
            in.putExtra(SinglePlaceActivity.BOUNDS_XR, nlng);
            in.putExtra(SinglePlaceActivity.BOUNDS_YL, nlat);
            in.putExtra(SinglePlaceActivity.BOUNDS_YR, slat);
            in.putExtra(SinglePlaceActivity.VENUE_ID, id);

            startActivity(in);
        }

        public void searchPlaces(double lat, double lng, double slat, double slng, double nlat, double nlng) {
            if (!cd.isConnectingToInternet()) {
                alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                        "Please connect to Internet!", false);
                return;
            }
            Log.d("Java log", "Coordinates:" + slat + "\n" + slng + "\n" + nlat + "\n" + nlng + "\n");
            boolean result = fp.searchPlaces(lat, lng, menu, new LatLngBounds(slat, slng, nlat, nlng));
            if (!result) {
                String msg = isEnglish ? "Select places to search!" : "Выберите места для поиска!";
                alert.showAlertDialog(MainActivity.this, "Error!",
                        msg, false);
            }
        }

        public void notFound(String query) {
            String msg = isEnglish ? query + ": Nothing found!" : query + ": Ничего не найдено!";
            alert.showAlertDialog(MainActivity.this, "Information.",
                    msg, true);

        }
    }

    static WebView myWebView;
    private Menu menu;
    public static boolean isEnglish = true;

    private static final String MAP_URL = "file:///android_asset/simplemap.html";

    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    GPSTracker gps;
    WebPlaceFinder fp;
    
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView)findViewById(R.id.mapview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);


        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("Java log", consoleMessage.message() + " — From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());

                return true;
            }
        });

        myWebView.loadUrl(MAP_URL);
        myWebView.addJavascriptInterface(new MainActivityJS(), "ac");

        String strLang = Locale.getDefault().getDisplayLanguage();
        if (strLang.equalsIgnoreCase("русский")){
            isEnglish = false;
        }

        cd = new ConnectionDetector(getApplicationContext());


        fp = new WebPlaceFinder(MainActivity.this, myWebView, getAssets());

        gps = new GPSTracker(this);

        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to Internet!", false);
        }


        Button btnClean = (Button)this.findViewById(R.id.clean);
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fp.removeAll();
                for (int i = 0; i < WebPlaceFinder.VENUES.length; i++)
                    menu.getItem(i).setChecked(false);
                return;

            }
        });

        Button btnSearchNear = (Button)this.findViewById(R.id.search_near);
        btnSearchNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cd.isConnectingToInternet()) {
                    alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                            "Please connect to Internet!", false);
                    return;
                }
                if (gps.canGetLocation()) {
               /* alert.showAlertDialog(MainActivity.this, "GPS Status",
                        "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude(),
                        false);*/
                } else {
                    gps.showSettingsAlert();
                    return;
                }
                if (gps.getLatitude() == 0) {
                    alert.showAlertDialog(MainActivity.this, "GPS Status",
                            "Do not find satelites!",
                            false);
                    return;
                }

                fp.removeAll();
                double lat = gps.getLatitude(), lng = gps.getLongitude();
                myWebView.loadUrl("javascript:searchNear('" + lat +
                        "','" + lng + "')");

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      this.menu = menu;
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	String query = null;
        switch(item.getItemId())
        {

        case R.id.hotel:
        	query = WebPlaceFinder.HOTEL;
            break;

        case R.id.hostel:
        	query = WebPlaceFinder.HOSTEL;
            break;

        case R.id.restaurant:
        	query = WebPlaceFinder.RESTAURANT;
            break;

        case R.id.landmark:
        	query = WebPlaceFinder.LANDMARK;
            break;
        case R.id.monument:
            query = WebPlaceFinder.MONUMENT;
            break;
        case R.id.park:
            query = WebPlaceFinder.PARK;
            break;
        case R.id.bridge:
            query = WebPlaceFinder.BRIDGE;
            break;
        case R.id.minihotel:
            query = WebPlaceFinder.MINI_HOTEL;
            break;
        }

        if(item.isChecked()) {
        	fp.remove(query);
            item.setChecked(false);
        }
        else {
            String msg;
            if (isEnglish) {
                msg = "You choosed - ";
            } else {
                msg = "Вы выбрали - ";
            }
            Log.d("Java log", msg + query);
            Toast.makeText(getApplicationContext(), msg + query, Toast.LENGTH_LONG).show();
            item.setChecked(true);
        }
    	
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putDouble("lat", cameraPos.latitude);
        //outState.putDouble("lng", cameraPos.longitude);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //cameraPos = new LatLng(savedInstanceState.getDouble("lat"), savedInstanceState.getDouble("lng"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cameraPos.latitude, cameraPos.longitude), 15));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                // do what you want when user is in LANDSCAPE
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                // do what you want when user is in PORTRAIT
                break;
        }

    }
}