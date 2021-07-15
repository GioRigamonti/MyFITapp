package it.unimib.adl_library;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.unimib.adl_library.ml.AdlModel;

public class TFRecognizer extends ADLManager {
    private static final String TAG = "TFRecognizer";
    private ADLListener accListener;
    private final ADLModel adl_model;
    private final AdlModel modelFromMetadata;
    private final List<String> outputLabels;
    private AdlModel.Outputs outputs;
    private HashMap<String, Float> floatMap;

    public TFRecognizer(Context context, ADLInstance adlInst) throws Exception {
        super(context);
        this.adl_model = new ADLModel(context);
        accListener = new ADLListener(adlInst, this, context);

        modelFromMetadata = adl_model.getModelFromMetadata();
        outputLabels = adl_model.getLabelRead();
    }

   @RequiresApi(api = Build.VERSION_CODES.N)
   public ADLInstance doInference(ADLInstance instance) throws IOException {
       TensorBuffer accelerometerCoordinates = TensorBuffer.createFixedSize(new int[]{150, 3, 1}, DataType.FLOAT32);

       // standardizzazione delle rilevazioni
       float[] accValuesArray = accelerometerDataStandardization(instance);

       accelerometerCoordinates.loadArray(accValuesArray);

       // inferenza sul modello
       outputs = modelFromMetadata.process(accelerometerCoordinates);

       instance.setMap(getLabel_ProbabilitiesMap());
       instance.setActivity(getActivityLabel(instance.getMap()));

       return instance;
    }

    private float[] accelerometerDataStandardization(ADLInstance instance){
        List<float[]> accelerometerValuesList = instance.getAccFeatures();
        int accelerometerValuesList_Size = accelerometerValuesList.size();
        float[] accelerometerValuesArray = new float[accelerometerValuesList_Size*3];

        // calcolo somma e media
        float somma_x = 0;
        float somma_y = 0;
        float somma_z = 0;

        for(int i = 0; i < accelerometerValuesList_Size; i++){
            somma_x += accelerometerValuesList.get(i)[0];
            somma_y += accelerometerValuesList.get(i)[1];
            somma_z += accelerometerValuesList.get(i)[2];
        }

        float media_x = somma_x/accelerometerValuesList_Size;
        float media_y = somma_y/accelerometerValuesList_Size;
        float media_z = somma_z/accelerometerValuesList_Size;

        // calcolo somma dei quadrati degli scarti
        float sommaQuadScarti_x = 0;
        float sommaQuadScarti_y = 0;
        float sommaQuadScarti_z = 0;

        for(int i = 0; i < accelerometerValuesList_Size; i++) {
            sommaQuadScarti_x += Math.pow((accelerometerValuesList.get(i)[0] - media_x), 2);
            sommaQuadScarti_y += Math.pow((accelerometerValuesList.get(i)[1] - media_y), 2);
            sommaQuadScarti_z += Math.pow((accelerometerValuesList.get(i)[2] - media_z), 2);
        }

        // calcolo std dev
        double stddev_x = Math.sqrt(sommaQuadScarti_x/accelerometerValuesList_Size);
        double stddev_y = Math.sqrt(sommaQuadScarti_y/accelerometerValuesList_Size);
        double stddev_z = Math.sqrt(sommaQuadScarti_z/accelerometerValuesList_Size);

        for (int i = 0; i < accelerometerValuesList_Size; i++) {
            if (stddev_x == 0) {
                accelerometerValuesArray[i] = 0;
            }else {
                accelerometerValuesArray[i] = (float) ((instance.getAccFeatures().get(i)[0] - media_x) / stddev_x);
            }

            if (stddev_y == 0) {
                accelerometerValuesArray[++i] = 0;
            }else {
                accelerometerValuesArray[++i] = (float) ((accelerometerValuesList.get(i)[1] - media_y) / stddev_y);
            }

            if (stddev_z == 0) {
                accelerometerValuesArray[++i] = 0;
            }else {
                accelerometerValuesArray[++i] = (float) ((accelerometerValuesList.get(i)[2] - media_z) / stddev_z);
            }
        }
        return accelerometerValuesArray;
    }

    private HashMap<String, Float> getLabel_ProbabilitiesMap() {
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder().build();

        if (null != outputLabels) {
            // mappa delle labels e delle loro corrispondenti probabilità
            TensorLabel labels = new TensorLabel(outputLabels, probabilityProcessor.process(outputs.getProbabilitiesAsTensorBuffer()));
            // creazione della mappa attività-probabilità
            floatMap = (HashMap<String, Float>) labels.getMapWithFloatValue();
        }
        return floatMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getActivityLabel(Map<String, Float> probMap) {
        return Collections.max(probMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public ADLListener getAccListener(){
        return accListener;
    }
}



