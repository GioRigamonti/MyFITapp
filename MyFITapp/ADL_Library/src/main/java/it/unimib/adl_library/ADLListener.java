package it.unimib.adl_library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ADLListener implements SensorEventListener {
    protected static final int SAMPLE_PER_SEC = 50;
    private ADLInstance adl_instance;
    private ADLManager recognizer;

    public ADLListener(ADLInstance inst) throws Exception {
        this.adl_instance = inst;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        while (adl_instance.getAccFeatures().size() < SAMPLE_PER_SEC) {   //lista ha dim_finale = 50, si sono letti 50 campioni/sec
            Sensor sensor = event.sensor;
            //ACCELEROMETRO CON GRAVITA'
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
            }
        }
        try {
            recognizer.doInference(adl_instance);
        } catch (Exception e) {}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void clearFeatures() {
        adl_instance.getAccFeatures().clear();
    }
}
