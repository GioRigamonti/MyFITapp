package it.unimib.adl_library;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import it.unimib.adl_library.ml.AdlModel;

public class ADLModel {
    private Context context;
    private AdlModel model;
    private List<String> labels;
    private final static String LABEL_FILE = "labels.txt";

    public ADLModel(Context context) throws IOException {
        this.context = context;
        this.model = loadModelFromMetadata();
        this.labels = loadLabelsFile();
    }

    public AdlModel getModelFromMetadata() {
        return model;
    }

    public List<String> getLabelRead() {
        return labels;
    }

    private AdlModel loadModelFromMetadata() throws IOException {
        return AdlModel.newInstance(context);
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
}
