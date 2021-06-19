package it.unimib.myfitapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "attività settimanale")
public class PerformanceRegistration {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "day")
    public int day;

    @ColumnInfo(name = "steps")
    public int step;


    @ColumnInfo(name = "time")
    public double time;

    public PerformanceRegistration(int step, double time) {
        this.step = step;
        this.time = time;
    }
    @Ignore
    public PerformanceRegistration(int id, int step, double time) {
        this.id = id;
        this.day = Calendar.DAY_OF_WEEK;
        this.step = step;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

}
