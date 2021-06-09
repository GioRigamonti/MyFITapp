package it.unimib.adl_library;

import android.content.Context;
import android.hardware.SensorManager;

import java.util.Map;

public abstract class ADLManager {
    public static final int DEFAULT_SAMPLING_DELAY = 5000;
    private Context mContext;
    protected ADLModel adl_model;
    protected SensorManager mSensorManager;
    protected long sampling_delay = DEFAULT_SAMPLING_DELAY;

    public ADLManager(Context context, long delay) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        sampling_delay = delay;
    }

    public abstract boolean initObserverRegistration(ADLObserver observer);
    public abstract boolean stopObserverRegistration(ADLObserver observer);
    public abstract void startReadingAccelerometer();
    public abstract void stopReadingAccelerometer();
    public abstract void doInference(ADLInstance instance)throws Exception;
    public abstract String getLabel();
}
