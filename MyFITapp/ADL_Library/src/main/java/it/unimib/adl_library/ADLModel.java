package it.unimib.adl_library;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

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
}
