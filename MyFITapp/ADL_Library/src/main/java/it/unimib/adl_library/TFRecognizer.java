package it.unimib.adl_library;

import android.content.Context;
import android.os.Build;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

public class TFRecognizer extends ADLManager {
    private Interpreter tflite;
    private ADLModel adl_model;
    private Map<String, Float> floatMap;
    private List<String> outputLabels;
    /*private Object[] inputVal;
    private float[] outputval;
    private TensorBuffer probabilityBuffer;*/
    private ADLListener accListener;
    private TensorBuffer accelerometerCoordinates;
    private AdlModel.Outputs outputs;

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        this.adl_model = new ADLModel(context);
        accListener = new ADLListener(adlInst, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        try{
            AdlModel model = AdlModel.newInstance(context);

            // Creates inputs for reference.
            if (accelerometerCoordinates == null){
                accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
                accelerometerCoordinates.loadBuffer(adl_model.getModel());}

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
    }


    protected Map setProbabilityMap() {
        Map<String, Float> probabilityMap = new HashMap<String, Float>(floatMap);
        return probabilityMap;
    }

    /*private void close_interpreter() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }*/

    public ADLListener getAccListener(){
        return accListener;
    }
}



