package com.example.wateringfromtwitterapp;

import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DataBroker {
    private Map<String, Plant> plants = new HashMap<>();

    private static DataBroker instance = null;
    private final static RequestSpecBuilder HTTP_REQUEST_BUILDER = (new RequestSpecBuilder()).addHeader("Content-Type", "application/json");
    private final static double DEFAULT_TEMPERATURE_THRESHOLD_DIFF = 2;
    private final static double DEFAULT_LUMINOSITY_THRESHOLD_DIFF = 30;
    private final static double DEFAULT_HUMIDITY_THRESHOLD_DIFF = 5;

    private DataBroker() {}

    public static DataBroker get() {
        if (instance == null) {
            synchronized (DataBroker.class) {
                if (instance == null) {
                    instance = new DataBroker();
                    RestAssured.baseURI = "http://192.168.0.42";
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
        Plant plant1 = plants.get("plant1"), plant2 = plants.get("plant2");
        //Get values
        Map<String, String> responseMap = this.getMeasurements();
        plant1.temperature().setValue(Double.parseDouble(responseMap.get("plant1.temperature")));
        plant1.luminosity().setValue(Double.parseDouble(responseMap.get("plant1.luminosity")));
        plant1.humidity().setValue(Double.parseDouble(responseMap.get("plant1.humidity")));
        plant2.temperature().setValue(Double.parseDouble(responseMap.get("plant2.temperature")));
        plant2.luminosity().setValue(Double.parseDouble(responseMap.get("plant2.luminosity")));
        plant2.humidity().setValue(Double.parseDouble(responseMap.get("plant2.humidity")));
        //Get thresholds
        responseMap = this.getThresholds();
        plant1.temperature().setThreshold(Double.parseDouble(responseMap.get("plant1.temperature_lower")));
        plant1.luminosity().setThreshold(Double.parseDouble(responseMap.get("plant1.luminosity_lower")));
        plant1.humidity().setThreshold(Double.parseDouble(responseMap.get("plant1.humidity_lower")));
        plant2.temperature().setThreshold(Double.parseDouble(responseMap.get("plant2.temperature_lower")));
        plant2.luminosity().setThreshold(Double.parseDouble(responseMap.get("plant2.luminosity_lower")));
        plant2.humidity().setThreshold(Double.parseDouble(responseMap.get("plant2.humidity_lower")));
        //Get actuator flags
        responseMap = this.getActiveFlags();
        plant1.temperature().setActive(responseMap.get("plant1.led1").equals("1"));
        plant1.luminosity().setActive(responseMap.get("plant1.led3").equals("1"));
        plant1.humidity().setActive(responseMap.get("plant1.led2").equals("1"));
        plant2.temperature().setActive(responseMap.get("plant2.led1").equals("1"));
        plant2.luminosity().setActive(responseMap.get("plant2.led3").equals("1"));
        plant2.humidity().setActive(responseMap.get("plant2.led2").equals("1"));
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
        Response response = HTTP_REQUEST_BUILDER.build().get(RestAssured.baseURI + "/info");
        response.then().statusCode(200);
        String responseBody = response.body().toString();
        JSONObject json;
        try {
            json = new JSONObject(responseBody);
        } catch (Exception e) {
            System.out.println("ERROR:\n\n" + responseBody);
            return null;
        }
        return JsonHandler.get().jsonToMap(json);
    }

    public Map<String, String> getThresholds() {
        Response response = HTTP_REQUEST_BUILDER.build().get(RestAssured.baseURI + "/infoT");
        response.then().statusCode(200);
        String responseBody = response.body().toString();
        JSONObject json;
        try {
            json = new JSONObject(responseBody);
        } catch (Exception e) {
            System.out.println("ERROR:\n\n" + responseBody);
            return null;
        }
        return JsonHandler.get().jsonToMap(json);
    }

    public Map<String, String> getActiveFlags() {
        Response response = HTTP_REQUEST_BUILDER.build().get(RestAssured.baseURI + "/infoL");
        response.then().statusCode(200);
        String responseBody = response.body().toString();
        JSONObject json;
        try {
            json = new JSONObject(responseBody);
        } catch (Exception e) {
            System.out.println("ERROR:\n\n" + responseBody);
            return null;
        }
        return JsonHandler.get().jsonToMap(json);
    }

    public void patchThreshold() {
        Map<String, String> requestMap = new HashMap<>();
        Plant plant1 = plants.get("plant1"), plant2 = plants.get("plant2");
        requestMap.put("plant1.temperature_lower", String.valueOf(plant1.temperature().getThreshold()));
        requestMap.put("plant1.luminosity_lower", String.valueOf(plant1.luminosity().getThreshold()));
        requestMap.put("plant1.humidity_lower", String.valueOf(plant1.humidity().getThreshold()));
        requestMap.put("plant1.temperature_high", String.valueOf(plant1.temperature().getThreshold() + DEFAULT_TEMPERATURE_THRESHOLD_DIFF));
        requestMap.put("plant1.luminosity_high", String.valueOf(plant1.luminosity().getThreshold() + DEFAULT_LUMINOSITY_THRESHOLD_DIFF));
        requestMap.put("plant1.humidity_high", String.valueOf(plant1.humidity().getThreshold() + DEFAULT_HUMIDITY_THRESHOLD_DIFF));
        requestMap.put("plant2.temperature_lower", String.valueOf(plant2.temperature().getThreshold()));
        requestMap.put("plant2.luminosity_lower", String.valueOf(plant2.luminosity().getThreshold()));
        requestMap.put("plant2.humidity_lower", String.valueOf(plant2.humidity().getThreshold()));
        requestMap.put("plant2.temperature_high", String.valueOf(plant2.temperature().getThreshold() + DEFAULT_TEMPERATURE_THRESHOLD_DIFF));
        requestMap.put("plant2.luminosity_high", String.valueOf(plant2.luminosity().getThreshold() + DEFAULT_LUMINOSITY_THRESHOLD_DIFF));
        requestMap.put("plant2.humidity_high", String.valueOf(plant2.humidity().getThreshold() + DEFAULT_HUMIDITY_THRESHOLD_DIFF));
        JSONObject requestJson = JsonHandler.get().mapToJson(requestMap);

        Response response = HTTP_REQUEST_BUILDER.setBody(requestJson.toString()).build().patch(RestAssured.baseURI + "/updateT");
        response.then().statusCode(200);
    }

    public void patchActuators() {
        Map<String, String> requestMap = new HashMap<>();
        Plant plant1 = plants.get("plant1"), plant2 = plants.get("plant2");
        requestMap.put("plant1.led1", String.valueOf(plant1.temperature().getThreshold()));
        requestMap.put("plant1.led2", String.valueOf(plant1.humidity().getThreshold()));
        requestMap.put("plant1.led3", String.valueOf(plant1.luminosity().getThreshold()));
        requestMap.put("plant2.led1", String.valueOf(plant2.temperature().getThreshold()));
        requestMap.put("plant2.led2", String.valueOf(plant2.humidity().getThreshold()));
        requestMap.put("plant2.led3", String.valueOf(plant2.luminosity().getThreshold()));

        JSONObject requestJson = JsonHandler.get().mapToJson(requestMap);
        Response response = HTTP_REQUEST_BUILDER.setBody(requestJson.toString()).build().patch(RestAssured.baseURI + "/update");
        response.then().statusCode(200);
    }
}
