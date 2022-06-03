package com.example.wateringfromtwitterapp.logic;

import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.example.wateringfromtwitterapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DataBroker {
    public static String baseUri = "http://10.0.2.2:8081";

    private Map<String, Plant> plants = new HashMap<>();
    private static DataBroker instance = null;

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

    public void loadPlant(String plantName) {
        if (!plants.containsKey(plantName)) {
            this.addPlant(plantName);
        }
        this.patchActuators();
        this.updateMeasurements(plantName);
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
        Plant plant = plants.get(plantName);
        //Get values
        Map<String, String> responseMap = this.getResponse("/info");
        System.out.println(responseMap.toString());
        System.out.println(plantName);
        plant.temperature().setValue(Double.parseDouble(responseMap.get(plantName + ".temperature")));
        plant.luminosity().setValue(Double.parseDouble(responseMap.get(plantName + ".luminosity")));
        plant.humidity().setValue(Double.parseDouble(responseMap.get(plantName + ".humidity")));

        //Get thresholds
        responseMap = this.getResponse("/infoT");
        plant.temperature().setUpperThreshold(Double.parseDouble(responseMap.get(plantName + ".temperature_upper")));
        plant.temperature().setLowerThreshold(Double.parseDouble(responseMap.get(plantName + ".temperature_lower")));
        plant.luminosity().setUpperThreshold(Double.parseDouble(responseMap.get(plantName + ".luminosity_upper")));
        plant.luminosity().setLowerThreshold(Double.parseDouble(responseMap.get(plantName + ".luminosity_lower")));
        plant.humidity().setUpperThreshold(Double.parseDouble(responseMap.get(plantName + ".humidity_upper")));
        plant.humidity().setLowerThreshold(Double.parseDouble(responseMap.get(plantName + ".humidity_lower")));

        //Get actuator flags
        responseMap = this.getResponse("/infoL");
        plant.humidity().setActive(responseMap.get(plantName + ".led1").equals("1"));
        plant.luminosity().setActive(responseMap.get(plantName + ".led2").equals("1"));
        plant.temperature().setActive(responseMap.get(plantName + ".led3").equals("1"));
        return plants.get(plantName);
    }

    public Plant changeThresholds(String plantName,
                                  Double newTemperatureUpperThreshold, Double newTemperatureLowerThreshold,
                                  Double newLuminosityUpperThreshold, Double newLuminosityLowerThreshold,
                                  Double newHumidityUpperThreshold, Double newHumidityLowerThreshold) {
        Plant plant = plants.get(plantName);
        if (newTemperatureUpperThreshold != null) {
            plant.temperature().setUpperThreshold(newTemperatureUpperThreshold);
        }
        if (newTemperatureLowerThreshold != null) {
            plant.temperature().setLowerThreshold(newTemperatureLowerThreshold);
        }
        if (newLuminosityUpperThreshold != null) {
            plant.luminosity().setUpperThreshold(newLuminosityUpperThreshold);
        }
        if (newLuminosityLowerThreshold != null) {
            plant.luminosity().setLowerThreshold(newLuminosityLowerThreshold);
        }
        if (newHumidityUpperThreshold != null) {
            plant.humidity().setUpperThreshold(newHumidityUpperThreshold);
        }
        if (newHumidityLowerThreshold != null) {
            plant.humidity().setLowerThreshold(newHumidityLowerThreshold);
        }
        patchThreshold();
        patchActuators();
        return plant;
    }

    public void patchThreshold() {
        Map<String, String> requestMap = new HashMap<>();
        for (String plantName : plants.keySet()) {
            Plant plant = plants.get(plantName);
            requestMap.put(plantName + ".temperature_upper", String.valueOf(plant.temperature().getUpperThreshold()));
            requestMap.put(plantName + ".temperature_lower", String.valueOf(plant.temperature().getLowerThreshold()));
            requestMap.put(plantName + ".luminosity_upper", String.valueOf(plant.luminosity().getUpperThreshold()));
            requestMap.put(plantName + ".luminosity_lower", String.valueOf(plant.luminosity().getLowerThreshold()));
            requestMap.put(plantName + ".humidity_upper", String.valueOf(plant.humidity().getUpperThreshold()));
            requestMap.put(plantName + ".humidity_lower", String.valueOf(plant.humidity().getLowerThreshold()));
        }

        JSONObject jsonRequest = JsonHandler.get().mapToJson(requestMap, plants.keySet().toArray(new String[]{}));
        patchRequest("/updateT", jsonRequest);
    }

    public void patchActuators() {
        Map<String, String> requestMap = new HashMap<>();
        for (Plant plant : plants.values()) {
            requestMap.put(plant.getName() + ".led1", plant.humidity().isForcedActive() ? "1" : "0");
            requestMap.put(plant.getName() + ".led2", plant.luminosity().isForcedActive() ? "1" : "0");
            requestMap.put(plant.getName() + ".led3", plant.temperature().isForcedActive() ? "1" : "0");
        }

        JSONObject jsonRequest = JsonHandler.get().mapToJson(requestMap, plants.keySet().toArray(new String[]{}));
        patchRequest("/update", jsonRequest);
    }

    private Map<String,String> getResponse(String endpoint) {
        try {
            URL url = new URL(baseUri + endpoint);
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setRequestMethod("GET");
            JSONObject json = this.readResponse(con);
            return JsonHandler.get().jsonToMap(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void patchRequest(String endpoint, JSONObject jsonRequest) {
        try {
            URL url = new URL(baseUri + endpoint);
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("PATCH");
            System.out.println("Url: " + url);
            System.out.println(jsonRequest.toString());
            this.mountRequest(con, jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject readResponse(HttpURLConnection con) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            //System.out.println(con.getResponseCode());
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mountRequest(HttpURLConnection con, JSONObject json) {
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
            System.out.println(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}