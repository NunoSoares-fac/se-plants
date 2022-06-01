package com.example.wateringfromtwitterapp;

import android.os.AsyncTask;
import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DataBroker {
    public static String baseUri = "http://192.168.43.191:8081";

    private Map<String, Plant> plants = new HashMap<>();
    private static DataBroker instance = null;

    private final static double DEFAULT_TEMPERATURE_THRESHOLD_DIFF = 2;
    private final static double DEFAULT_LUMINOSITY_THRESHOLD_DIFF = 30;
    private final static double DEFAULT_HUMIDITY_THRESHOLD_DIFF = 5;

    private DataBroker() {
    }

    public static DataBroker get() {
        if (instance == null) {
            synchronized (DataBroker.class) {
                if (instance == null) {
                    instance = new DataBroker();
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
            }
        }
        return instance;
    }

    public Plant addPlant(String plantName) {
        plants.put(plantName, new Plant(plantName));
        return plants.get(plantName);
    }

    public Plant getPlant(String plantName) {
        if (plants.containsKey(plantName)) {
            return plants.get(plantName);
        }
        return null;
    }

    public boolean loadPlant(String plantName) {
        if (!plants.containsKey(plantName)) {
            this.addPlant(plantName);
        }
        this.updateMeasurements(plantName);
        return true;
    }

    public Plant changeTemperatureActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.temperature().toggleForcedActive();
        this.patchActuators();
        return plant;
    }

    public Plant changeLuminosityActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.luminosity().toggleForcedActive();
        this.patchActuators();
        return plant;
    }

    public Plant changeHumidityActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.humidity().toggleForcedActive();
        this.patchActuators();
        return plant;
    }

    public Plant updateMeasurements(String plantName) {
        Plant plant1 = plants.get(plantName);
        //Get values
        Map<String, String> responseMap = this.getMeasurements();
        System.out.println(responseMap.toString());
        System.out.println(plantName);
        System.out.println(plants.toString());
        plant1.temperature().setValue(Double.parseDouble(responseMap.get(plantName + ".temperature")));
        plant1.luminosity().setValue(Double.parseDouble(responseMap.get(plantName + ".luminosity")));
        plant1.humidity().setValue(Double.parseDouble(responseMap.get(plantName + ".humidity")));

        //Get thresholds
        responseMap = this.getThresholds();
        plant1.temperature().setThreshold(Double.parseDouble(responseMap.get(plantName + ".temperature_lower")));
        plant1.luminosity().setThreshold(Double.parseDouble(responseMap.get(plantName + ".luminosity_lower")));
        plant1.humidity().setThreshold(Double.parseDouble(responseMap.get(plantName + ".humidity_lower")));

        //Get actuator flags
        responseMap = this.getActiveFlags();
        plant1.temperature().setActive(responseMap.get(plantName + ".led1").equals("1"));
        plant1.luminosity().setActive(responseMap.get(plantName + ".led3").equals("1"));
        plant1.humidity().setActive(responseMap.get(plantName + ".led2").equals("1"));
        return plants.get(plantName);
    }

    public Plant changeThresholds(String plantName, String newTemperatureThreshold, String newLuminosityThreshold, String newHumidityThreshold) {
        Plant plant = plants.get(plantName);
        if (newTemperatureThreshold != null) {
            plant.temperature().setThreshold(Double.parseDouble(newTemperatureThreshold));
        }
        if (newLuminosityThreshold != null) {
            plant.luminosity().setThreshold(Double.parseDouble(newLuminosityThreshold));
        }
        if (newHumidityThreshold != null) {
            plant.humidity().setThreshold(Double.parseDouble(newHumidityThreshold));
        }
        patchThreshold();
        patchActuators();
        return plant;
    }

    public Map<String, String> getMeasurements() {
        try {
            URL url = new URL(baseUri + "/info");
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setRequestMethod("GET");
            //con.connect();
            JSONObject json = this.readResponse(con);
            return JsonHandler.get().jsonToMap(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getThresholds() {
        try {
            URL url = new URL(baseUri + "/infoT");
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setRequestMethod("GET");
            JSONObject json = this.readResponse(con);
            return JsonHandler.get().jsonToMap(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getActiveFlags() {
        try {
            URL url = new URL(baseUri + "/infoL");
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setRequestMethod("GET");
            JSONObject json = this.readResponse(con);
            return JsonHandler.get().jsonToMap(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void patchThreshold() {
        Map<String, String> requestMap = new HashMap<>();
        for (String plantName: plants.keySet()) {
            Plant plant = plants.get(plantName);
            requestMap.put(plantName + ".temperature_lower", String.valueOf(plant.temperature().getThreshold()));
            requestMap.put(plantName  + ".luminosity_lower", String.valueOf(plant.luminosity().getThreshold()));
            requestMap.put(plantName  + ".humidity_lower", String.valueOf(plant.humidity().getThreshold()));
            requestMap.put(plantName  + ".temperature_high", String.valueOf(plant.temperature().getThreshold() + DEFAULT_TEMPERATURE_THRESHOLD_DIFF));
            requestMap.put(plantName  + ".luminosity_high", String.valueOf(plant.luminosity().getThreshold() + DEFAULT_LUMINOSITY_THRESHOLD_DIFF));
            requestMap.put(plantName  + ".humidity_high", String.valueOf(plant.humidity().getThreshold() + DEFAULT_HUMIDITY_THRESHOLD_DIFF));
        }

        JSONObject requestJson = JsonHandler.get().mapToJson(requestMap, plants.keySet().toArray(new String[]{}));
        try {
            URL url = new URL(baseUri + "/updateT");
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("PATCH");
            this.mountRequest(con, requestJson);
            System.out.println("Url: " + url);
            System.out.println(requestMap.toString());
            System.out.println(requestJson.toString());
            System.out.println(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void patchActuators() {
        Map<String, String> requestMap = new HashMap<>();
        for (Plant plant : plants.values()) {
            requestMap.put(plant.getName() + ".led1", plant.temperature().isActive() ? "1" : "0");
            requestMap.put(plant.getName() + ".led2", plant.humidity().isActive() ? "1" : "0");
            requestMap.put(plant.getName() + ".led3", plant.luminosity().isActive() ? "1" : "0");
        }

        JSONObject requestJson = JsonHandler.get().mapToJson(requestMap, plants.keySet().toArray(new String[]{}));
        try {
            URL url = new URL(baseUri + "/update");
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("PATCH");
            this.mountRequest(con, requestJson);
            System.out.println("Url: " + url);
            System.out.println(requestMap.toString());
            System.out.println(requestJson.toString());
            System.out.println(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject readResponse(HttpURLConnection con) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void mountRequest(HttpURLConnection con, JSONObject json) {
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}