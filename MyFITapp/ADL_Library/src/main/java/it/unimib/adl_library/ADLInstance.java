package it.unimib.adl_library;

public class ADLInstance {
    private String activity;
    private float acc_x, acc_y, acc_z;
    private long timestamp;

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public float getAcc_x() {
        return acc_x;
    }

    public void setAcc_x(float acc_x) {
        this.acc_x = acc_x;
    }

    public float getAcc_y() {
        return acc_y;
    }

    public void setAcc_y(float acc_y) {
        this.acc_y = acc_y;
    }

    public float getAcc_z() {
        return acc_z;
    }

    public void setAcc_z(float acc_z) {
        this.acc_z = acc_z;
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
