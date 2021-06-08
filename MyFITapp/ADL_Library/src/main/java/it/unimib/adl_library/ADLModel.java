package it.unimib.adl_library;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ADLModel {
    private Context context;
    private MappedByteBuffer model;
    private final static String MODEL_FILE = "ADL_Model.tflite";
    private final static String LABEL_FILE = "labels.txt";

    public void ADLModel(Context context) throws IOException {
        this.context = context;
    }

    public MappedByteBuffer getModel() throws IOException {
        return loadModelFile(context);
    }
    public static String getModelPath() {
        return MODEL_FILE;
    }

    public static String getLabelPath() {
        return LABEL_FILE;
    }

    private MappedByteBuffer loadModelFile(Context context)throws IOException{
        //open the model using an input stream and memory map it load
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        Long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



}
