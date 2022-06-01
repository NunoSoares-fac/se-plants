package com.example.wateringfromtwitterapp;

public class Plant {
    private MeasuredVariable temperature;
    private MeasuredVariable luminosity;
    private MeasuredVariable humidity;

    Plant() {
        temperature = new MeasuredVariable();
        luminosity = new MeasuredVariable();
        humidity = new MeasuredVariable();
    }

    public MeasuredVariable temperature() {
        return temperature;
    }
    public MeasuredVariable luminosity() {
        return luminosity;
    }
    public MeasuredVariable humidity() {
        return humidity;
    }
}
