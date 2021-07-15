package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

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
}

