package it.unimib.adl_library;

import android.content.Context;

public abstract class ADLManager {
    protected Context context;

    public ADLManager(Context context) {
        this.context = context;
    }

    public abstract ADLInstance doInference(ADLInstance instance) throws Exception;
    public abstract ADLListener getAccListener();
}
