package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFRecognizer extends ADLManager {
    private final String ASSOCIATED_AXIS_LABELS = ADLModel.getLabelPath();
    private Interpreter tflite;
    private ADLModel adl_model;
    private Map<String, Float> floatMap;
    private List<String> outputLabels;
    private Object[] inputVal;
    private float[] outputval;
    private ADLListener accListener;

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        this.adl_model = new ADLModel(context);
        accListener = new ADLListener(adlInst);
    }

    public void startReadingAccelerometer() {
        accListener.clearFeatures(); //si assicura che l'istanza non ha letture precedenti
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accListener, s_i, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void stopReadingAccelerometer() {
        mSensorManager.unregisterListener(accListener);
        accListener.clearFeatures(); //pulizia delle letture precedenti
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        try{
            if (tflite == null){
                initInterpreter(instance);
            }
            tflite.run(inputVal, outputval);
            instance.setActivity(setLabel());
            instance.setMap(setProbabilityMap());
            close_interpreter();
        }catch(IOException e){}
    }

    private void initInterpreter(ADLInstance instance) throws IOException {
            tflite = new Interpreter(adl_model.getModel());
            inputVal = instance.getAccFeatures().toArray();
            try {
                outputLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS);
            } catch (IOException e) {
                Log.e("tfliteSupport", "Error reading label file", e);
            }
            outputval = new float[]{outputLabels.size()};

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String setLabel() {
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

    protected Map setProbabilityMap() {
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



