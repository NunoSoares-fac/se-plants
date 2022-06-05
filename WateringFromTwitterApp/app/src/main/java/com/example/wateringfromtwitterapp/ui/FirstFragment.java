package com.example.wateringfromtwitterapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wateringfromtwitterapp.R;
import com.example.wateringfromtwitterapp.databinding.FragmentFirstBinding;
import com.example.wateringfromtwitterapp.logic.DataBroker;
import com.example.wateringfromtwitterapp.logic.Plant;

import java.util.Timer;

/**
 * <p>This class is the centerpiece of the UI where all of the functionality provided by the system
 * can be accessed by a user.</p>
 *
 * @see Plant
 */
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        DataBroker.get().addPlant("plant1");
        DataBroker.get().addPlant("plant2");
        return binding.getRoot();
    }

    /**
     * <p>This method creates the main and only view, loading the two Plants' data and changing
     * the display to one of the plants, referred to as "plant1"</p>
     * <p>Additionally, the UI refresh task is started here, beginning with a delay of 2 seconds
     * and an interval of 2 seconds as well.</p>
     * <p>All interactable elements in the UI have their event listeners initialised here as well.</p>
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataBroker.get().loadPlant("plant2");
        DataBroker.get().loadPlant("plant1");
        FirstFragment.updateDisplayedValues(view, DataBroker.get().getPlant("plant1"));
        FirstFragment.updateCheckboxes(view, DataBroker.get().getPlant("plant1"));

        new Timer().scheduleAtFixedRate(new RefreshDataTask(view), 2000, 2000);

        binding.checkboxTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.updateCheckboxTemperature(view);
            }
        });

        binding.checkboxLuminosity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.updateCheckboxLuminosity(view);
            }
        });

        binding.checkboxHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.updateCheckboxHumidity(view);
            }
        });

        binding.buttonChangeThresholds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.updateThresholds(view);
            }
        });

        binding.buttonChangePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.changePlant(view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * <p>When an action begins in the UI, the first thing to do is to retrieve the current displayed
     * Plant's name and to wipe any error messages from the view.</p>
     *
     * @return The displayed Plant's name
     */
    public static String startAction(View view) {
        TextView plantNameTextView = (TextView) view.findViewById(R.id.plant_name);
        String plantName = String.valueOf(plantNameTextView.getText());
        TextView errorMessageTextView = (TextView) view.findViewById(R.id.error_message);
        errorMessageTextView.setText(null);
        return plantName;
    }

    /**
     * <p>Triggers the update of the user activated led indicator related to temperature, both in
     * backend (internal values and server values) and in the UI of the displayed Plant.</p>
     */
    public static void updateCheckboxTemperature(View view) {
        View rootView = view.getRootView();
        String plantName = FirstFragment.startAction(rootView);
        Plant plant = DataBroker.get().changeTemperatureActuatorForcedFlag(plantName);
        updateDisplayedActiveFlag(rootView, plant);
    }

    /**
     * <p>Triggers the update of the user activated led indicator related to luminosity, both in
     * backend (internal values and server values)  and in the UI of the displayed Plant.</p>
     */
    public static void updateCheckboxLuminosity(View view) {
        View rootView = view.getRootView();
        String plantName = FirstFragment.startAction(rootView);
        Plant plant = DataBroker.get().changeLuminosityActuatorForcedFlag(plantName);
        updateDisplayedActiveFlag(rootView, plant);
    }

    /**
     * <p>Triggers the update of the user activated led indicator related to humidity, both in
     * backend (internal values and server values) and in the UI of the displayed Plant.</p>
     */
    public static void updateCheckboxHumidity(View view) {
        View rootView = view.getRootView();
        String plantName = FirstFragment.startAction(rootView);
        Plant plant = DataBroker.get().changeHumidityActuatorForcedFlag(plantName);
        updateDisplayedActiveFlag(rootView, plant);
    }

    /**
     * <p>Triggers the update of the actuators' thresholds in the Plant instance and server with the values given by
     * the user through the UI of the displayed Plant.</p>
     */
    public static void updateThresholds(View view) {
        View rootView = view.getRootView();
        String plantName = FirstFragment.startAction(rootView);
        try {
            View temperatureThresholdTable = rootView.findViewById(R.id.temperature_layout);
            View luminosityThresholdTable = rootView.findViewById(R.id.luminosity_layout);
            View humidityThresholdTable = rootView.findViewById(R.id.humidity_layout);

            Editable newTemperatureUpperThresholdText = ((EditText) temperatureThresholdTable.findViewById(R.id.input_upper)).getText();
            Editable newTemperatureLowerThresholdText = ((EditText) temperatureThresholdTable.findViewById(R.id.input_lower)).getText();
            Editable newLuminosityUpperThresholdText = ((EditText) luminosityThresholdTable.findViewById(R.id.input_upper)).getText();
            Editable newLuminosityLowerThresholdText = ((EditText) luminosityThresholdTable.findViewById(R.id.input_lower)).getText();
            Editable newHumidityUpperThresholdText = ((EditText) humidityThresholdTable.findViewById(R.id.input_upper)).getText();
            Editable newHumidityLowerThresholdText = ((EditText) humidityThresholdTable.findViewById(R.id.input_lower)).getText();

            Double newTemperatureUpperThreshold = convertThresholdText(newTemperatureUpperThresholdText);
            Double newTemperatureLowerThreshold = convertThresholdText(newTemperatureLowerThresholdText);
            Double newLuminosityUpperThreshold = convertThresholdText(newLuminosityUpperThresholdText);
            Double newLuminosityLowerThreshold = convertThresholdText(newLuminosityLowerThresholdText);
            Double newHumidityUpperThreshold = convertThresholdText(newHumidityUpperThresholdText);
            Double newHumidityLowerThreshold = convertThresholdText(newHumidityLowerThresholdText);

            Plant plant = DataBroker.get().changeThresholds(plantName,
                    newTemperatureUpperThreshold, newTemperatureLowerThreshold,
                    newLuminosityUpperThreshold, newLuminosityLowerThreshold,
                    newHumidityUpperThreshold, newHumidityLowerThreshold);
            FirstFragment.updateDisplayedValues(rootView, plant);
        } catch (NumberFormatException e) {
            ((TextView) view.findViewById(R.id.error_message)).setText(R.string.error_invalid_threshold);
        }
    }

    /**
     * <p>Updates the data in the UI to display the data of the Plant with the given name in the
     * respective text box where it is prompted.</p>
     */
    public static void changePlant(View view) {
        View rootView = view.getRootView();
        TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
        errorMessageTextView.setText(null);

        //Change Plant
        Editable newPlantNameText = ((EditText) rootView.findViewById(R.id.input_change_plant)).getText();
        if (newPlantNameText == null || newPlantNameText.toString().trim().equals("") || DataBroker.get().getPlant(newPlantNameText.toString().trim()) == null) {
            errorMessageTextView.setText(R.string.error_invalid_plant_name);
        } else {
            String newPlantName = newPlantNameText.toString().trim();
            Plant plant = DataBroker.get().getPlant(newPlantName);
            FirstFragment.updateDisplayedValues(rootView, plant);
            FirstFragment.updateCheckboxes(rootView, plant);
        }
    }

    /**
     * <p>Updates the data in the UI, from measured values to
     * thresholds and led states, with the data of the given Plant instance</p>
     */
    public static void updateDisplayedValues(View view, Plant plant) {
        ((TextView) view.findViewById(R.id.plant_name)).setText(plant.getName());
        String temperature = String.valueOf(plant.temperature().getValue());
        String luminosity = String.valueOf(plant.luminosity().getValue());
        String humidity = String.valueOf(plant.humidity().getValue());
        ((TextView) view.findViewById(R.id.temperature)).setText(temperature);
        ((TextView) view.findViewById(R.id.luminosity)).setText(luminosity);
        ((TextView) view.findViewById(R.id.humidity)).setText(humidity);

        String temperatureUpperThreshold = String.valueOf(plant.temperature().getUpperThreshold());
        String temperatureLowerThreshold = String.valueOf(plant.temperature().getLowerThreshold());
        String luminosityUpperThreshold = String.valueOf(plant.luminosity().getUpperThreshold());
        String luminosityLowerThreshold = String.valueOf(plant.luminosity().getLowerThreshold());
        String humidityUpperThreshold = String.valueOf(plant.humidity().getUpperThreshold());
        String humidityLowerThreshold = String.valueOf(plant.humidity().getLowerThreshold());

        ((TextView) view.findViewById(R.id.temperature_layout).findViewById(R.id.current_upper)).setText(temperatureUpperThreshold);
        ((TextView) view.findViewById(R.id.temperature_layout).findViewById(R.id.current_lower)).setText(temperatureLowerThreshold);
        ((TextView) view.findViewById(R.id.luminosity_layout).findViewById(R.id.current_upper)).setText(luminosityUpperThreshold);
        ((TextView) view.findViewById(R.id.luminosity_layout).findViewById(R.id.current_lower)).setText(luminosityLowerThreshold);
        ((TextView) view.findViewById(R.id.humidity_layout).findViewById(R.id.current_upper)).setText(humidityUpperThreshold);
        ((TextView) view.findViewById(R.id.humidity_layout).findViewById(R.id.current_lower)).setText(humidityLowerThreshold);

        FirstFragment.updateDisplayedActiveFlag(view, plant);
    }

    /**
     * <p>Updates the data in the UI with the data concerning led states,
     * related to the data of the given Plant instance.</p>
     */
    public static void updateDisplayedActiveFlag(View view, Plant plant) {
        String activeFlag;
        if (plant.temperature().isActive()) {
            activeFlag = "true";
        } else {
            activeFlag = "false";
        }
        ((TextView) view.findViewById(R.id.temperature_actuator)).setText(activeFlag);

        if (plant.luminosity().isActive()) {
            activeFlag = "true";
        } else {
            activeFlag = "false";
        }
        ((TextView) view.findViewById(R.id.luminosity_actuator)).setText(activeFlag);

        if (plant.humidity().isActive()) {
            activeFlag = "true";
        } else {
            activeFlag = "false";
        }
        ((TextView) view.findViewById(R.id.humidity_actuator)).setText(activeFlag);
    }

    /**
     * <p>Updates the data in the UI with the data concerning user activated led indicators,
     * related to the data of the given Plant instance.</p>
     */
    public static void updateCheckboxes(View view, Plant plant) {
        ((CheckBox) view.findViewById(R.id.checkbox_temperature)).setChecked(plant.temperature().isForcedActive());
        ((CheckBox) view.findViewById(R.id.checkbox_luminosity)).setChecked(plant.luminosity().isForcedActive());
        ((CheckBox) view.findViewById(R.id.checkbox_humidity)).setChecked(plant.humidity().isForcedActive());
    }

    /**
     * <p>A helper method that obtains text from an editable element's text that concerns the threshold
     * value of a measured variable, returning null if the content does not exist or is equivalent
     * to an empty String, otherwise converting the text to a Double an returning it in the new format
     * </p>
     */
    private static Double convertThresholdText(Editable thresholdText) throws NumberFormatException {
        if (thresholdText != null) {
            String threshold = thresholdText.toString().trim();
            if (!threshold.equals("")) {
                return Double.parseDouble(threshold);
            }
        }
        return null;
    }
}

