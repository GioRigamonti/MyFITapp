package it.unimib.adl_library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ADLInstance {
    private final int OVERLAP = 100;
    private final int FRAME = 150;
    private String activity;
    private Map<String, Float> map;
    List<float[]> acc_Features;
    private int i = 0;
    private List<float[]> frame;

    public ADLInstance() {
        this.acc_Features = new ArrayList<float[]>();
        this.activity = null;
        this.map = new HashMap<>();
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setMap(Map probabilityMap) {
        this.map = probabilityMap;
    }

    public Map<String, Float> getMap() {
        return map;
    }

    public void setAccFeatures(float ax, float ay, float az){
        float[] acc = new float[]{ax, ay, az};
        acc_Features.add(acc);
    }

    public List<float[]> getAccFeatures(){
        return acc_Features;
    }

    public void setFrame(){
        if(i == 0) {
            frame=acc_Features.subList(i, i + FRAME);
        }else {
            frame=acc_Features.subList(i - OVERLAP, i + FRAME - OVERLAP);
        }
        i = i + FRAME;
    }

    public List<float[]> getFrame(){
        return frame;
    }

}
