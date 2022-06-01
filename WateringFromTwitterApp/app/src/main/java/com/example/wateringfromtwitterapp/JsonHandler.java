package com.example.wateringfromtwitterapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonHandler {

    private static JsonHandler instance = null;

    private JsonHandler() {}

    public static JsonHandler get() {
        if (instance == null) {
            synchronized (JsonHandler.class) {
                if (instance == null) {
                    instance = new JsonHandler();
                }
            }
        }
        return instance;
    }

    public Map<String, String> jsonToMap(JSONObject body) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            Iterator<String> keys = body.keys();
            while (keys.hasNext()) {
                String plantName = keys.next();
                JSONObject wrapper = body.getJSONObject(plantName);
                for (Iterator<String> it = wrapper.keys(); it.hasNext(); ) {
                    String variable = it.next();
                    responseMap.put(plantName + "." + variable, wrapper.getString(variable));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return responseMap;
    }

    public JSONObject mapToJson(Map<String,String> map) {
        JSONObject body = new JSONObject();
        try {
            body.put("plant1", new JSONObject());
            body.put("plant2", new JSONObject());
            Iterator<String> keys = body.keys();
            while (keys.hasNext()) {
                String plantName = keys.next();
                JSONObject wrapper = body.getJSONObject(plantName);
                for (Iterator<String> it = wrapper.keys(); it.hasNext(); ) {
                    String key = it.next();
                    map.put(plantName + "." + key, wrapper.getString(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }
}
