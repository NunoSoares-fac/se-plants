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

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

            }
        }, 0, 3000);

        binding.checkboxTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = view.getRootView();
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
                Plant plant = DataBroker.get().changeTemperatureActuatorForcedFlag(plantName);
                TextView errorMessageTextView = (TextView) rootView.findViewById(R.id.error_message);
                errorMessageTextView.setText(null);

                boolean isActuatorActive = plant.temperature().isActive();
                String activeFlag;
                if (isActuatorActive) {
                    activeFlag = "true";
                } else {
                    activeFlag = "false";
                }
                ((TextView) rootView.findViewById(R.id.temperature_actuator)).setText(activeFlag);
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

                boolean isActuatorActive = plant.luminosity().isActive();
                String activeFlag;
                if (isActuatorActive) {
                    activeFlag = "true";
                } else {
                    activeFlag = "false";
                }
                ((TextView) rootView.findViewById(R.id.luminosity_actuator)).setText(activeFlag);
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

                boolean isActuatorActive = plant.humidity().isActive();
                String activeFlag;
                if (isActuatorActive) {
                    activeFlag = "true";
                } else {
                    activeFlag = "false";
                }
                ((TextView) rootView.findViewById(R.id.humidity_actuator)).setText(activeFlag);
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

                    String activeFlag;
                    if (plant.temperature().isActive()) {
                        activeFlag = "true";
                    } else {
                        activeFlag = "false";
                    }
                    ((TextView) rootView.findViewById(R.id.temperature_actuator)).setText(activeFlag);
                    ((TextView) rootView.findViewById(R.id.temperature_threshold)).setText(String.valueOf(plant.temperature().getThreshold()));

                    if (plant.luminosity().isActive()) {
                        activeFlag = "true";
                    } else {
                        activeFlag = "false";
                    }
                    ((TextView) rootView.findViewById(R.id.luminosity_actuator)).setText(activeFlag);
                    ((TextView) rootView.findViewById(R.id.luminosity_threshold)).setText(String.valueOf(plant.luminosity().getThreshold()));

                    if (plant.humidity().isActive()) {
                        activeFlag = "true";
                    } else {
                        activeFlag = "false";
                    }
                    ((TextView) rootView.findViewById(R.id.humidity_actuator)).setText(activeFlag);
                    ((TextView) rootView.findViewById(R.id.humidity_threshold)).setText(String.valueOf(plant.humidity().getThreshold()));

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
                TextView plantNameTextView = (TextView) rootView.findViewById(R.id.plant_name);
                String plantName = String.valueOf(plantNameTextView.getText());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
    }
}