package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.DequantizeOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

public class TFRecognizer extends ADLManager {
    private Interpreter tflite;
    private final ADLModel adl_model;
    private HashMap<String, Float> floatMap;
    private List<String> outputLabels;
    private final ADLListener accListener;
    private AdlModel.Outputs outputs;

    private static final String TAG = "TFRecognizer";

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        this.adl_model = new ADLModel(context);
        accListener = new ADLListener(adlInst, this, context);
    }

    public void startReadingAccelerometer() {
        //accListener.clearFeatures(); //si assicura che l'istanza non ha letture precedenti
        //ACCELEROMETRO CON GRAVITA'
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> ls = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (int i = 0; i < ls.size(); i++) {
                Sensor s_i = ls.get(i);
                mSensorManager.registerListener(accListener, s_i, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public HashMap<String, Float> doInference(ADLInstance instance) throws IOException {
        AdlModel model = AdlModel.newInstance(context);

        int accelerometerValuesListSize = instance.getAccFeatures().size();

        float[] accelerometerValuesArray = new float[accelerometerValuesListSize*3];

        TensorBuffer accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{150, 1, 3}, DataType.FLOAT32);

        for (int i = 0; i < instance.getAccFeatures().size(); i++) {
            accelerometerValuesArray[i] = instance.getAccFeatures().get(i)[0];
            accelerometerValuesArray[++i] = instance.getAccFeatures().get(i)[1];
            accelerometerValuesArray[++i] = instance.getAccFeatures().get(i)[2];
        }

        accelerometerCoordinates.loadArray(accelerometerValuesArray);

        // Runs model inference and gets result.
        outputs = model.process(accelerometerCoordinates);
        List<Category> probabilities = outputs.getProbabilitiesAsCategoryList();
        Log.d(TAG, probabilities.get(3).getLabel() + " " + probabilities.get(3).getScore());
        outputLabels = adl_model.getLabelRead();
        instance.setActivity(setLabel());
        instance.setMap(floatMap);

        return floatMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String setLabel() {
        Map<String, Float> probMap = getLabel_Probabilities();
        return Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private HashMap<String, Float> getLabel_Probabilities() {
        TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();

        TensorProcessor probabilityProcessor2 =
                new TensorProcessor.Builder().build();

        if (null != outputLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(outputLabels, probabilityProcessor2.process(outputs.getProbabilitiesAsTensorBuffer()));
            // Create a map to access the result based on label
            floatMap = (HashMap<String, Float>) labels.getMapWithFloatValue();
        }
        return floatMap;
    }

    private void close_interpreter() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }

    public ADLListener getAccListener(){
        return accListener;
    }

    public void stopReadingAccelerometer() {
        mSensorManager.unregisterListener(accListener);
        accListener.clearFeatures(); //pulizia delle letture precedenti
    }
}
