package com.sbpmap.Restoclub;

import com.sbpmap.Map.Place;
import com.sbpmap.SinglePlaceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JDima on 22/03/15.
 */
public class RestoclubParser {

    private final static Map<String, String> borders = new LinkedHashMap<String, String>()
    {
        {
            put("class=\"po_head\"><h1>", "</h1>");
            put("Заказ столика: ","\t");
            put("/map/\">","</a>");
            put("<b>Средний счет без напитков: </b>","</td>");
            put("<td width=\"100%\">","</td>");
        }
    };

    public final static String[] names = {"name", "phone", "address", "bill", "hours"};


    private static String getField(String src, String border1, String border2, String field, Map<String, String> fields) {
        src = src.substring(src.indexOf(border1) + border1.length());
        String result = src.substring(0, src.indexOf(border2));
        fields.put(field, result);
        return src;
    }

    public static ArrayList<Place> parseResponse(String response) {
        ArrayList<Place> temp = new ArrayList<Place>();
        try {
            JSONObject jsonObject = new JSONObject(response);

            Iterator iter = jsonObject.keys();
            while(iter.hasNext()) {
                Place poi = new Place();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject((String) iter.next());
                    poi.setName(jsonObject1.getString("name"));
                    poi.setLat(jsonObject1.getDouble("lat"));
                    poi.setLng(jsonObject1.getDouble("lng"));
                    poi.setId(jsonObject1.getString("id"));
                    temp.add(poi);
                } catch (Exception e) {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Place>();
        }
        return temp;
    }

    public static Map<String, String> parseSinglePlaceResponse(String response) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (String name : names) {
            fields.put(name, SinglePlaceActivity.NOT_PRESENT);
        }

        int i = 0;
        for (Map.Entry<String, String> border : borders.entrySet()) {
            if (response.contains(border.getKey())) {
                if (names[i].equals(names[4])) {
                    response = response.substring(response.indexOf("Время работы:"));
                }
                response = getField(response, border.getKey(), border.getValue(), names[i], fields);
            }
            i++;
        }

        return fields;
    }
}
