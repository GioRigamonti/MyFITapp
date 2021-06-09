package it.unimib.adl_library;

import java.util.HashSet;
import java.util.List;

public class ADLInstance {
    private String activity;
    private Float acc_x, acc_y, acc_z;
    List<float[]> acc_Features;
    private long timestamp;

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setAcc_tot(float ax, float ay, float az){
        float[] acc = new float[]{ax, ay, az};

        acc_Features.add(acc);
    }

    public List<float[]> getAcc_tot(){
        return acc_Features;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamps) {
        this.timestamp = timestamps;
    }
}
