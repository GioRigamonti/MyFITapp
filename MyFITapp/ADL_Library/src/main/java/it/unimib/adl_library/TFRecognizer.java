package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Handler;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

public class TFRecognizer extends ADLManager {
    final String ASSOCIATED_AXIS_LABELS = ADLModel.getLabelPath();
    private Interpreter tflite;
    private Context context;
    private ArrayList<ADLObserver> accObserver = new ArrayList<ADLObserver>();
    private ADLModel adl_model;
    private Map<String, Float> floatMap;
    private List<String> outputLabels;
    private Object[] inputVal;
    private float[] outputval;
    private Handler classificationHandler = new Handler();
    private ADLListener accListener = new ADLListener(ADLListener.READING_DELAY);

    public TFRecognizer(Context context, long delay) throws Exception {
        super(context, delay);
        this.adl_model = new ADLModel(context);
    }

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
            classificationHandler.postDelayed(classificationRunnable, sampling_delay);
        }
    };

    public boolean initObserverRegistration(ADLObserver observer) {
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

    public boolean stopObserverRegistration(ADLObserver observer) {
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

    public void startReadingAccelerometer() {
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accListener, s_i, SensorManager.SENSOR_DELAY_FASTEST);
            }

        }
        accListener.startGenerating();
        /* ACCELEROMETRO SENZA GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accListener, s_i, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }*/

        classificationHandler.removeCallbacks(classificationRunnable);
        classificationHandler.postDelayed(classificationRunnable, sampling_delay);

    }

    public void stopReadingAccelerometer() {
        mSensorManager.unregisterListener(accListener);
        accListener.stopGenerating();
        classificationHandler.removeCallbacks(classificationRunnable);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        try{
            if (tflite == null){
                initInterpreter(instance);
            }
            tflite.run(inputVal, outputval);
            instance.setActivity(getLabel());
            close_interpreter();
        }catch(IOException e){}
    }

    private void initInterpreter(ADLInstance instance) throws IOException {
            tflite = new Interpreter(adl_model.getModel());
            inputVal = instance.getAccFeatures().toArray();
            try {
                outputLabels = FileUtil.loadLabels(this.context, ASSOCIATED_AXIS_LABELS);
            } catch (IOException e) {
                Log.e("tfliteSupport", "Error reading label file", e);
            }
            outputval = new float[]{outputLabels.size()};

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getLabel() {
        Map<String, Float> probMap = getLabel_Probabilities();
        String key = Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        return key;
    }

    private Map<String, Float> getLabel_Probabilities() {
        for (int i = 0; i < outputLabels.size(); i++) {
            String label = outputLabels.get(i);
            floatMap.put(label, outputval[i]);

        }
        return floatMap;
    }

    public Map getProbabilityMap() {
        Map<String, Float> probabilityMap = new HashMap<String, Float>(floatMap);
        return probabilityMap;
    }

    private void close_interpreter() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}



