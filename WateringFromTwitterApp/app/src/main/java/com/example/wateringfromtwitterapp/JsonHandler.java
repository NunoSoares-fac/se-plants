package com.example.wateringfromtwitterapp;

import org.json.JSONObject;

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
/*
    public Map<String, String> jsonToMap(JSONObject body) {
    }

    public JSONObject fieldToJson(String field, String value) {

    }

    private void createEntry(Map<String,String> map, String field, String value) {

    }

 */
}
