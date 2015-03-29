package com.sbpmap.Foursquare;

import com.sbpmap.Map.Place;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class FoursquareParser {

    public static ArrayList<Place> parseResponse(String response) {
        ArrayList<Place> temp = new ArrayList<Place>();
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Place poi = new Place();
                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).getString("name"));
                            poi.setId(jsonArray.getJSONObject(i).getString("id"));
                            if (jsonArray.getJSONObject(i).has("location")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("location").has("lat")) {
                                    poi.setLat(jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lat"));
                                }
                                if (jsonArray.getJSONObject(i).getJSONObject("location").has("lng")) {
                                    poi.setLng(jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lng"));
                                }
                            }
                        }
                        temp.add(poi);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Place>();
        }
        return temp;
    }

    public static FoursquareInfoPlace parseSinglePlaceResponse(String response) {
        FoursquareInfoPlace poi = new FoursquareInfoPlace();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("response")) {

                if (jsonObject.getJSONObject("response").has("venue")) {
                    jsonObject = jsonObject.getJSONObject("response").getJSONObject("venue");

                    poi.setName(jsonObject.getString("name"));
                    poi.setId(jsonObject.getString("id"));

                    if (jsonObject.has("location")) {
                        if (jsonObject.getJSONObject("location").has("lat")) {
                            poi.setLat(jsonObject.getJSONObject("location").getDouble("lat"));
                        }
                        if (jsonObject.getJSONObject("location").has("lng")) {
                            poi.setLng(jsonObject.getJSONObject("location").getDouble("lng"));
                        }
                        if (jsonObject.getJSONObject("location").has("address")) {
                            poi.setAddress(jsonObject.getJSONObject("location").getString("address"));
                        }
                    }
                    if (jsonObject.has("contact")) {
                        if (jsonObject.getJSONObject("contact").has("phone")) {
                            poi.setPhone(jsonObject.getJSONObject("contact").getString("phone"));
                        }
                    }
                    if (jsonObject.has("hours")) {
                        if (jsonObject.getJSONObject("hours").has("status")) {
                            poi.setHours(jsonObject.getJSONObject("hours").getString("status"));
                        }
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            return new FoursquareInfoPlace();
        }
        return poi;
    }
}
