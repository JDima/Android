package edu.amd.spbstu.sbpmap;

import edu.amd.spbstu.sbpmap.Map.WebPlaceFinder;
import edu.amd.spbstu.sbpmap.Map.WebPlaceFinder.WebPlaceFinderJS;
import edu.amd.spbstu.sbpmap.Utils.AlertDialogManager;
import edu.amd.spbstu.sbpmap.Utils.ConnectionDetector;
import edu.amd.spbstu.sbpmap.Utils.GPSTracker;
import edu.amd.spbstu.sbpmap.Utils.LatLngBounds;
import edu.amd.spbstu.sbpmap.Utils.QustomDialogBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


import java.util.Locale;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    static WebView myWebView;
    public static boolean isEnglish = true;
    private static final String MAP_URL = "file:///android_asset/simplemap.html";
    ProgressDialog progressBar;

    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    GPSTracker gps;
    WebPlaceFinder fp;

    class MainActivityJS
    {
        @JavascriptInterface
        public void startSinglePlaceActivity(String id, double slat, double slng, double nlat, double nlng) {
            Log.d("Java log", "Marker: " + id + "Coordinates:" + slat + "\n" + slng + "\n" + nlat + "\n" + nlng + "\n");
            Intent in = new Intent(getApplicationContext(), SinglePlaceActivity.class);

            in.putExtra(SinglePlaceActivity.BOUNDS_XL, slng);
            in.putExtra(SinglePlaceActivity.BOUNDS_XR, nlng);
            in.putExtra(SinglePlaceActivity.BOUNDS_YL, nlat);
            in.putExtra(SinglePlaceActivity.BOUNDS_YR, slat);
            in.putExtra(SinglePlaceActivity.VENUE_ID, id);

            startActivity(in);
        }

        @JavascriptInterface
        public void showSelectDialog(double lat, double lng, double slat, double slng, double nlat, double nlng) {
            Log.d("Java log", "showSelectDialog:" + slat + "\n" + slng + "\n" + nlat + "\n" + nlng + "\n");
            alert.showSelectCategoryDialog(MainActivity.this, fp, lat, lng, new LatLngBounds(slat, slng, nlat, nlng));
        }

        @JavascriptInterface
        public void cleanMap() {
            fp.removeAll();
            alert.showAlertDialog(MainActivity.this,
                    isEnglish ? "Removing" : "Удаление",
                    isEnglish ? "All objects are removed!" : "Все объекты удалены!",
                    R.drawable.remove);
            return;
        }

        @JavascriptInterface
        public void mapIsEmpty() {
            alert.showAlertDialog(MainActivity.this,
                    isEnglish ? "Removing" : "Удаление",
                    isEnglish ? "Map does not contain markers!" : "Карта не содержит маркеров!",
                    R.drawable.remove);
            return;
        }
    }

    public void setLocale() {
        String strLang = Locale.getDefault().getDisplayLanguage();
        if (strLang.equalsIgnoreCase("русский")){
            isEnglish = false;
        }
    }

    void connectionErrorDialog() {
        AlertDialog alertDialog = alert.connectionError(MainActivity.this);
        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
        Log.d("Java log", "connectionErrorDialog()");
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressBar!=null) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    if (!cd.isConnectingToInternet()) {
                        connectionErrorDialog();
                        return;
                    } else {
                        onStart();
                    }
                }

            }
        }, intentFilter);

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff426088")));
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        ab.setIcon(R.drawable.logo);

        setContentView(R.layout.activity_main);

        setLocale();

        cd = new ConnectionDetector(getApplicationContext());

        gps = new GPSTracker(this);

        myWebView = (WebView)findViewById(R.id.mapview);

        progressBar = ProgressDialog.show(MainActivity.this,
                isEnglish ? "SBPMap" : "Карта СПБ",
                isEnglish ? "Loading" : "Загрузка" + "...");

        myWebView.loadUrl(MAP_URL);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);

        fp = new WebPlaceFinder(MainActivity.this, myWebView, getAssets());

        myWebView.addJavascriptInterface(new MainActivityJS(), "mainactivity");
        myWebView.addJavascriptInterface(fp.new WebPlaceFinderJS(), "webplacefinder");
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("MyApplication", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
        });

        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("Java log: ", url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("Java log: ", "Finished loading URL: " + url);
                if ((progressBar != null) && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("Java log: ", "Error: " + description);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(isEnglish ? "Error" : "Ошибка");
                alertDialog.setMessage(description);
                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

        Button btnClean = (Button)this.findViewById(R.id.clean);
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.loadUrl("javascript:cleanMap()");

            }
        });

        Button btnSearchNear = (Button)this.findViewById(R.id.search_near);
        btnSearchNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps.canGetLocation()) {
               /* alert.showAlertDialog(MainActivity.this, "GPS Status",
                        "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude(),
                        false);*/
                } else {
                    gps.showSettingsAlert();
                    return;
                }
                if (gps.getLatitude() == 0) {
                    String title = isEnglish ? "GPS Status" : "GPS Статус";
                    String msg = isEnglish ? "Incorrect GPS location" : "Неверная GPS локация";
                    alert.showAlertDialog(MainActivity.this, title,
                            msg,
                            R.drawable.fail);
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
        setLocale();
        //cd = new ConnectionDetector(getApplicationContext());

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.about:
                Intent in = new Intent(getApplicationContext(), ActivityMain.class);
                startActivity(in);
                break;
            case R.id.help:
                alert.showAlertDialog(MainActivity.this,
                        isEnglish ? "Help" : "Помощь",
                        isEnglish ? "Click on the screen to search." : "Нажмите на экран для поиска.", R.drawable.help);
                break;


        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putDouble("lat", cameraPos.latitude);
        //outState.putDouble("lng", cameraPos.longitude);

    }

    public boolean onKeyDown(int keyCode, KeyEvent evt)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                return true;
            case KeyEvent.KEYCODE_MENU:
                return true;
        }
        boolean ret = super.onKeyDown(keyCode, evt);
        return ret;
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

    public static void callWebView(final String query) {
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);
        myWebView.loadUrl(query);
    }

}