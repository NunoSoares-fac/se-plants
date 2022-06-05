package com.example.wateringfromtwitterapp.ui;

import android.view.View;
import android.widget.TextView;

import com.example.wateringfromtwitterapp.R;
import com.example.wateringfromtwitterapp.logic.DataBroker;
import com.example.wateringfromtwitterapp.logic.Plant;

import java.util.TimerTask;

/**
 * <p>This class has a placeholder for the UI update task, which must be scheduled and repeated.</p>
 * <p>When this instance is created, it stores the Root view, to then access the necessary UI
 * elements, starting from the one where the name of currently displayed Plant's is.</p>
 *
 * @see FirstFragment
 * @see Plant
 */
public class RefreshDataTask extends TimerTask {

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
