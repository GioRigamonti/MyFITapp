package it.unimib.adl_library;

public abstract class ADLManager {
    //generate constructor
    public ADLManager() {
    }
    public abstract boolean initObserverRegistration(ADLObserver observer);
    public abstract boolean stopObserverRegistration(ADLObserver observer);
    public abstract void startReadingAccelerometer();
    public abstract void stopReadingAccelerometer();
    public abstract String getLabel();
    public abstract float getConfidence();
}
