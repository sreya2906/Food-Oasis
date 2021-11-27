package com.example.foodoasis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// class fro parsing data which are fetched from Google Api
public class PlaceResult {
    private HashMap<String, String> parseJsonObject(JSONObject object) {
        HashMap<String, String> dataList = new HashMap<>();
        try {
            //get details from the object
            String name = object.getString("name");
            String icon_url = object.getString("icon");
            String place_id = object.getString("place_id");
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location")
                    .getString("lat");
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location")
                    .getString("lng");

            //add key and value pairs in the hashmap
            dataList.put("name", name);
            dataList.put("icon", icon_url);
            dataList.put("place_id", place_id);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                //add data in hashmap list
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArray(jsonArray);
    }
}
