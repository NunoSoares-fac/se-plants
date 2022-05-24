package com.example.wateringfromtwitter;

public class Plant {
    private double temperature = -1;
    private double humidity = -1;
    private double luminosity = -1;
    private double luminosityThreshold = -1;
    private boolean isActuatorActive = false;

    public void updateMeasurements(double newTemperature, double newHumidity, double newLuminosity, boolean isActuatorActive) {
        this.temperature = newTemperature;
        this.humidity = newHumidity;
        this.luminosity = newLuminosity;
        this.isActuatorActive = isActuatorActive;
    }

    public void updateLuminosityThreshold(double newThreshold) {
        this.luminosityThreshold = newThreshold;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getLuminosity() {
        return luminosity;
    }

    public double getLuminosityThreshold() {
        return luminosityThreshold;
    }

    public boolean isActuatorActive() {
        return isActuatorActive;
    }
}
