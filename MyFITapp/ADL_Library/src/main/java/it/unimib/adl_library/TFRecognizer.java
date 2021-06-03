package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Handler;
import android.hardware.SensorManager;

import org.tensorflow.lite.Interpreter;

import java.util.ArrayList;
import java.util.List;

public class TFRecognizer extends ADLManager {
    private ADLModel model;
    private ArrayList<ADLObserver> accObservers = new ArrayList<ADLObserver>();


    private Handler classificationHandler = new Handler();
    private ADLListener accListener = new ADLListener();

    private Runnable classificationRunnable = new Runnable() {
        @Override
        public void run() {
            ADLInstance instance = new ADLInstance();
            instance.setTimestamp(System.currentTimeMillis());
            /*instance.setAccFeatures( //features );*/

            // Send the new instance to the observers
            for (ADLObserver o : accObservers) {
                o.onNewInstance(instance);
            }

            // clear readings arrays
            accListener.clearFeatures();

            // Reset delayed runnable
            classificationHandler.postDelayed(classificationRunnable, samplingDelay);
        }
    };

    public TFRecognizer(Context context, ADLModel model, long delay) {
        super(context, model, delay);
        //this.model = model;
    }
    public boolean initObserverRegistration(ADLObserver observer){
        boolean result = false;

        if (!accObservers.contains(observer)) {
            int initialSize = accObservers.size();
            accObservers.add(observer);

            if (initialSize == 0) {
                startReadingAccelerometer();
            }

            result = true;
        }

        return result;

    }
    public boolean stopObserverRegistration(ADLObserver observer){
        boolean result = false;

        if (accObservers.contains(observer)) {
            accObservers.remove(observer);
            int currentSize = accObservers.size();

            if (currentSize == 0) {
                stopReadingAccelerometer();
            }

            result = true;
        }

        return result;

    }
    public void startReadingAccelerometer(){
        // Register the sensors and start the MagnitudeListeners
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager
                    .getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accListener, s_i,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        // Launch the delayed classification task
        classificationHandler.removeCallbacks(classificationRunnable);
        classificationHandler
                .postDelayed(classificationRunnable, samplingDelay);

    }
    public void stopReadingAccelerometer(){
        // Unregister the sensors and stop the MagnitudeListeners
        mSensorManager.unregisterListener(accListener);
        // Remove the classification task
        classificationHandler.removeCallbacks(classificationRunnable);

    }
    public float doInference(String inputString) {
        //input shape is 1
        float[] inputVal = new float[1];
        inputVal[0] = Float.parseFloat(inputString);

        //output shape is 1
        float [] outputval = new float[1];

        //Run inference passing the input shape and getting the output shape
        model.getModel().run(inputVal, outputval);
        //inferred value is at
        float inferredValue = outputval[0];

        return inferredValue;
    }

    /*public String getLabel(){

    }*/

    /*public float getConfidence(){

    }*/

    public void interpreterClose(Interpreter interpreter){
        interpreter.close();
    }



}
