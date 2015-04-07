package com.sbpmap.Ostrovok;

import com.sbpmap.Map.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

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

    private static LinkedHashSet<String> getFilters(JSONObject obj, String filter) {
        LinkedHashSet<String> filters = new LinkedHashSet<>();
        try {
            if (obj.has(filter)) {
                String serp_filters = obj.getString(filter);
                while(serp_filters.contains("'")) {
                    serp_filters = serp_filters.substring(serp_filters.indexOf("'") + 1);
                    filters.add(serp_filters.substring(0, serp_filters.indexOf("'")));
                    serp_filters = serp_filters.substring(serp_filters.indexOf("'") + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filters;
    }

    public static OstrovokInfoPlace parseSinglePlaceResponse(String response, String id) {
        OstrovokInfoPlace oip = new OstrovokInfoPlace();
        try {
            JSONArray jsonArray= new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                if (id.contains(jsonArray.getJSONObject(i).getString("id"))) {
                    oip.setName(jsonArray.getJSONObject(i).getString("name"));
                    oip.setAddress(jsonArray.getJSONObject(i).getString("address"));
                    oip.setCost(jsonArray.getJSONObject(i).getString("price"));
                    oip.setDefinition(jsonArray.getJSONObject(i).getString("room_name"));

                    JSONObject obj = jsonArray.getJSONObject(i);
                    oip.addAdds(getFilters(obj,"serp_filters1"));
                    oip.addAdds(getFilters(obj,"serp_filters2"));

                    return oip;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return oip;
    }
}
