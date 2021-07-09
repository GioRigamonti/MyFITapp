package it.unimib.adl_library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Observer implements ADLObserver{
    private ADLInstance instance;
    private ADLManager recognizer;
    private ADLListener listener;

    public Observer(Context context, String index) throws Exception {
        instance = new ADLInstance();
        switch (index.toUpperCase()){
            case "TF":
                recognizer = new TFRecognizer(context, instance);
                break;
            /*default: ??? */
        }
        listener = recognizer.getAccListener();

    }

    @Override
    public void startReadingAccelerometer(SensorManager mSensorManager) throws Exception {
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void stopReadingAccelerometer(SensorManager mSensorManager) {
        mSensorManager.unregisterListener(listener);
    }

    @Override
    public String activityIdentified() {
        return instance.getActivity();
    }

    @Override
    public HashMap<String, Float> activityConfidence() {
        return instance.getMap();
    }

     /*public Intent probability(){
        Intent i = new Intent();
        i.setAction("it.unimib.myfitapp");
        i.putExtra("label", observer.activityIdentified());
        //i.putExtra("confidence", activityConfidence());
        //sendBroadcast(i);
        return intent;
    }*/
}

