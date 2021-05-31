package it.unimib.adl_library;

import android.content.Context;
import android.hardware.SensorManager;

public abstract class ADLManager {
    public static final int DEFAULT_SAMPLING_DELAY = 5000;

    private Context mContext = null;
    protected ADLModel mModelManager = null;
    protected SensorManager mSensorManager = null;
    protected long samplingDelay = DEFAULT_SAMPLING_DELAY;

    //generate constructor
    public ADLManager(Context context, ADLModel modelManager, long delay) {
        mContext = context;
        mModelManager = modelManager;
        mSensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        samplingDelay = delay;
    }
    public abstract boolean initObserverRegistration(ADLObserver observer);
    public abstract boolean stopObserverRegistration(ADLObserver observer);
    public abstract void startReadingAccelerometer();
    public abstract void stopReadingAccelerometer();
    public abstract String getLabel();
    public abstract float getConfidence();
}
