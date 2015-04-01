package com.sbpmap;
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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
	private GoogleMap googleMap;
    private boolean isSearh = false;
    private Menu menu;


    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    public static String KEY_REFERENCE = "reference";
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
        
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();
        cd = new ConnectionDetector(getApplicationContext());
        if (googleMap != null) {

            fp = new WebPlaceFinder(googleMap, getAssets());
        
            gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                alert.showAlertDialog(MainActivity.this, "GPS Status",
                        "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude(),
                        false);
            } else {
                gps.showSettingsAlert();
                return;
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(new OnMarkerClickListener()
            {

                @Override
                public boolean onMarkerClick(Marker arg) {
                    if (!cd.isConnectingToInternet()) {
                        alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                                "Please connect to working Internet connection", false);
                        return false;
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
                        for (int id = 0; id < WebPlaceFinder.VENUES.length; id++) {
                            if (menu.getItem(0).getSubMenu().getItem(id).isChecked()) {
                                fp.execute(arg0.latitude, arg0.longitude, WebPlaceFinder.VENUES[id], 1000);
                            }
                        }
                        Toast.makeText(MainActivity.this, "Current pos:"
                                 + googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude + " --"
                                + googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.longitude + " --"
                                + googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.latitude + " --"
                                + googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.longitude, Toast.LENGTH_LONG).show();
                    }
                }
            });
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        } else {
            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	String query = null;
        isSearh = false;
    	if (!cd.isConnectingToInternet()) {
    		alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            return true;
    	}
        switch(item.getItemId())
        {
        case R.id.clean:
            fp.removeAll();
            for (int i = 0; i < WebPlaceFinder.VENUES.length; i++)
                menu.getItem(0).getSubMenu().getItem(i).setChecked(false);
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
        	fp.remove(query);
            item.setChecked(false);
            
        }
        else {
        	fp.execute(59.9300, 30.3615, query, 10000);
            item.setChecked(true);
        }
    	
        return true;
    }
}