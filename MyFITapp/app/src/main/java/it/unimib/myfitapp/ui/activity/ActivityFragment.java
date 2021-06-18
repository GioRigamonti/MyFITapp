package it.unimib.myfitapp.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import it.unimib.myfitapp.R;

import static android.content.Context.MODE_PRIVATE;

public class ActivityFragment extends Fragment {

    private ActivityViewModel activityViewModel;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    TextView onActivity;
    Button startButton;
    boolean start = false;
    SensorManager sensorManager;
    TextView steps;
    private double MagnitudePrevious = 0;
    private int stepCount = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        /*final TextView textView = root.findViewById(R.id.text_daily_activity);
        activityViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        onActivity = root.findViewById(R.id.text_start_activity);
        steps = root.findViewById(R.id.textView_num_piedi);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent != null){
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration +
                            y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude-MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 6){
                        stepCount++;
                    }
                    steps.setText(Integer.toString(stepCount));
                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        startButton = (Button) root.findViewById(R.id.button_start_activity);
        startButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        startButton.setOnClickListener(mButton);

        barChart = (BarChart) root.findViewById(R.id.barchart_activity);
        getBarEntries();
        setBarData(barEntriesArrayList,getResources().getString(R.string.activity_done_daily));

        return root;
    }
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences =  this.getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences =  this.getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences =  this.getActivity().getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }



    private void setBarData(ArrayList barEntriesArrayList, String string) {
        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, string);

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(Color.MAGENTA);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
    }


    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(new BarEntry(1f, 4));
        barEntriesArrayList.add(new BarEntry(2f, 6));
        barEntriesArrayList.add(new BarEntry(3f, 8));
        barEntriesArrayList.add(new BarEntry(4f, 2));
        barEntriesArrayList.add(new BarEntry(5f, 4));
        barEntriesArrayList.add(new BarEntry(6f, 1));
        barEntriesArrayList.add(new BarEntry(7f, 1));
    }
    final View.OnClickListener mButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // change your button background
            if (!start) {
                v.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                onActivity.setText(getResources().getString(R.string.start_activity));

            } else {
                // v.cancel();
                v.setBackgroundResource(R.drawable.ic_baseline_stop_24);
                //v.vibrate(50000);
                onActivity.setText("Stop activity");
            }
            start = !start; // reverse
        }
    };


}
