package it.unimib.adl_library;

import android.content.Context;
import android.hardware.SensorManager;

import java.util.Map;

public abstract class ADLManager {
    private Context mContext;
    protected ADLModel mModelManager;
    protected SensorManager mSensorManager;
    protected long sampling_delay = 5000;

    public ADLManager(Context context, long delay) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        sampling_delay = delay;
    }

    public abstract boolean initObserverRegistration(ADLObserver observer);
    public abstract boolean stopObserverRegistration(ADLObserver observer);
    public abstract void startReadingAccelerometer();
    public abstract void stopReadingAccelerometer();
    public abstract Map getLabel();
    //public abstract float getConfidence();
}
