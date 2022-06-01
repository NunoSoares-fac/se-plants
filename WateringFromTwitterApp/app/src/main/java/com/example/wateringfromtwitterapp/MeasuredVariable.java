package com.example.wateringfromtwitterapp;

public class MeasuredVariable {
    private double value;
    private double threshold;
    private boolean isActive;
    private boolean isForcedActive;

    public MeasuredVariable() {
        value = -1;
        threshold = -1;
        isActive = false;
        isForcedActive = false;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
        if (value < threshold) {
            isActive = true;
        } else if (!isForcedActive) {
            isActive = false;
        }
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
        if (!isForcedActive) {
            if (value < threshold) {
                isActive = true;
            } else {
                isActive = false;
            }
        } else {
            isActive = true;
        }
    }
}
