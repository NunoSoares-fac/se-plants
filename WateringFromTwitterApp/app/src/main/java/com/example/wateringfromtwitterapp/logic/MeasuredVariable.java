package com.example.wateringfromtwitterapp.logic;

public class MeasuredVariable {
    private double value;
    private double upperThreshold;
    private double lowerThreshold;
    private boolean isActive;
    private boolean isForcedActive;

    public MeasuredVariable() {
        value = -1;
        upperThreshold = -1;
        lowerThreshold = -1;
        isActive = false;
        isForcedActive = false;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isForcedActive() {
        return isForcedActive;
    }

    public void toggleForcedActive() {
        isForcedActive = !isForcedActive;
    }

    public double getUpperThreshold() {
        return upperThreshold;
    }

    public void setUpperThreshold(double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public double getLowerThreshold() {
        return lowerThreshold;
    }

    public void setLowerThreshold(double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }
}
