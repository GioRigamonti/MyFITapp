package it.unimib.adl_library;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFRecognizer extends ADLManager {
    private Interpreter tflite;
    private ADLModel adl_model;
    private HashMap<String, Float> floatMap;
    private List<String> outputLabels;
    private float [][] inputVal;
    private float [][] outputval;
    private TensorBuffer probabilityBuffer;
    private ADLListener accListener;
    //private TensorBuffer accelerometerCoordinates;
    //private AdlModel.Outputs outputs;

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        this.adl_model = new ADLModel(context);
        accListener = new ADLListener(adlInst, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {

            tflite = new Interpreter(adl_model.getModel());
            tflite.allocateTensors();
            inputVal = new float [150][3];
            for (int i = 0; i< inputVal.length; i++){
                for(int j = 0; j < inputVal[i].length; j ++){
                    inputVal[i][j] = instance.getAccFeatures().get(i)[j];
                }
            }
            //inputVal = instance.getAccFeatures().toArray();
            outputLabels = adl_model.getLabelRead();
            outputval = new float[1][outputLabels.size()];

            tflite.run(inputVal, outputval);


        /*try{
            AdlModel model = AdlModel.newInstance(context);
            if (accelerometerCoordinates == null) {
                accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
                accelerometerCoordinates = (TensorBuffer) instance.getAccFeatures();
            }

            // Runs model inference and gets result.
            outputs = model.process(accelerometerCoordinates);
            List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();

            outputLabels = adl_model.getLabelRead();*/

            instance.setActivity(setLabel());
            instance.setMap(setProbabilityMap());
            tflite.close();

            /*Intent i = new Intent();
            i.setAction("it.unimib.myfitapp");
            i.putExtra("label", "a");
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String setLabel() {
        Map<String, Float> probMap = getLabel_Probabilities();
        String key = Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        return key;
    }

    private HashMap<String, Float> getLabel_Probabilities() {
         for (int i = 0; i < outputLabels.size(); i++) {
            String label = outputLabels.get(i);
            floatMap.put(label, outputval[1][i]);
        }
        return floatMap;
        // Post-processor which dequantize the result
        /*TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        if (null != outputLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(outputLabels, probabilityProcessor.process(outputval));
            // Create a map to access the result based on label
            floatMap = (HashMap<String, Float>) labels.getMapWithFloatValue();*/
        //}
        //return floatMap;
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



