package it.unimib.adl_library;

import android.content.Context;
import android.hardware.SensorManager;

import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

public abstract class ADLManager {
    public final int default_sampling_delay= 3000;
    protected Context context;
    protected SensorManager mSensorManager;
    protected long sampling_delay;
    protected ADLModel mModelManager = null;

    public ADLManager(Context context, ADLModel modelManager) {
        this.context = context;
        sampling_delay = default_sampling_delay;
        mModelManager = modelManager;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public abstract void startReadingAccelerometer() throws Exception;
    public abstract void stopReadingAccelerometer();
    //public abstract ADLListener getAccListener();
    //public abstract void doInference(ADLInstance instance)throws Exception;
    //protected abstract String setLabel();
    //protected abstract Map setProbabilityMap();
}
