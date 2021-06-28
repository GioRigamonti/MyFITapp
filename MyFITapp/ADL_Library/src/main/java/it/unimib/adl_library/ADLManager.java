package it.unimib.adl_library;

import android.content.Context;
import android.hardware.SensorManager;

import java.util.Map;

public abstract class ADLManager {
    protected Context context;

    public ADLManager(Context context) {
        this.context = context;
    }

    public abstract ADLListener getAccListener();
    public abstract void doInference(ADLInstance instance)throws Exception;
    protected abstract String setLabel();
    protected abstract Map setProbabilityMap();
}
