package it.unimib.adl_library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ADLListener implements SensorEventListener {
    protected static final int SAMPLE_PER_SEC = 150;
    private ADLInstance adl_instance;
    private ADLManager recognizer;
    private Context context;

    public ADLListener(ADLInstance inst, ADLManager rec, Context context) throws Exception  {
        this.adl_instance = inst;
        this.recognizer = rec;
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (adl_instance.getAccFeatures().size() < SAMPLE_PER_SEC) {
            Sensor sensor = event.sensor;
            //ACCELEROMETRO CON GRAVITA'
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                adl_instance.setAccFeatures(event.values[0], event.values[1], event.values[2]);
            }
        } else{
            try {
                recognizer.doInference(adl_instance);
                HashMap<String, Float> map = recognizer.doInference(adl_instance);

                Intent intent = new Intent();
                intent.setAction("it.unimib.myservice");
                intent.putExtra("map", map);

                context.sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
            clearFeatures();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void clearFeatures() {
        adl_instance.getAccFeatures().subList(0,adl_instance.getFRAME() - adl_instance.getOVERLAP()).clear();
        adl_instance.acc_Features.subList(0,adl_instance.getAccFeatures().size());
    }

}
