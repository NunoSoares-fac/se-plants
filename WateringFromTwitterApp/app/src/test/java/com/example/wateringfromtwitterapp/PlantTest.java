package com.example.wateringfromtwitterapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class PlantTest {
    final private static double DOUBLES_COMPARISON_TOLERANCE = 0.001;
    final private static double DEFAULT_TEMPERATURE = 30;
    final private static double DEFAULT_HUMIDITY = 50.5;
    final private static double DEFAULT_LUMINOSITY = 10;
    final private static double DEFAULT_THRESHOLD = 30;
    final private static boolean DEFAULT_ACTUATOR_FLAG = true;

    @Test
    public void testUpdateMeasurements() {
        Plant plant = new Plant();
        plant.updateMeasurements(DEFAULT_TEMPERATURE, DEFAULT_HUMIDITY, DEFAULT_LUMINOSITY, DEFAULT_ACTUATOR_FLAG);

        assertEquals(DEFAULT_TEMPERATURE, plant.getTemperature(), DOUBLES_COMPARISON_TOLERANCE);
        assertEquals(DEFAULT_HUMIDITY, plant.getHumidity(), DOUBLES_COMPARISON_TOLERANCE);
        assertEquals(DEFAULT_LUMINOSITY, plant.getLuminosity(), DOUBLES_COMPARISON_TOLERANCE);
        assertEquals(DEFAULT_ACTUATOR_FLAG, plant.isActuatorActive());
    }

    @Test
    public void testUpdateLuminosityThreshold() {
        Plant plant = new Plant();
        plant.updateMeasurements(DEFAULT_TEMPERATURE, DEFAULT_HUMIDITY, DEFAULT_LUMINOSITY, DEFAULT_ACTUATOR_FLAG);
        plant.updateLuminosityThreshold(DEFAULT_THRESHOLD);

        assertEquals(DEFAULT_THRESHOLD, plant.getLuminosityThreshold(), DOUBLES_COMPARISON_TOLERANCE);
    }
}