package com.example.wateringfromtwitterapp.logic;

import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This is a singleton that handles backend logic,
 * from storing the Plants and updating their values
 * to handling the REST request and responses to the
 * RaspberryPi central.</p>
 * <p>The hostname used to access the server is
 * hardcoded here.
 * </p>
 *
 * @see Plant
 */
public class DataBroker {
    public static String baseUri = "http://20";

    private Map<String, Plant> plants = new HashMap<>();
    private static DataBroker instance = null;

    private DataBroker() {
    }

    /**
     * <p>The singleton's access point, with an old double-lock sync mechanism.</p>
     * <p>Upon creation, the inner ThreadPolicy is disable to avoid having to
     * create threads (otherwise, the current implementation of the system
     * would not be possible), even though it would be recommendable to go for
     * a safer approach.</p>
     *
     * @return The single instance of this class
     */
    public static DataBroker get() {
        if (instance == null) {
            synchronized (DataBroker.class) {
                if (instance == null) {
                    instance = new DataBroker();
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
            }
        }
        return instance;
    }

    /**
     * <p>Loads the data of the Plant with the given name.</p>
     * <p>If no such Plant exists, it is created first.</p>
     * <p>In either case, the indicators of user activated actuators
     * on the server are updated with the internal values,
     * since this is currently the only way to guarantee
     * the integrity of those indicators across the system
     * after the disconnection of the user in the Android.</p>
     * <p>Finally, the measurements, thresholds and the actual
     * state of the actuators of the Plant are updated with the
     * server's values.</p>
     *
     * @param plantName The new Plant's name
     * @return The Plant with the given name
     * @see Plant
     */
    public void loadPlant(String plantName) {
        if (!plants.containsKey(plantName)) {
            this.addPlant(plantName);
        }
        this.patchActuators();
        this.updateMeasurements(plantName);
    }

    /**
     * <p>Adds a new Plant instance if there isn't another one with the same name,
     * returning it afterwards.</p>
     * <p>If there's a plant with that name already stored, it is returned instead.</p>
     *
     * @param plantName The new Plant's name
     * @return The Plant with the given name
     * @see Plant
     */
    public Plant addPlant(String plantName) {
        plants.put(plantName, new Plant(plantName));
        return plants.get(plantName);
    }

    /**
     * <p>Returns the Plant with the given name, if it exists</p>
     * <p>If there's no plant with that name, returns null.</p>
     *
     * @param plantName The target Plant's name
     * @return The Plant with the given name if it exists, null otherwise
     * @see Plant
     */
    public Plant getPlant(String plantName) {
        if (plants.containsKey(plantName)) {
            return plants.get(plantName);
        }
        return null;
    }


    /**
     * <p>Toggles the state of the user activated led indicator, in this case
     * for the temperature</p>
     *
     * @param plantName Target Plant's name
     * @return The Plant with the given name
     * @see Plant
     */
    public Plant changeTemperatureActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.temperature().toggleForcedActive();
        this.patchActuators();
        return plant;
    }

