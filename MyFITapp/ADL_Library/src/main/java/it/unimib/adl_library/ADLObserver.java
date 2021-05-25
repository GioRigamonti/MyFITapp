package it.unimib.adl_library;

public interface ADLObserver {
    private ADLInstance instance;

    public void onNewInstance(ADLInstance instance);

    public String activityIndentified();

    public float[] activityConfidence();
}
