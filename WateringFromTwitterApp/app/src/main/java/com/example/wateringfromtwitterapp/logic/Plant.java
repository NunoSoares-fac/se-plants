package com.example.wateringfromtwitterapp.logic;

public class Plant {
    private MeasuredVariable temperature;
    private MeasuredVariable luminosity;
    private MeasuredVariable humidity;
    private final String plantName;

    Plant(String plantName) {
        temperature = new MeasuredVariable();
        luminosity = new MeasuredVariable();
        humidity = new MeasuredVariable();
        this.plantName = plantName;
    }

    public MeasuredVariable temperature() {return temperature;}

    public MeasuredVariable luminosity() {
        return luminosity;
    }

    public MeasuredVariable humidity() {
        return humidity;
    }

    public String getName() {
        return plantName;
    }

}
