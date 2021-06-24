/*package it.unimib.adl_library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;*/

/*public class Observer extends Service implements ADLObserver{
    private ADLInstance instance;
    private TFRecognizer recognizer;
    protected SensorManager mSensorManager;
    private ADLListener listener;
    Context context1;

    public Observer(Context context, String index) throws Exception {
        this.context1=context;
        Toast.makeText(context1, "inObserver constr", Toast.LENGTH_LONG).show();

        instance = new ADLInstance();
        switch (index.toUpperCase()){
            case "TF":
                recognizer = new TFRecognizer(context, instance);
                break;
            //default:
                //throw new Exception();
        }

        listener = recognizer.getAccListener();
    }

    @Override
    public String activityIdentified() {
        return instance.getActivity();
    }

    @Override
    public Map<String, Float> activityConfidence() {
        return instance.getMap();
    }

    public void probability(){
        Intent intent = new Intent();
        intent.setAction("it.unimib.myfitapp");
        intent.putExtra("label", activityIdentified());
        intent.putExtra("confidence", (Serializable) activityConfidence());
        sendBroadcast(intent);
    }

    private void startReadingAccelerometer() {
        listener.clearFeatures(); //si assicura che l'istanza non ha letture precedenti
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(listener, s_i, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    private void stopReadingAccelerometer() {
        mSensorManager.unregisterListener(listener);
        listener.clearFeatures(); //pulizia delle letture precedenti
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toast.makeText(context1, "The new Service was Created", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(context1, "Start Detecting", Toast.LENGTH_LONG).show();
        startReadingAccelerometer();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(context1, "Service Destroyed", Toast.LENGTH_LONG).show();
        stopReadingAccelerometer();
    }
}*/

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
    SensorManager m;

    public Observer(Context context, String index) throws Exception {
        instance = new ADLInstance();
        switch (index.toUpperCase()){
            case "TF":
                recognizer = new TFRecognizer(context, instance);
                break;
            /*default: ??? */
        }
        listener = recognizer.getAccListener();
        /*m = recognizer.mSensorManager;
        startReadingAccelerometer(m);*/
    }

    @Override
    public void startReadingAccelerometer(SensorManager mSensorManager) throws Exception {
        //listener.clearFeatures(); //si assicura che l'istanza non ha letture precedenti
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            /*List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(listener, s_i, SensorManager.SENSOR_DELAY_GAME);*/
            mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            /*if(instance.getAccFeatures().size()<=50)
                recognizer.doInference(instance);
            else
                stopReadingAccelerometer(mSensorManager);*/
        }
    }

    @Override
    public void stopReadingAccelerometer(SensorManager mSensorManager) {
        mSensorManager.unregisterListener(listener);
        //listener.clearFeatures(); //pulizia delle letture precedenti
    }

    public ADLListener getListener() {
        return listener;
    }

    @Override
    public String activityIdentified() {
        return instance.getActivity();
    }

    @Override
    public HashMap<String, Float> activityConfidence() {
        return instance.getMap();
    }
}

