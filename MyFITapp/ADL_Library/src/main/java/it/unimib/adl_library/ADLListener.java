package it.unimib.adl_library;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ADLListener extends Service implements SensorEventListener {
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
            Intent intent = new Intent();
            intent.setAction("it.unimib.adl_library");
            intent.putExtra("label", adl_instance.getActivity());
            intent.putExtra("confidence", (Serializable) adl_instance.getMap());
            sendBroadcast(intent);
            clearFeatures();}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void clearFeatures() {
        adl_instance.getAccFeatures().clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
