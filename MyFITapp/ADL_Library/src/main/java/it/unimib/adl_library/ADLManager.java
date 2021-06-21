package it.unimib.adl_library;

import android.content.Context;
import android.hardware.SensorManager;

import java.util.Map;

public abstract class ADLManager {
    protected Context context;
    //protected SensorManager mSensorManager;

    public ADLManager(Context context) {
        this.context = context;
        //mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    //public abstract void startReadingAccelerometer();
    //public abstract void stopReadingAccelerometer();
    public abstract ADLListener getAccListener();
    public abstract void doInference(ADLInstance instance)throws Exception;
    protected abstract String setLabel();
    protected abstract Map setProbabilityMap();
}
