package com.example.wateringfromtwitterapp;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DataBroker {
    private Map<String, Plant> plants = new HashMap<>();

    private static DataBroker instance = null;

    private DataBroker() {}

    public static DataBroker get() {
        if (instance == null) {
            synchronized (DataBroker.class) {
                if (instance == null) {
                    instance = new DataBroker();
                }
            }
        }
        return instance;
    }

    public Plant getPlant(String plantName) {
        if (plants.containsKey(plantName)) {
            return plants.get(plantName);
        }
        return null;
    }

    public void loadPlant(String plantName) {
        if (!plants.containsKey(plantName)) {
            plants.put(plantName, new Plant());
            //this.plants.get(plantName).temperature().setThreshold();
            //this.plants.get(plantName).luminosity().setThreshold();
            //this.plants.get(plantName).humidity().setThreshold();
        }
        this.updateMeasurements(plantName);
    }

    public Plant changeTemperatureActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.temperature().toggleForcedActive();
        this.patchActuators(plant);
        return plant;
    }

    public Plant changeLuminosityActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.luminosity().toggleForcedActive();
        this.patchActuators(plant);
        return plant;
    }

    public Plant changeHumidityActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.humidity().toggleForcedActive();
        this.patchActuators(plant);
        return plant;
    }

    public Plant updateMeasurements(String plantName) {
        Plant plant = plants.get(plantName);
        //this.getMeasurements(plantName);
        return plant;
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
        this.patchActuators(plant);

        return plant;
    }
/*
    public Map<String, String> getMeasurements(Plant plant) {
        //TODO: Handle response here
        //Response response = RestAssured.get("")....
        //return
    }
*/
    public void patchThreshold(Plant plant) {
        //TODO: Handle request here

    }

    public void patchActuators(Plant plant) {
        //TODO: Handle request here

    }


}
