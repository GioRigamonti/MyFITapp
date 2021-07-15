package it.unimib.adl_library;

import android.hardware.SensorManager;

import java.util.Map;

public interface ADLObserver {

    public void startReadingAccelerometer(SensorManager mSensorManager) throws Exception;

    public void stopReadingAccelerometer(SensorManager mSensorManager);
}
