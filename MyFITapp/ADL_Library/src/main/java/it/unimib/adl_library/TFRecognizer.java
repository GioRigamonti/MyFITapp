package it.unimib.adl_library;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.os.Build;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

import static java.security.AccessController.getContext;

public class TFRecognizer extends ADLManager {
    private Interpreter tflite;
    private ADLModel adl_model;
    private Map<String, Float> floatMap;
    private List<String> outputLabels;
    private Object[] inputVal;
    private float[] outputval;
    private ADLListener accListener;
    private TensorBuffer accelerometerCoordinates;
    TensorBuffer probabilityBuffer;
    AdlModel.Outputs outputs;
    List<String> associatedAxisLabels = null;

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        Toast.makeText(context, "TFrec_created", Toast.LENGTH_LONG).show();
        this.adl_model = new ADLModel(context);
        Toast.makeText(context, "model_init", Toast.LENGTH_LONG).show();
        accListener = new ADLListener(adlInst, this);
        Toast.makeText(context, "list_init", Toast.LENGTH_LONG).show();
    }



    /*@RequiresApi(api = Build.VERSION_CODES.N)
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
        AdlModel model = AdlModel.newInstance(context);
        TensorBuffer accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
        accelerometerCoordinates.loadBuffer(adl_model.getModel());

        AdlModel.Outputs outputs = model.process(accelerometerCoordinates);
        List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doInference(ADLInstance instance) throws IOException {
        try{
            // if (tflite == null){
            //if(accelerometerCoordinates==null)
                //initInterpreter(instance);
            //}
            //tflite.run(accelerometerCoordinates.getBuffer(), probabilityBuffer.getBuffer());
            AdlModel model = AdlModel.newInstance(context);

            // Creates inputs for reference.
            if (accelerometerCoordinates == null){
                accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
                accelerometerCoordinates.loadBuffer(adl_model.getModel());}

            // Runs model inference and gets result.
            outputs = model.process(accelerometerCoordinates);
            List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();
            //outputLabels = adl_model.getLabelsFromFile();
            // Releases model resources if no longer used.
            //model.close();
            //instance.setActivity(setLabel());
            //instance.setMap(setProbabilityMap());

            outputLabels = adl_model.getLabelRead();

            instance.setActivity(setLabel());
            instance.setMap(setProbabilityMap());
            close_interpreter();
        }catch(IOException e){}
    }

    /*private void initInterpreter(ADLInstance instance) throws IOException {
            tflite = new Interpreter(adl_model.getModel());
            inputVal = instance.getAccFeatures().toArray();
            //try {
                //outputLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS);
                outputLabels= new ArrayList<String>();
                outputLabels.add("walking");
                outputLabels.add("jogging");
                outputLabels.add("standing");
                outputLabels.add("sitting");
                outputLabels.add("stairs up");
                outputLabels.add("stairs down");
            //} catch (IOException e) {
                //Log.e("tfliteSupport", "Error reading label file", e);
            //}
            outputval = new float[outputLabels.size()];
    }*/
   /* private void initInterpreter(ADLInstance instance) throws IOException {
            tflite = new Interpreter(adl_model.getModel());
            tflite.allocateTensors();

            //inputVal = instance.getAccFeatures().toArray();

            //outputval = new float[outputLabels.size()];
        AdlModel model = AdlModel.newInstance(context);
        accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
        accelerometerCoordinates.load();
        // Runs model inference and gets result.
        probabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, 6}, DataType.FLOAT32);
        //outputs = model.process(accelerometerCoordinates);
        //List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();
        try {
            outputLabels = FileUtil.loadLabels(context.getApplicationContext(), ASSOCIATED_AXIS_LABELS);
                /*outputLabels= new ArrayList<String>();
                outputLabels.add("walking");
                outputLabels.add("jogging");
                outputLabels.add("standing");
                outputLabels.add("sitting");
                outputLabels.add("stairs up");
                outputLabels.add("stairs down");*/
       /* }catch (IOException e) {
            Log.e("tfliteSupport", "Error reading label file", e);
        }

    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String setLabel() {
        Map<String, Float> probMap = getLabel_Probabilities();
        String key = Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        return key;
    }

    private Map<String, Float> getLabel_Probabilities() {
        /*for (int i = 0; i < outputLabels.size(); i++) {
            String label = outputLabels.get(i);
            floatMap.put(label, outputval[i]);
        }
        return floatMap;*/
        // Post-processor which dequantize the result
        TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        if (null != outputLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(outputLabels, probabilityProcessor.process(probabilityBuffer));
            // Create a map to access the result based on label
            Map<String, Float> floatMap = labels.getMapWithFloatValue();
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



