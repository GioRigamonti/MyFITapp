package it.unimib.myfitapp.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unimib.myfitapp.PerformanceDao;
import it.unimib.myfitapp.PerformanceDatabase;
import it.unimib.myfitapp.PerformanceRegistration;
import it.unimib.myfitapp.R;
import static android.content.Context.MODE_PRIVATE;
import static java.time.DayOfWeek.SUNDAY;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityFragment extends Fragment {

    private ActivityViewModel activityViewModel;
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;
    private ArrayList barEntriesArrayList;
    private TextView onActivity;
    private Button startButton;
    boolean start = false;
    private SensorManager sensorManager;
    private TextView steps;
    private double MagnitudePrevious = 0;
    private int stepCount = 0;
    private TextView timerText;
    private Timer timer;
    private TimerTask timerTask;
    private int time = 0; // misurazione del tempo in s
    DayOfWeek day = LocalDate.now().getDayOfWeek();
    private PerformanceDatabase db;
    private PerformanceRegistration performanceRegistration;
    private SensorEventListener stepDetector;

    private PerformanceDatabase getDatabaseManager()
    {
        if (db==null)
            db = PerformanceDatabase.buildDatabase(this.getContext());
            db=PerformanceDatabase.getInMemoryDatabase(this.getContext());
        return db;
    }




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
        steps = root.findViewById(R.id.textView_num_passi);
        timerText = (TextView) root.findViewById(R.id.textView_timer);
        timer = new Timer();
        timerText.setText(getResources().getString(R.string.timer));
        startButton = (Button) root.findViewById(R.id.button_start_activity);
        startButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        steps.setText(Integer.toString(stepCount));
        /*AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(),
                    AppDatabase.class, "attività settimanale").build();
        PerformanceDao performanceDao = db.performanceDao();*/
        //List<Performance> performanceList = performanceDao.getAll();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change your button background
                if (!start) {
                    // v.cancel();
                    v.setBackgroundResource(R.drawable.ic_baseline_stop_24);
                    //v.vibrate(50000);
                    onActivity.setText(getResources().getString(R.string.stop));
                    stepCount = 0;
                    startTimer();
                    sensorManager  = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
                    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    stepDetector = new SensorEventListener() {
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

                            }
                            steps.setText(Integer.toString(stepCount));

                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        }
                    };
                    sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    v.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    onActivity.setText(getResources().getString(R.string.start_activity));
                    sensorManager.unregisterListener(stepDetector);
                    timerTask.cancel();
                    performanceRegistration = new PerformanceRegistration(day, stepCount, time);
                    getDatabaseManager().performanceDao().insertPerformance(performanceRegistration);
                }
                start = !start; // reverse
            }
        });
        if (LocalTime.now().getHour() == 00 && LocalTime.now().getMinute() == 00
                && LocalTime.now().getSecond() == 00 && day.compareTo(SUNDAY)==0){
            getDatabaseManager().performanceDao().deleteAll();
        }
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
        barEntriesArrayList.add(new BarEntry(1, getDatabaseManager().performanceDao().readSteps1()));
        barEntriesArrayList.add(new BarEntry(2, getDatabaseManager().performanceDao().readSteps2()));
        barEntriesArrayList.add(new BarEntry(3, getDatabaseManager().performanceDao().readSteps3()));
        barEntriesArrayList.add(new BarEntry(4, getDatabaseManager().performanceDao().readSteps4()));
        barEntriesArrayList.add(new BarEntry(5, getDatabaseManager().performanceDao().readSteps5()));
        barEntriesArrayList.add(new BarEntry(6, getDatabaseManager().performanceDao().readSteps6()));
        barEntriesArrayList.add(new BarEntry(7, getDatabaseManager().performanceDao().readSteps7()));
    }

    private void startTimer() {
        if (time != 0 ) {
            /*AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(),
                    AppDatabase.class, "attività settimanale").build();
            PerformanceDao performanceDao = db.performanceDao();
            List<Performance> performanceList = performanceDao.getAll();*/
            time = 0;
        }
        timerTask = new TimerTask() {
            @Override
            public void run()
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);
        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }
}
