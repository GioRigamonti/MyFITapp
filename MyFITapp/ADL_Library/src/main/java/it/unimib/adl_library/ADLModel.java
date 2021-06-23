package it.unimib.adl_library;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.adl_library.ml.AdlModel;

public class ADLModel {
    private Context context;
    private MappedByteBuffer model;
    private List<String> labels;
    private final static String MODEL_FILE = "ADL_Model.tflite";
    private final static String LABEL_FILE = "labels.txt";

    public ADLModel(Context context) throws IOException {
        this.context = context;
        this.model = loadModelFile();
        this.labels = loadLabelsFile();
    }

    public MappedByteBuffer getModel() {
        return model;
    }

    public List<String> getLabelRead() {
        return labels;
    }

    private List<String> loadLabelsFile(){
        List<String> labels = new ArrayList<>();
        try (InputStream ins = context.getAssets().open(LABEL_FILE);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins))) {
            while (bufferedReader.ready()) {
                labels.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return labels;
    }

    private MappedByteBuffer loadModelFile()throws IOException{
        //open the model using an input stream and memory map it load
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        Long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /*public static String getModelPath() {
        return MODEL_FILE;
    }

    public static String getLabelPath() {
        return LABEL_FILE;
    }*/
    private TensorBuffer accelerometerCoordinates;
    private AdlModel.Outputs outputs;
    private List<String> outputLabels;
    private Map<String, Float> floatMap;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ADLInstance doInference(ADLInstance instance) throws IOException {
        try{
            AdlModel model = AdlModel.newInstance(context);
            if (accelerometerCoordinates == null) {
                accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{1, 150, 3, 1}, DataType.FLOAT32);
                accelerometerCoordinates = (TensorBuffer) instance.getAccFeatures();
            }

            // Runs model inference and gets result.
            outputs = model.process(accelerometerCoordinates);
            List<Category> Probabilities = outputs.getProbabilitiesAsCategoryList();

            outputLabels = getLabelRead();

            instance.setActivity(setLabel());
            instance.setMap(setProbabilityMap());

        }catch(IOException e){}
        return instance;
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
}
