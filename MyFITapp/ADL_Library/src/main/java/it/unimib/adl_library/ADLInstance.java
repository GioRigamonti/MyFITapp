package it.unimib.adl_library;

import java.util.HashSet;
import java.util.List;

public class ADLInstance {
    private String activity;
    List<float[]> acc_Features;
    private long timestamp;

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setAccFeatures(float ax, float ay, float az){
        float[] acc = new float[]{ax, ay, az};

        acc_Features.add(acc);
    }

    public List<float[]> getAccFeatures(){
        return acc_Features;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamps) {
        this.timestamp = timestamps;
    }
}
