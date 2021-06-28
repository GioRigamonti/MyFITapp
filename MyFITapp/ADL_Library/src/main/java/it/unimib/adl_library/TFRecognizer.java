package it.unimib.adl_library;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

import static android.content.ContentValues.TAG;

public class TFRecognizer extends ADLManager {
    private Interpreter tflite;
    private ADLModel adl_model;
    private HashMap<String, Float> floatMap;
    private List<String> outputLabels;

    private TensorBuffer probabilityBuffer;
    private ADLListener accListener;
    private TensorBuffer accelerometerCoordinates;
    private AdlModel.Outputs outputs;

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        this.adl_model = new ADLModel(context);
        accListener = new ADLListener(adlInst, this);
    }

    //Versione 1
   /* @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        AdlModel model = AdlModel.newInstance(context);
        accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
        accelerometerCoordinates = (TensorBuffer) instance.getAccFeatures();

        // Runs model inference and gets result.
        outputs = model.process(accelerometerCoordinates);
        //List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();
        outputLabels = adl_model.getLabelRead();
        instance.setActivity(setLabel());
        instance.setMap(setProbabilityMap());
    }*/
    private HashMap<String, Float> getLabel_Probabilities() {
        TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        if (null != outputLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(outputLabels, probabilityProcessor.process(outputs.getProbabilitiesAsTensorBuffer()));
            // Create a map to access the result based on label
            floatMap = (HashMap<String, Float>) labels.getMapWithFloatValue();
        }
        return floatMap;
    }
    //Versione 2
    /*public FloatBuffer input;
    public FloatBuffer output;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        tflite = new Interpreter(adl_model.getModel());
        input = FloatBuffer.allocate(tflite.getInputTensor(0).numElements());
        // Populate inputs...
        for (int i = 0; i< 150; i++){
            for(int j = 0; j < 3; j ++){
                input.put(instance.getAccFeatures().get(i)[j]);
            }
        }
        output = FloatBuffer.allocate(tflite.getOutputTensor(0).numElements());

        tflite.run(input, output);
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("labels.txt")));
            for (int i = 0; i < output.capacity(); i++) {
                String label = reader.readLine();
                float probability = output.get(i);
                Log.i(TAG, String.format("%s: %1.4f", label, probability));
            }
        } catch (IOException e) {
            // File not found?
        }
        //al momento i risultati non sono ancora stati salvati nella instance perchè codice ancora in fase di
        // prova; (quindi i metodi getLabel_Probabilities(),  setProbabilityMap() e  setLabel() risultano
        // inutilizzati con questo doInference; andranno poi sistemati una volta risolto l’errore)

        //instance.setActivity(setLabel());
        //instance.setMap(setProbabilityMap());
        close_interpreter();
    }*/


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String setLabel() {
        Map<String, Float> probMap = getLabel_Probabilities();
        String key = Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        return key;
    }

    protected HashMap setProbabilityMap() {
        HashMap<String, Float> probabilityMap = new HashMap<String, Float>(floatMap);
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



