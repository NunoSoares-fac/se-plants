package com.example.wateringfromtwitterapp.logic;

/**
 * <p>Stores a variable with a value, upper and lower thresholds, the respective actuator's state
 * and a boolean that indicates if the actuator has been activated by the user.</p>
 * <p>All members have getters and setters, though the user activated actuator indicator can only
 * be toggled, not changed to a specific value</p>
 * <p>By default, every the user activated actuator indicator starts as false, which will make the
 * app disabling these on the server side if they were activated before.
 * </p>
 */
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
