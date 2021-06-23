package it.unimib.myfitapp;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;

@Entity(tableName = "attività settimanale")
public class PerformanceRegistration {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "day")
    public DayOfWeek day;

    @ColumnInfo(name = "steps")
    public int step;


    @ColumnInfo(name = "time")
    public double time;

    public PerformanceRegistration(int step, double time) {
        this.step = step;
        this.time = time;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Ignore
    public PerformanceRegistration(DayOfWeek day, int step, double time) {
        this.day = day;
        this.step = step;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
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
