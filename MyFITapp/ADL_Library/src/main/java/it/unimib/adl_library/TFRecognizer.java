package it.unimib.adl_library;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

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
        Toast.makeText(context, "TFrec_created", Toast.LENGTH_LONG).show();
        this.adl_model = new ADLModel(context);
        Toast.makeText(context, "model_init", Toast.LENGTH_LONG).show();
        accListener = new ADLListener(adlInst);
        Toast.makeText(context, "list_init", Toast.LENGTH_LONG).show();

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

    public ADLListener getAccListener(){
        return accListener;
    }
}



