package com.example.wateringfromtwitterapp;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wateringfromtwitterapp.databinding.FragmentFirstBinding;

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

        new Timer().scheduleAtFixedRate(new RefreshDataTask(view), 3000, 2000);

        binding.checkboxTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                Plant plant = DataBroker.get().changeTemperatureActuatorForcedFlag(plantName);
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);

                updateDisplayedActiveFlag(rootView, plant);
            }
        });

        binding.checkboxLuminosity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                Plant plant = DataBroker.get().changeLuminosityActuatorForcedFlag(plantName);
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);

                updateDisplayedActiveFlag(rootView, plant);
            }
        });

        binding.checkboxHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                Plant plant = DataBroker.get().changeHumidityActuatorForcedFlag(plantName);
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);

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
                    Editable newTemperatureThresholdText = ((EditText) rootView.findViewById(R.id.input_temperature_threshold)).getText();
                    String newTemperatureThreshold = null;
                    if (newTemperatureThresholdText != null) {
                        newTemperatureThreshold = newTemperatureThresholdText.toString().trim();
                        if (newTemperatureThreshold.equals("")) {
                            newTemperatureThreshold = null;
                        } else {
                            Double.parseDouble(newTemperatureThreshold);
                        }
                    }
                    Editable newLuminosityThresholdText = ((EditText) rootView.findViewById(R.id.input_luminosity_threshold)).getText();
                    String newLuminosityThreshold = null;
                    if (newLuminosityThresholdText != null) {
                        newLuminosityThreshold = newLuminosityThresholdText.toString().trim();
                        if (newLuminosityThreshold.equals("")) {
                            newLuminosityThreshold = null;
                        } else {
                            Double.parseDouble(newLuminosityThreshold);
                        }
                    }
                    Editable newHumidityThresholdText = ((EditText) rootView.findViewById(R.id.input_humidity_threshold)).getText();
                    String newHumidityThreshold = null;
                    if (newHumidityThresholdText != null) {
                        newHumidityThreshold = newHumidityThresholdText.toString().trim();
                        if (newHumidityThreshold.equals("")) {
                            newHumidityThreshold = null;
                        } else {
                            Double.parseDouble(newHumidityThreshold);
                        }
                    }

                    Plant plant = DataBroker.get().changeThresholds(plantName, newTemperatureThreshold, newLuminosityThreshold, newHumidityThreshold);

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
                if (newPlantNameText == null || newPlantNameText.toString().trim().equals("")) {
                    errorMessageTextView.setText(R.string.error_invalid_plant_name);
                } else {
                    String newPlantName = newPlantNameText.toString().trim();
                    FirstFragment.updateDisplayedValues(rootView, DataBroker.get().getPlant(newPlantName));
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

        String temperatureThreshold = String.valueOf(plant.temperature().getThreshold());
        String luminosityThreshold = String.valueOf(plant.luminosity().getThreshold());
        String humidityThreshold = String.valueOf(plant.humidity().getThreshold());
        ((TextView) view.findViewById(R.id.temperature_threshold)).setText(temperatureThreshold);
        ((TextView) view.findViewById(R.id.luminosity_threshold)).setText(luminosityThreshold);
        ((TextView) view.findViewById(R.id.humidity_threshold)).setText(humidityThreshold);

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
}

class RefreshDataTask extends TimerTask {

    private View rootView;

    public RefreshDataTask(View view) {
        this.rootView = view;
    }

    @Override
    public void run() {
        TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
        String plantName = String.valueOf(plantNameTextView.getText());
        Plant plant = DataBroker.get().updateMeasurements(plantName);

        //Update UI
        FirstFragment.updateDisplayedValues(rootView, plant);
    }
}