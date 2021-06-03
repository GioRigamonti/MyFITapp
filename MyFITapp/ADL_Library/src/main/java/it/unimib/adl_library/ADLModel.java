package it.unimib.adl_library;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;


import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ADLModel {
    private ADLModel model;
    private Interpreter tflite;
    private Context context;

    //create constructor
    public void ADLModel() {
        try{
            tflite = new Interpreter(loadModelFile(context));

        }catch (Exception e){
            e.printStackTrace();
            Log.e("tfliteSupport", "Error reading model", e);
        }
    }

    // get model method
    public Interpreter getModel(){
        return tflite;
    }



    private MappedByteBuffer loadModelFile(Context context)throws IOException{
        //open the model using an input stream and memory map it load
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("ADL_Model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        Long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



}
