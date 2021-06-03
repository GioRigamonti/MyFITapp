package it.unimib.adl_library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

public class ADLListener implements SensorEventListener {
    public static final int DEFAULT_SAMPLING_DELAY = 5000;
    private int frequenza;
    private int readingDelay;
    private long lastReading;
    protected long samplingDelay = DEFAULT_SAMPLING_DELAY;
    public ArrayList<Double> accArray = new ArrayList<Double>();

    public void ADLListener(int frequenza){
        readingDelay = frequenza;
    }

    private static List<Float> ax = new ArrayList<>();
    private static List<Float> ay = new ArrayList<>();
    private static List<Float> az = new ArrayList<>();

    @Override
    public void onSensorChanged(SensorEvent event){
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);
        }

        }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void setFrequenzaDiCampionamento(int millisecondi){
        samplingDelay = millisecondi;
    }

    public int getFrequenzaDiCampionamento(){

        return frequenza;
    }

    public void clearFeatures() {
        accArray.clear();
    }
}
