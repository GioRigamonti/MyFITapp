package it.unimib.adl_library;

import java.util.List;

public class ADLInstance {
    private String activity;
    private static List<Float> acc_x, acc_y, acc_z;
    private long timestamp;


    public void setActivity(String activity) {
        this.activity = activity;
    }

    public List<Float> getAcc_x() {
        return acc_x;
    }

    public void setAcc_x(float ax) {
        this.acc_x.add(ax);
    }

    public List<Float> getAcc_y() {
        return acc_y;
    }

    public void setAcc_y(float ay) {
        this.acc_y.add(ay);
    }

    public List<Float> getAcc_z() {
        return acc_z;
    }

    public void setAcc_z(float az) {
        this.acc_z.add(az);
    }

    public void setAccFeatures(float x, float y, float z){
        setAcc_x(x);
        setAcc_y(y);
        setAcc_z(z);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamps) {
        this.timestamp = timestamps;
    }
}
