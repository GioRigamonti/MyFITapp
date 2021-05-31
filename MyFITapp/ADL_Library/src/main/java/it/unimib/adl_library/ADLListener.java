package it.unimib.adl_library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;

import java.util.ArrayList;

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
    @Override
    public void onSensorChanged(SensorEvent event){
        lastReading = SystemClock.elapsedRealtime();
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
