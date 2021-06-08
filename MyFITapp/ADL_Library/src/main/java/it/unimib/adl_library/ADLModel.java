package it.unimib.adl_library;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;


import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ADLModel {
    private ADLModel model;
    private Interpreter tflite;
    private Context context;
    private final static String MODEL_FILE = "ADL_Model.tflite";
    //create constructor
    public MappedByteBuffer ADLModel(Context context) throws IOException {
        this.context = context;
        return loadModelFile(context);
    }

    public String getModelPath() {
        return MODEL_FILE;
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
