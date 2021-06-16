package it.unimib.adl_library;

import android.content.Context;

import java.util.Map;

public class Observer implements ADLObserver{
    private ADLInstance instance;

    public Observer(Context context) throws Exception {
        instance = new ADLInstance();
        ADLManager recognizer = new TFRecognizer(context, instance);
    }

    @Override
    public String activityIndentified() {
        return instance.getActivity();
    }

    @Override
    public Map<String, Float> activityConfidence() {
        return instance.getMap();
    }
}
