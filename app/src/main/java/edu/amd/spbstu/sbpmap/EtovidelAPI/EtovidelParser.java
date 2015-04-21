package edu.amd.spbstu.sbpmap.EtovidelAPI;

import edu.amd.spbstu.sbpmap.Map.Place;
import edu.amd.spbstu.sbpmap.Utils.LatLngBounds;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by JDima on 29/03/15.
 */
public class EtovidelParser {

    private static String[] borders = {"<title>", "</title>", "Добавить в мою коллекцию</a>",
                                "</div>", "<p>", "<strong>Адрес: </strong>", "</p>"};

    private static String getField(String src, String border1, String border2) {
        src = src.substring(src.indexOf(border1) + border1.length());
        String result = src.substring(0, src.indexOf(border2));
        return src;
    }

    public static ArrayList<Place> parseResponse(String response, String query, double cenlat, double cenlng, LatLngBounds latLngBounds) {
        ArrayList<Place> temp = new ArrayList<Place>();
        try {
            JSONArray jsonArray= new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("kind").equals(query)) {
                    Place poi = new Place();

                    poi.setLat(jsonArray.getJSONObject(i).getDouble("lat"));
                    poi.setLng(jsonArray.getJSONObject(i).getDouble("lng"));

                    double alpha = LatLngBounds.getAlpha(poi.getLat(), poi.getLng(),
                            cenlat, cenlng, latLngBounds);
                    if (alpha < 0.2) {
                        continue;
                    }
                    poi.setAlpha(alpha);

                    poi.setName(jsonArray.getJSONObject(i).getString("name"));
                    poi.setId(query + jsonArray.getJSONObject(i).getString("id"));

                    temp.add(poi);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Place>();
        }
        return temp;
    }

    public static EtovidelInfoPlace parseSinglePlaceResponse(String response) {
        EtovidelInfoPlace oip = new EtovidelInfoPlace();
        response = response.substring(response.indexOf(borders[0]) + borders[0].length());
        oip.setName(response.substring(0, Math.min(response.indexOf(","), response.indexOf("("))));

        response = response.substring(response.indexOf(borders[2]) + borders[2].length());
        response = response.substring(response.indexOf(borders[3]) + borders[3].length());
        String definition = response.substring(0, response.indexOf(borders[4]));
        definition.replace("\n", "").replace("\r", "").replace("	", "");
        oip.setDefinition(definition);

        response = response.substring(response.indexOf(borders[5]) + borders[5].length());
        oip.setAddress(response.substring(0, response.indexOf(borders[6])));
        return oip;
    }
}
