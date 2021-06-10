package it.unimib.adl_library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

public class ADLListener implements SensorEventListener {
    protected static final int READING_DELAY = 150;
    private int readingDelay;
    private long lastReading;
    protected long samplingDelay = READING_DELAY;
    private ADLInstance adl_instance = new ADLInstance();
    private ADLManager recognizer;

    public ADLListener(int delay){
        readingDelay = delay;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (SystemClock.elapsedRealtime() > lastReading + readingDelay) {
            // generate the new magnitude
            lastReading = SystemClock.elapsedRealtime();
            Sensor sensor = event.sensor;
            //ACCELEROMETRO CON GRAVITA'
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void startGenerating() {
        lastReading = SystemClock.elapsedRealtime();
    }

    public void stopGenerating() {
        lastReading = Long.MAX_VALUE;
    }

    public void clearFeatures() {
        adl_instance.getAccFeatures().clear();

    }
}
