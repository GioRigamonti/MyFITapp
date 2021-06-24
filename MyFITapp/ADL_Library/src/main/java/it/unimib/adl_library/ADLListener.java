package it.unimib.adl_library;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class ADLListener implements SensorEventListener {
    protected static final int SAMPLE_PER_SEC = 150;
    private ADLInstance adl_instance;
    private ADLManager recognizer;

    public ADLListener(ADLInstance inst, ADLManager rec) throws Exception  {
        this.adl_instance = inst;
        this.recognizer = rec;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*Sensor sensor = event.sensor;
        //ACCELEROMETRO CON GRAVITA'
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
            int rilevazioniAcc = adl_instance.getAccFeatures().size();

            if (rilevazioniAcc >= SAMPLE_PER_SEC && rilevazioniAcc % SAMPLE_PER_SEC == 0) {  //lista ha dim_finale = 150, si sono letti 50 campioni/sec
                //adl_instance.setFrame(rilevazioniAcc);   //crea finestre di lettura da 150 campioni
                try {
                    recognizer.doInference(adl_instance);
                } catch (Exception e) {}
            }
        }*/
        if (adl_instance.getAccFeatures().size() < SAMPLE_PER_SEC) {   //lista ha dim_finale = 50, si sono letti 50 campioni/sec
            Sensor sensor = event.sensor;
            //ACCELEROMETRO CON GRAVITA'
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
            }
        } else{
            try {
                recognizer.doInference(adl_instance);
            } catch (Exception e) {}
            clearFeatures();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void clearFeatures() {
        //List<float[]>  clearFeatures = adl_instance.getAccFeatures().subList(0,adl_instance.FRAME - adl_instance.OVERLAP);
        //List<float[]>  newFeatures = adl_instance.getAccFeatures().subList(adl_instance.FRAME - adl_instance.OVERLAP + 1, adl_instance.getAccFeatures().size());
        adl_instance.getAccFeatures().subList(0,adl_instance.FRAME - adl_instance.OVERLAP).clear();
        adl_instance.acc_Features.subList(0,adl_instance.getAccFeatures().size());

    }

}
