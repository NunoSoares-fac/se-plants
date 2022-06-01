package com.example.wateringfromtwitterapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    public void jsonToMapNoWrapper(Map<String, String> map, String plantName, JSONObject body) {
        try {
            for (Iterator<String> it = body.keys(); it.hasNext(); ) {
                String variable = it.next();
                map.put(plantName + "." + variable, body.getString(variable));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject mapToJson(Map<String,String> map, String[] plants) {
        JSONObject body = new JSONObject();
        try {
            for (String plantName: plants) {
                body.put(plantName, new JSONObject());
                for (String key: map.keySet()) {
                    String variable = key.split("\\.")[1].substring(1);
                    body.getJSONObject(plantName).put(variable, map.get(plantName + "." + variable));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }

    public JSONObject mapToJsonNoWrapper(Map<String,String> map, String plantName) {
        JSONObject body = new JSONObject();
        try {
            for (String key: map.keySet()) {
                body.put(key, map.get(plantName + "." + key));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }
}
