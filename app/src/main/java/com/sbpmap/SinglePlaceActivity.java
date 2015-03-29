package com.sbpmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sbpmap.Foursquare.FoursquareAPI;
import com.sbpmap.Map.API;
import com.sbpmap.Map.WebPlaceFinder;
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
	      //alert.showAlertDialog(SinglePlaceActivity.this, "OHUENNO",
	      //	  venueId, false);

          API api;
          if (venueId.length() < 7) {
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
	    	  //temp = makeCall("https://api.foursquare.com/v2/venues/430d0a00f964a5203e271fe3?client_id=5WKHRU5SHZF0HMGPW1CHWW1FVAMYJH5X1UC1LI0CWZ2NSVP1&client_secret=HIU1W3V2KIDZE2Y2JW3MML4BDLVHHMJAXDBGO5QWQVRLY5QB&v=20150309");
			  return "";
	      }
	 
	      protected void onPostExecute(String file_url) {
	          pDialog.dismiss();
	          runOnUiThread(new Runnable() {
	        	  public void run() {
			            if (response == null) {
							
						} else {
                            api.createSinglePage(SinglePlaceActivity.this, response);

						}
	        	  }
	          });
	      }
	  }

}
