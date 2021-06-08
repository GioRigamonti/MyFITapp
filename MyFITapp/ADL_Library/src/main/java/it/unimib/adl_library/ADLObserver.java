package it.unimib.adl_library;

public interface ADLObserver {

    public void onNewInstance(ADLInstance instance);

    public String activityIndentified();

    public float[] activityConfidence();
}
