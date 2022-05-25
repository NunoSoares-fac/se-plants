package com.example.wateringfromtwitterapp;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DataBroker {
    private Plant plant;

    DataBroker(Plant plant) {
        this.plant = plant;
    }

    public void getMeasurements() {
        //TODO: Handle response here
        //Response response = RestAssured.get("")....
        //this.updateMeasurements(...);
    }

    public void patchThreshold(String newThreshold) {
        //TODO: Handle request here
        //this.updateMeasurements(...);
    }

    public void updateMeasurements(double temperature, double humidity, double luminosity, double luminosityThreshold, int isActuatorActive) {
        boolean actuatorFlag = true;
        if (isActuatorActive == 0) {
            actuatorFlag = false;
        }
        plant.updateMeasurements(temperature, humidity, luminosity, actuatorFlag);
        plant.updateLuminosityThreshold(luminosityThreshold);
    }
}
