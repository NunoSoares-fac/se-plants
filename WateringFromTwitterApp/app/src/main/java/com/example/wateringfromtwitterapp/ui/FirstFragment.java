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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataBroker.get().loadPlant("plant1");
        DataBroker.get().loadPlant("plant2");
        FirstFragment.updateDisplayedValues(view, DataBroker.get().getPlant("plant1"));
        FirstFragment.updateCheckboxes(view, DataBroker.get().getPlant("plant1"));

        new Timer().scheduleAtFixedRate(new RefreshDataTask(view), 2000, 2000);

        binding.checkboxTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);
                Plant plant = DataBroker.get().changeTemperatureActuatorForcedFlag(plantName);

                updateDisplayedActiveFlag(rootView, plant);
            }
        });

        binding.checkboxLuminosity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);
                Plant plant = DataBroker.get().changeLuminosityActuatorForcedFlag(plantName);

                updateDisplayedActiveFlag(rootView, plant);
            }
        });

        binding.checkboxHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);
                Plant plant = DataBroker.get().changeHumidityActuatorForcedFlag(plantName);


                updateDisplayedActiveFlag(rootView, plant);
            }
        });

        binding.buttonChangeThresholds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);

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
                    Double newLuminosityUpperThreshold  = convertThresholdText(newLuminosityUpperThresholdText);
                    Double newLuminosityLowerThreshold = convertThresholdText(newLuminosityLowerThresholdText);
                    Double newHumidityUpperThreshold = convertThresholdText(newHumidityUpperThresholdText);
                    Double newHumidityLowerThreshold = convertThresholdText(newHumidityLowerThresholdText);

                    Plant plant = DataBroker.get().changeThresholds(plantName,
                                                                    newTemperatureUpperThreshold, newTemperatureLowerThreshold,
                                                                    newLuminosityUpperThreshold, newLuminosityLowerThreshold,
                                                                    newHumidityUpperThreshold, newHumidityLowerThreshold);
                    FirstFragment.updateDisplayedValues(rootView, plant);
                } catch (NumberFormatException e) {
                    errorMessageTextView.setText(R.string.error_invalid_threshold);
                }
            }
        });

        binding.buttonChangePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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

    public static void updateCheckboxes(View view, Plant plant) {
        ((CheckBox) view.findViewById(R.id.checkbox_temperature)).setChecked(plant.temperature().isForcedActive());
        ((CheckBox) view.findViewById(R.id.checkbox_luminosity)).setChecked(plant.luminosity().isForcedActive());
        ((CheckBox) view.findViewById(R.id.checkbox_humidity)).setChecked(plant.humidity().isForcedActive());
    }

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

