package it.unimib.adl_library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADLInstance {
    private final int OVERLAP = 100;
    private final int FRAME = 150;
    private String activity;
    private HashMap<String, Float> map;
    private List<float[]> acc_Features;

    public ADLInstance() {
        this.acc_Features = new ArrayList<float[]>();
        this.map = new HashMap<>();
    }

    public int getOVERLAP() {
        return OVERLAP;
    }

    public int getFRAME() {
        return FRAME;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setMap(HashMap<String, Float> probabilityMap) {
        this.map = probabilityMap;
    }

    public HashMap<String, Float> getMap() {
        return map;
    }

    public void setAccFeatures(float ax, float ay, float az){
        float[] acc = new float[]{ax, ay, az};
        acc_Features.add(acc);
    }

    public List<float[]> getAccFeatures(){
        return acc_Features;
    }
}
