package com.sbpmap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.sbpmap.Map.WebPlaceFinder;
import com.sbpmap.Utils.AlertDialogManager;
import com.sbpmap.Utils.ConnectionDetector;
import com.sbpmap.Utils.GPSTracker;
import com.sbpmap.Utils.CustomWindowAdapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import java.sql.Connection;
import java.util.Locale;


public class MainActivity extends Activity {
	private GoogleMap googleMap;
    private boolean isSearh = false;
    private Menu menu;
    public static boolean isEnglish = true;
    private static final LatLngBounds saintPetersburgBounds = new LatLngBounds(
                                                    new LatLng(59.71584, 30.09941),
                                                    new LatLng(60.09037, 30.61897));

    private LatLng cameraPos = new LatLng(0,0);


    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    GPSTracker gps;
    WebPlaceFinder fp;
    
    public boolean detectOpenGLES20() {
        ActivityManager am = (ActivityManager) getSystemService(MainActivity.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(MainActivity.this, "AGAIN", Toast.LENGTH_LONG).show();

        String strLang = Locale.getDefault().getDisplayLanguage();
        if (strLang.equalsIgnoreCase("русский")){
            isEnglish = false;
        }

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            this.finish();
        } else {

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            cd = new ConnectionDetector(getApplicationContext());

            if (googleMap != null) {
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(saintPetersburgBounds, 10));
                    }
                });

                fp = new WebPlaceFinder(googleMap, getAssets());

                gps = new GPSTracker(this);

                googleMap.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

                googleMap.setMyLocationEnabled(true);

                googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker arg) {
                        if (!cd.isConnectingToInternet()) {
                            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                                    "Please connect to Internet!", false);
                            return true;
                        }
                        Intent in = new Intent(getApplicationContext(), SinglePlaceActivity.class);

                        LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        in.putExtra(SinglePlaceActivity.BOUNDS_XL, latLngBounds.southwest.longitude);
                        in.putExtra(SinglePlaceActivity.BOUNDS_XR, latLngBounds.northeast.longitude);
                        in.putExtra(SinglePlaceActivity.BOUNDS_YL, latLngBounds.northeast.latitude);
                        in.putExtra(SinglePlaceActivity.BOUNDS_YR, latLngBounds.southwest.latitude);
                        in.putExtra(SinglePlaceActivity.VENUE_ID, arg.getSnippet());

                        startActivity(in);
                        //Toast.makeText(MainActivity.this, arg0.getSnippet(), 1000).show();
                        return false;
                    }

                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng arg0) {
                        if (isSearh) {
                            isSearh = false;
                            searchPlaces(arg0.latitude, arg0.longitude, menu.getItem(0).getSubMenu());
                        }
                    }
                });

                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        }
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

    public void searchPlaces(double lat, double lng, SubMenu subMenu) {
        boolean result = fp.searchPlaces(lat, lng, subMenu);
        cameraPos = new LatLng(lat, lng);
        if (!result) {
            String msg = isEnglish ? "Nothing found." : "Ничего не найдено.";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	String query = null;
        isSearh = false;
        switch(item.getItemId())
        {
        case R.id.about:
            Intent in = new Intent(getApplicationContext(), ActivityMain.class);
            startActivity(in);
            return true;
        case R.id.clean:
            fp.removeAll();
            for (int i = 0; i < WebPlaceFinder.VENUES.length; i++)
                menu.getItem(0).getSubMenu().getItem(i).setChecked(false);
            return true;


        case R.id.search_near:
            if (!cd.isConnectingToInternet()) {
                alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                        "Please connect to Internet!", false);
                return true;
            }
            if (gps.canGetLocation()) {
               /* alert.showAlertDialog(MainActivity.this, "GPS Status",
                        "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude(),
                        false);*/
            } else {
                gps.showSettingsAlert();
                return true;
            }
            /*if (gps.getLatitude() == 0) {
                alert.showAlertDialog(MainActivity.this, "GPS Status",
                        "GPS do not work correctly!",
                        false);
                return true;
            }*/

            fp.removeAll();
            //double lat = 59.9300, lng = 30.3615;
            double lat = gps.getLatitude(), lng = gps.getLongitude();
            searchPlaces(lat, lng, menu.getItem(0).getSubMenu());
            return true;

        case R.id.search:
            fp.removeAll();
            isSearh = true;
            return true;

        case R.id.places:
            return true;

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
            LatLng latLng = googleMap.getCameraPosition().target;
        	fp.remove(query, latLng.latitude, latLng.longitude);
            item.setChecked(false);
        }
        else {
            item.setChecked(true);
        }
    	
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("lat", cameraPos.latitude);
        outState.putDouble("lng", cameraPos.longitude);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cameraPos = new LatLng(savedInstanceState.getDouble("lat"), savedInstanceState.getDouble("lng"));
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