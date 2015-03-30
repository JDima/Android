package com.sbpmap.Ostrovok;

import com.sbpmap.Map.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JDima on 29/03/15.
 */
public class OstrovokParser {
    public static ArrayList<Place> parseResponse(String response, String query) {
        ArrayList<Place> temp = new ArrayList<Place>();
        try {
            JSONArray jsonArray= new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("kind").equals(query)) {
                    Place poi = new Place();

                    poi.setName(jsonArray.getJSONObject(i).getString("name"));
                    poi.setId(query + jsonArray.getJSONObject(i).getString("id"));
                    poi.setLat(jsonArray.getJSONObject(i).getDouble("lat"));
                    poi.setLng(jsonArray.getJSONObject(i).getDouble("lng"));

                    temp.add(poi);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Place>();
        }
        return temp;
    }

    public static OstrovokInfoPlace parseSinglePlaceResponse(String response, String id) {
        OstrovokInfoPlace oip = new OstrovokInfoPlace();
        try {
            JSONArray jsonArray= new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                if (id.contains(jsonArray.getJSONObject(i).getString("id"))) {
                    oip.setName(jsonArray.getJSONObject(i).getString("name"));
                    oip.setAddress(jsonArray.getJSONObject(i).getString("address"));
                    oip.setCost(jsonArray.getJSONObject(i).getDouble("price"));
                    oip.setDefinition(jsonArray.getJSONObject(i).getString("room_name"));
                    return oip;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return oip;
    }
}
