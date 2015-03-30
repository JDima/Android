package com.sbpmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sbpmap.Foursquare.FoursquareAPI;
import com.sbpmap.Map.API;
import com.sbpmap.Map.WebPlaceFinder;
import com.sbpmap.Ostrovok.OstrovokAPI;
import com.sbpmap.Restoclub.RestoclubAPI;
import com.sbpmap.Utils.APIRequest;
import com.sbpmap.Utils.AlertDialogManager;


public class SinglePlaceActivity extends Activity{
      public static final String NOT_PRESENT = "Not present";
	  ProgressDialog pDialog;
      AlertDialogManager alert = new AlertDialogManager();

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	      setContentView(R.layout.single_place);
	         
	      Intent i = getIntent();
	         
	      String venueId = i.getStringExtra(WebPlaceFinder.VENUE_ID);
	      //alert.showAlertDialog(SinglePlaceActivity.this, "trtr",
	      //	  venueId, false);

          API api;
          if (venueId.contains(WebPlaceFinder.HOSTEL) || venueId.contains(WebPlaceFinder.HOTEL) || venueId.contains(WebPlaceFinder.MINI_HOTEL)) {
              setContentView(R.layout.single_place_hotel);
              api = new OstrovokAPI(getAssets());
          }
          else if (venueId.length() < 7) {
              setContentView(R.layout.single_place_restaurant);
              api = new RestoclubAPI();
          } else {
              setContentView(R.layout.single_place);
              api = new FoursquareAPI();
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
              pDialog.setMessage("Loading profile ...");
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
