package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Handler;
import android.hardware.SensorManager;
import android.util.Log;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

public class TFRecognizer extends ADLManager {
    final String ASSOCIATED_AXIS_LABELS = ADLModel.getLabelPath();
    private Interpreter tflite;
    private ADLModel model;
    private ArrayList<ADLObserver> accObserver = new ArrayList<ADLObserver>();
    private List<Category> probabilities;
    TensorBuffer accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);


    //private Handler classificationHandler = new Handler();
    private ADLListener accListener = new ADLListener();

    public TFRecognizer(Context context, long delay) throws IOException, IllegalArgumentException {
        super(context, delay);
        this.model = new ADLModel();
        try {

            //tflite = new Interpreter(model.getModel());

        }
        catch (IllegalArgumentException ex){

        }

    }

    public boolean initObserverRegistration(ADLObserver observer){
        boolean result = false;

        if (!accObserver.contains(observer)) {
            int initialSize = accObserver.size();
            accObserver.add(observer);

            if (initialSize == 0) {
                startReadingAccelerometer();
            }
            result = true;
        }
        return result;
    }
    public boolean stopObserverRegistration(ADLObserver observer){
        boolean result = false;

        if (accObserver.contains(observer)) {
            accObserver.remove(observer);
            int currentSize = accObserver.size();

            if (currentSize == 0) {
                stopReadingAccelerometer();
            }
            result = true;
        }
        return result;
    }

    public void startReadingAccelerometer(){
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accListener, s_i, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        /*classificationHandler.removeCallbacks(classificationRunnable);
        classificationHandler.postDelayed(classificationRunnable, sampling_delay);*/

    }
    public void stopReadingAccelerometer(){
        mSensorManager.unregisterListener(accListener);
        //classificationHandler.removeCallbacks(classificationRunnable);

    }


    public float doInference(String inputString)throws IOException {
        model = new ADLModel();
        accelerometerCoordinates.loadBuffer(model.getModel());

        AdlModel.Outputs outputs = model.process(accelerometerCoordinates);
        probabilities = outputs.getProbabilitiesAsCategoryList();
        tflite.close();
        return ;
    }

    public String getLabel(Context context){
        List<String> associatedAxisLabels = null;

        try {
            associatedAxisLabels = FileUtil.loadLabels(this, ASSOCIATED_AXIS_LABELS);
        } catch (IOException e) {
            Log.e("tfliteSupport", "Error reading label file", e);
        }

        // Post-processor which dequantize the result
        TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        if (null != associatedAxisLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(associatedAxisLabels,
                    probabilityProcessor.process(probabilityBuffer));
            // Create a map to access the result based on label
            Map<String, Float> floatMap = labels.getMapWithFloatValue();
            float max = floatMap.get()
            return getLabel(floatMap, );
        }

    }

    /*public float getConfidence(){

    }*/



    private Runnable classificationRunnable = new Runnable() {
        @Override
        public void run() {
            ADLInstance instance = new ADLInstance();
            instance.setTimestamp(System.currentTimeMillis());
            /*instance.setAccFeatures( //features );*/

            // Send the new instance to the observers
            for (ADLObserver o : accObserver) {
                o.onNewInstance(instance);
            }

            // clear readings arrays
            accListener.clearFeatures();

            // Reset delayed runnable
            //classificationHandler.postDelayed(classificationRunnable, sampling_delay);
        }
    };

}
