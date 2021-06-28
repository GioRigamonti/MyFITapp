package it.unimib.adl_library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;

public class ADLInstance {
    public final int OVERLAP = 100;
    public final int FRAME = 150;
    private String activity;
    private HashMap<String, Float> map;
    List<float[]> acc_Features;
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

    public void setMap(HashMap probabilityMap) {
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
