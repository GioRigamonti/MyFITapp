package it.unimib.adl_library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ADLListener implements SensorEventListener {
    protected static final int SAMPLE_PER_SEC = 150;
    //private ADLInstance adl_instance;
    //private ADLManager recognizer;

    private int readingDelay = Integer.MAX_VALUE;
    private long lastReading = Long.MAX_VALUE;

    List<float[]> acc_Features = new ArrayList<>();

    public ADLListener(){
        this.readingDelay=SAMPLE_PER_SEC;
    }

    /*public ADLListener(ADLInstance inst, ADLManager rec) throws Exception  {
        this.adl_instance = inst;
        this.recognizer = rec;
    }*/

    public void startGenerating() {
        lastReading = SystemClock.elapsedRealtime();
    }

    public void stopGenerating() {
        lastReading = Long.MAX_VALUE;
    }

    public void clearFeatures() {
        acc_Features.clear();
    }

    float ax, ay, az;

    public float getAx() {
        return ax;
    }

    public float getAy() {
        return ay;
    }

    public float getAz() {
        return az;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*Sensor sensor = event.sensor;
        //ACCELEROMETRO CON GRAVITA'
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
            int rilevazioniAcc = adl_instance.getAccFeatures().size();

            if (rilevazioniAcc != 0 && rilevazioniAcc % SAMPLE_PER_SEC == 0) {  //lista ha dim_finale = 150, si sono letti 50 campioni/sec
                adl_instance.setFrame(rilevazioniAcc);   //crea finestre di lettura da 150 campioni
                try {
                    recognizer.doInference(adl_instance);
                } catch (Exception e) {}
            }
        }*/
        /*if (adl_instance.getAccFeatures().size() < SAMPLE_PER_SEC) {   //lista ha dim_finale = 50, si sono letti 50 campioni/sec
            Sensor sensor = event.sensor;
            //ACCELEROMETRO CON GRAVITA'
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
            }
        } else{
            try {
                recognizer.doInference(adl_instance);
                clearFeatures();
            } catch (Exception e) {}
            }*/
        if (SystemClock.elapsedRealtime() > lastReading + readingDelay) {
            // generate the new magnitude
            lastReading = SystemClock.elapsedRealtime();
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

}
