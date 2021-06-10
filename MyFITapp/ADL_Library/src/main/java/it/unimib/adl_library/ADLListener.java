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
    private ADLInstance adl_instance = new ADLInstance();
    private ADLManager recognizer;

    public void ADLListener(int frequenza){
        readingDelay = frequenza;
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        Sensor sensor = event.sensor;
        //ACCELEROMETRO CON GRAVITA'
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            adl_instance.setAccFeatures(event.values[0], event.values[1],event.values[2]);
        }
        /* ACCELEROMETRO SENZA GRAVITA'
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            adl_instance.setAccFeatures(event.values[0], event.values[1],event.values[2]);
        }*/
        try {
            recognizer.doInference(adl_instance);
        } catch (Exception e) {

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
        adl_instance.getAccFeatures().clear();

    }
}
