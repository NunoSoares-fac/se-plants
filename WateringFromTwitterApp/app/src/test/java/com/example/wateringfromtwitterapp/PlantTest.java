package com.example.wateringfromtwitterapp;

import static org.junit.Assert.assertEquals;

import com.example.wateringfromtwitterapp.logic.Plant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 */
@RunWith(JUnit4.class)
public class PlantTest {
    final private static double DOUBLES_COMPARISON_TOLERANCE = 0.001;
    final private static double DEFAULT_TEMPERATURE = 30;
    final private static double DEFAULT_HUMIDITY = 50.5;
    final private static double DEFAULT_LUMINOSITY = 10;
    final private static double DEFAULT_THRESHOLD = 30;

    @Test
    public void testUpdateMeasurements() {
    }

    @Test
    public void testUpdateLuminosityThreshold() {
    }
}