package edu.amd.spbstu.sbpmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import edu.amd.spbstu.sbpmap.EtovidelAPI.EtovidelAPI;
import edu.amd.spbstu.sbpmap.Map.API;
import edu.amd.spbstu.sbpmap.Map.WebPlaceFinder;
import edu.amd.spbstu.sbpmap.Ostrovok.OstrovokAPI;
import edu.amd.spbstu.sbpmap.Restoclub.RestoclubAPI;
import edu.amd.spbstu.sbpmap.Utils.APIRequest;
import edu.amd.spbstu.sbpmap.Utils.LatLngBounds;


public class SinglePlaceActivity extends Activity{
      public static final String NOT_PRESENT = MainActivity.isEnglish ? "Not present": "Неизвестно";
      public static final String VENUE_ID = "venue_id";
      public static final String BOUNDS_XL = "bounds_xl";
      public static final String BOUNDS_XR = "bounds_xr";
      public static final String BOUNDS_YL = "bounds_yl";
      public static final String BOUNDS_YR = "bounds_yr";


	  ProgressDialog pDialog;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	      setContentView(R.layout.single_place);
	         
	      Intent i = getIntent();
	      String venueId = i.getStringExtra(VENUE_ID);
          LatLngBounds latLngBounds = new LatLngBounds(i.getDoubleExtra(BOUNDS_YR, 0), i.getDoubleExtra(BOUNDS_YL, 0),
                                                       i.getDoubleExtra(BOUNDS_XL, 0), i.getDoubleExtra(BOUNDS_XR, 0));

          API api;
          if (venueId.contains(WebPlaceFinder.HOSTEL) || venueId.contains(WebPlaceFinder.HOTEL) || venueId.contains(WebPlaceFinder.MINI_HOTEL)) {
              setContentView(R.layout.single_place_hotel);
              api = new OstrovokAPI(getAssets());
          } else if (venueId.contains(WebPlaceFinder.MONUMENT) || venueId.contains(WebPlaceFinder.LANDMARK)
                    || venueId.contains(WebPlaceFinder.BRIDGE) || venueId.contains(WebPlaceFinder.PARK)){
              setContentView(R.layout.single_place_landmark);
              api = new EtovidelAPI(getAssets());
          } else  {
              setContentView(R.layout.single_place_restaurant);
              api = new RestoclubAPI(latLngBounds);
          }
          new LoadSinglePlaceDetails().execute(new APIRequest(api, api.getSinglePlaceRequest(venueId), venueId));
	  }
	     
	     
	  class LoadSinglePlaceDetails extends AsyncTask<APIRequest, String, String> {
		  String response;
		  API api;
	      @Override
	      protected void onPreExecute() {
	    	  super.onPreExecute();
              pDialog = new ProgressDialog(SinglePlaceActivity.this);
              String msg = MainActivity.isEnglish ? "Searching ..." : "Поиск ...";
              pDialog.setMessage(msg);
              pDialog.setIndeterminate(false);
              pDialog.setCancelable(false);
              pDialog.show();
	      }
	 
	      protected String doInBackground(APIRequest... args) {
              api = args[0].api;
	    	  response = api.getSinglePlace(args[0].query);
			  return "";
	      }
	 
	      protected void onPostExecute(String file_url) {
	          pDialog.dismiss();
	          runOnUiThread(new Runnable() {
	        	  public void run() {
                      if (response != null) {
                          api.createSinglePage(SinglePlaceActivity.this, response);
                      }
                  }
	          });
	      }
	  }

}
