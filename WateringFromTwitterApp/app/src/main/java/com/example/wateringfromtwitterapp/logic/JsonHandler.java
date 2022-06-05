package com.example.wateringfromtwitterapp.logic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>A singleton class that helps converting JSON objects to String Maps and vice-versa.</p>
 */
public class JsonHandler {

    private static JsonHandler instance = null;

    private JsonHandler() {
    }

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

    /**
     * <p>Converts a JSON object to a String map, by creating keys that match the hierarchy of keys in
     * the JSON object</p>
     * <p>It is assumed that the JSON object contains wrapper objects, which are the plants' names,
     * and within them come the actual values. Both of these are joined in a string, using '.' as a
     * split character</p>
     *
     * @param body JSON object to convert
     * @return Map of Strings with JSON's content as key/value pairs
     */
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

    /**
     * <p>Converts a Map of Strings to a JSON object, creating the wrapper objects that have the
     * plants' names first and then adding to each the String values in the Map</p>
     *
     * @param map    Map of Strings with the content to be converted
     * @param plants Array of plant names
     * @return JSON object with the content of 'map'
     */
    public JSONObject mapToJson(Map<String, String> map, String[] plants) {
        JSONObject body = new JSONObject();
        try {
            for (String plantName : plants) {
                body.put(plantName, new JSONObject());
                for (String key : map.keySet()) {
                    String variable = key.split("\\.")[1];
                    body.getJSONObject(plantName).put(variable, map.get(plantName + "." + variable));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }
}
