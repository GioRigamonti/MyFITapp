package it.unimib.myfitapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import it.unimib.adl_library.*;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BackgroundAccelerometerService extends Service{
    private Observer observer;
    protected SensorManager mSensorManager;

    public BackgroundAccelerometerService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try {
            observer = new Observer(getApplicationContext(), "TF");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG)
                .show();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Start Detecting", Toast.LENGTH_LONG).show();
        observer.startReadingAccelerometer(mSensorManager);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Service Destroyed", Toast.LENGTH_LONG).show();
        observer.stopReadingAccelerometer(mSensorManager);
        sendBroadcast(observer.probability());
    }
}