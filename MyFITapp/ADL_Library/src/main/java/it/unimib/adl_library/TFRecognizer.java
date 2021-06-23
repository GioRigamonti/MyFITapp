package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import it.unimib.adl_library.ml.AdlModel;

public class TFRecognizer extends ADLManager {
    /*private Interpreter tflite;
    private ADLModel adl_model;
    private Map<String, Float> floatMap;
    private List<String> outputLabels;*/
    /*private Object[] inputVal;
    private float[] outputval;
    private TensorBuffer probabilityBuffer;*/
    /*private ADLListener accListener;
    private TensorBuffer accelerometerCoordinates;
    private AdlModel.Outputs outputs;*/

    private ArrayList<ADLObserver> mObservers = new ArrayList<ADLObserver>();
    private Handler classificationHandler = new Handler();
    private ADLListener accelListener = new ADLListener();

    private Runnable classificationRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            // Create the instance and classify it
            ADLInstance instance = new ADLInstance();
            instance.setAccFeatures(accelListener.getAx(), accelListener.getAy(), accelListener.getAz());
            List<float[]> accelFeatures = instance.getAccFeatures();
            instance.setTimestamp(System.currentTimeMillis());
            try {
                mModelManager.doInference(instance);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Send the new instance to the observers
            for (ADLObserver o : mObservers) {
                o.onNewInstance(instance);
            }

            // clear readings arrays
            accelListener.clearFeatures();

            // Reset delayed runnable
            classificationHandler.postDelayed(classificationRunnable, default_sampling_delay);
        }
    };

    public TFRecognizer(Context context, ADLModel model) throws Exception {
        super(context, model);
        //this.adl_model = new ADLModel(context);
        //accListener = new ADLListener(adlInst, this);
    }

    public boolean registerVehicleObserver(ADLObserver observer) throws Exception {
        boolean result = false;

        if (!mObservers.contains(observer)) {
            int initialSize = mObservers.size();
            mObservers.add(observer);

            if (initialSize == 0) {
                startReadingAccelerometer();
            }

            result = true;
        }

        return result;
    }

    public boolean unregisterVehicleObserver(ADLObserver observer) {
        boolean result = false;

        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
            int currentSize = mObservers.size();

            if (currentSize == 0) {
                stopReadingAccelerometer();
            }

            result = true;
        }

        return result;
    }

    public void startReadingAccelerometer() throws Exception {
        //listener.clearFeatures(); //si assicura che l'istanza non ha letture precedenti
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accelListener, s_i, SensorManager.SENSOR_DELAY_GAME);
                /*mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);*/
            }
        }
        accelListener.startGenerating();
            /*if(instance.getAccFeatures().size()<=50)
                recognizer.doInference(instance);
            else
                stopReadingAccelerometer(mSensorManager);*/
        classificationHandler.removeCallbacks(classificationRunnable);
        classificationHandler
                .postDelayed(classificationRunnable, default_sampling_delay);
    }

    public void stopReadingAccelerometer() {
        mSensorManager.unregisterListener(accelListener);
        accelListener.stopGenerating();
        classificationHandler.removeCallbacks(classificationRunnable);
        //listener.clearFeatures(); //pulizia delle letture precedenti
    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        try{
            AdlModel model = AdlModel.newInstance(context);
            if (accelerometerCoordinates == null) {
                accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
                accelerometerCoordinates = (TensorBuffer) instance.getAccFeatures();
            }

            // Runs model inference and gets result.
            outputs = model.process(accelerometerCoordinates);
            List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();

            outputLabels = adl_model.getLabelRead();

            instance.setActivity(setLabel());
            instance.setMap(setProbabilityMap());

        }catch(IOException e){}
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String setLabel() {
        Map<String, Float> probMap = getLabel_Probabilities();
        String key = Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        return key;
    }

    private Map<String, Float> getLabel_Probabilities() {
        // Post-processor which dequantize the result
        TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        if (null != outputLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(outputLabels, probabilityProcessor.process(outputs.getProbabilitiesAsTensorBuffer()));
            // Create a map to access the result based on label
            floatMap = labels.getMapWithFloatValue();
        }
        return floatMap;
    }*/


    /*protected Map setProbabilityMap() {
        Map<String, Float> probabilityMap = new HashMap<String, Float>(floatMap);
        return probabilityMap;
    }*/

    /*private void close_interpreter() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }*/

    /*public ADLListener getAccListener(){
        return accListener;
    }*/
}



