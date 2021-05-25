package it.unimib.adl_library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class ADLListener {
    private int frequenza;

    public void ADLListener(int frequenza){}

    public void onSensorChanged(SensorEvent event){}

    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void setFrequenzaDiCampionamento(int millisecondi){}

    public int getFrequenzaDiCampionamento(){
        return frequenza;
    }
}