    /**
     * <p>Toggles the state of the user activated led indicator, in this case
     * for the luminosity</p>
     *
     * @param plantName Target Plant's name
     * @return The Plant with the given name
     * @see Plant
     */
    public Plant changeLuminosityActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.luminosity().toggleForcedActive();
        this.patchActuators();
        return plant;
    }

    /**
     * <p>Toggles the state of the user activated led indicator, in this case
     * for the humidity</p>
     *
     * @param plantName Target Plant's name
     * @return The Plant with the given name
     * @see Plant
     */
    public Plant changeHumidityActuatorForcedFlag(String plantName) {
        Plant plant = plants.get(plantName);
        plant.humidity().toggleForcedActive();
        this.patchActuators();
        return plant;
    }

    /**
     * <p>Retrieves the current measured values of temperature, luminosity and humidity
     * of the Plant with the given name.</p>
     * <p>It then retrieves the thresholds and leds' states for each.</p>
     * <p>All of these values are obtained from the server with GETs, and finally used
     * to update the internal Plant's instance values.</p>
     *
     * @param plantName Target Plant's name
     * @return The Plant with the given name
     * @see Plant
     */
    public Plant updateMeasurements(String plantName) {
        Plant plant = plants.get(plantName);
        //Get values
        Map<String, String> responseMap = this.getResponse("/info");
        if (responseMap != null && !responseMap.isEmpty()) {
            System.out.println(responseMap.toString());
            System.out.println(plantName);
            plant.temperature().setValue(Double.parseDouble(responseMap.get(plantName + ".temperature")));
            plant.luminosity().setValue(Double.parseDouble(responseMap.get(plantName + ".luminosity")));
            plant.humidity().setValue(Double.parseDouble(responseMap.get(plantName + ".humidity")));
        }


        //Get thresholds
        responseMap = this.getResponse("/infoT");
        if (responseMap != null && !responseMap.isEmpty()) {
            plant.temperature().setUpperThreshold(Double.parseDouble(responseMap.get(plantName + ".temperature_upper")));
            plant.temperature().setLowerThreshold(Double.parseDouble(responseMap.get(plantName + ".temperature_lower")));
            plant.luminosity().setUpperThreshold(Double.parseDouble(responseMap.get(plantName + ".luminosity_upper")));
            plant.luminosity().setLowerThreshold(Double.parseDouble(responseMap.get(plantName + ".luminosity_lower")));
            plant.humidity().setUpperThreshold(Double.parseDouble(responseMap.get(plantName + ".humidity_upper")));
            plant.humidity().setLowerThreshold(Double.parseDouble(responseMap.get(plantName + ".humidity_lower")));
        }
        //Get actuator flags
        responseMap = this.getResponse("/infoL");
        if (responseMap != null && !responseMap.isEmpty()) {
            plant.humidity().setActive(responseMap.get(plantName + ".led1").equals("1"));
            plant.luminosity().setActive(responseMap.get(plantName + ".led2").equals("1"));
            plant.temperature().setActive(responseMap.get(plantName + ".led3").equals("1"));
        }
        return plants.get(plantName);
    }

    /**
     * <p>Updates the internal values of the Plant's actuators' thresholds</p>
     * <p>It then also updates these values on the server.</p>
     *
     * @param plantName                    Target Plant's name
     * @param newTemperatureUpperThreshold New upper threshold for the Plant's temperature actuator
     * @param newTemperatureLowerThreshold New lower threshold for the Plant's temperature actuator
     * @param newLuminosityUpperThreshold  New upper threshold for the Plant's luminosity actuator
     * @param newLuminosityLowerThreshold  New lower threshold for the Plant's luminosity actuator
     * @param newHumidityUpperThreshold    New upper threshold for the Plant's humidity actuator
     * @param newHumidityLowerThreshold    New lower threshold for the Plant's humidity actuator
     * @return The Plant with the given name
     * @see Plant
     */
    public Plant changeThresholds(String plantName,
                                  Double newTemperatureUpperThreshold, Double newTemperatureLowerThreshold,
                                  Double newLuminosityUpperThreshold, Double newLuminosityLowerThreshold,
                                  Double newHumidityUpperThreshold, Double newHumidityLowerThreshold) {
        Plant plant = plants.get(plantName);
        if (newTemperatureUpperThreshold != null) {
            plant.temperature().setUpperThreshold(newTemperatureUpperThreshold);
        }
        if (newTemperatureLowerThreshold != null) {
            plant.temperature().setLowerThreshold(newTemperatureLowerThreshold);
        }
        if (newLuminosityUpperThreshold != null) {
            plant.luminosity().setUpperThreshold(newLuminosityUpperThreshold);
        }
        if (newLuminosityLowerThreshold != null) {
            plant.luminosity().setLowerThreshold(newLuminosityLowerThreshold);
        }
        if (newHumidityUpperThreshold != null) {
            plant.humidity().setUpperThreshold(newHumidityUpperThreshold);
        }
        if (newHumidityLowerThreshold != null) {
            plant.humidity().setLowerThreshold(newHumidityLowerThreshold);
        }
        patchThreshold();
        return plant;
    }

    /**
     * <p>Retrieves the internal threshold values of each Plant and then makes a PATCH request to
     * update the server's values with those.</p>
     *
     * @see Plant
     */
    public void patchThreshold() {
        Map<String, String> requestMap = new HashMap<>();
        for (String plantName : plants.keySet()) {
            Plant plant = plants.get(plantName);
            requestMap.put(plantName + ".temperature_upper", String.valueOf(plant.temperature().getUpperThreshold()));
            requestMap.put(plantName + ".temperature_lower", String.valueOf(plant.temperature().getLowerThreshold()));
            requestMap.put(plantName + ".luminosity_upper", String.valueOf(plant.luminosity().getUpperThreshold()));
            requestMap.put(plantName + ".luminosity_lower", String.valueOf(plant.luminosity().getLowerThreshold()));
            requestMap.put(plantName + ".humidity_upper", String.valueOf(plant.humidity().getUpperThreshold()));
            requestMap.put(plantName + ".humidity_lower", String.valueOf(plant.humidity().getLowerThreshold()));
        }

        JSONObject jsonRequest = JsonHandler.get().mapToJson(requestMap, plants.keySet().toArray(new String[]{}));
        patchRequest("/updateT", jsonRequest);
    }

    /**
     * <p>Retrieves the internal user activated leds flags of each Plant and then makes a PATCH request to
     * update the server's values with those.</p>
     *
     * @see Plant
     */
    public void patchActuators() {
        Map<String, String> requestMap = new HashMap<>();
        for (Plant plant : plants.values()) {
            requestMap.put(plant.getName() + ".led1", plant.humidity().isForcedActive() ? "1" : "0");
            requestMap.put(plant.getName() + ".led2", plant.luminosity().isForcedActive() ? "1" : "0");
            requestMap.put(plant.getName() + ".led3", plant.temperature().isForcedActive() ? "1" : "0");
        }

        JSONObject jsonRequest = JsonHandler.get().mapToJson(requestMap, plants.keySet().toArray(new String[]{}));
        patchRequest("/update", jsonRequest);
    }

    /**
     * <p>Makes a GET request to the server, storing its JSON response as a Map of Strings</p>
     *
     * @param endpoint Endpoint of the GET request (without the hostname)
     * @return Map of Strings with the GET response's key/value pairs
     */
    private Map<String, String> getResponse(String endpoint) {
        try {
            URL url = new URL(baseUri + endpoint);
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setRequestMethod("GET");
            JSONObject json = this.readResponse(con);
            return JsonHandler.get().jsonToMap(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Makes a PATCH request to the server, using the given endpoint and JSON body.</p>
     *
     * @param endpoint    Endpoint of the GET request (without the hostname)
     * @param jsonRequest The JSONObject to be used as the request's body
     */
    private void patchRequest(String endpoint, JSONObject jsonRequest) {
        try {
            URL url = new URL(baseUri + endpoint);
            HttpURLConnection con = ((HttpURLConnection) url.openConnection());
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("PATCH");
            //System.out.println("Url: " + url);
            //System.out.println(jsonRequest.toString());
            this.mountRequest(con, jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Makes a request from which the response will be read.</p>
     * <p>The response is returned as a JSON object.</p>
     *
     * @param con Request to be used
     * @return Response as a JSON object
     */
    private JSONObject readResponse(HttpURLConnection con) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            //System.out.println(con.getResponseCode());
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Makes a request that sends data to the server.</p>
     * <p>The server's response is ignored.</p>
     *
     * @param con  Request to be used, without the body
     * @param json Request body to be used
     */
    private void mountRequest(HttpURLConnection con, JSONObject json) {
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
            System.out.println(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}