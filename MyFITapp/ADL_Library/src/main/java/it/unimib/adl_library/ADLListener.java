package it.unimib.adl_library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ADLListener implements SensorEventListener {
    protected static final int SAMPLE_PER_SEC = 150;
    private ADLInstance adl_instance;
    private ADLManager recognizer;

    public ADLListener(ADLInstance inst, ADLManager rec) throws Exception {
        this.adl_instance = inst;
        this.recognizer = rec;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        //ACCELEROMETRO CON GRAVITA'
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
        }
        int rilevazioniAcc = adl_instance.getAccFeatures().size();

        if (rilevazioniAcc >= SAMPLE_PER_SEC && rilevazioniAcc % SAMPLE_PER_SEC == 0) {  //lista ha dim_finale = 150, si sono letti 50 campioni/sec
            adl_instance.setFrame();
            try {
                recognizer.doInference(adl_instance);
            } catch (Exception e) {}
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void clearFeatures() {
        adl_instance.getAccFeatures().clear();
    }
}
