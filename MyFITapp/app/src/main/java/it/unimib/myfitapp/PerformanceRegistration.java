package it.unimib.myfitapp;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.DayOfWeek;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

@Entity(tableName = "attività settimanale")
public class PerformanceRegistration {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "day")
    public DayOfWeek day;

    @ColumnInfo(name = "steps")
    public int step;


    @ColumnInfo(name = "time")
    public int time;

    public PerformanceRegistration(int step, int time) {
        this.step = step;
        this.time = time;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Ignore
    public PerformanceRegistration(DayOfWeek day, int step, int time) {
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PerformanceRegistration[] populateData() {
        return new PerformanceRegistration[] {
                new PerformanceRegistration ( MONDAY,500, 2100),
                new PerformanceRegistration (TUESDAY,	1000, 2400),
                new PerformanceRegistration (WEDNESDAY,	500,	6600),
                new PerformanceRegistration (THURSDAY,	1500,	3300),
                new PerformanceRegistration (FRIDAY,	500,	2123),
                new PerformanceRegistration (SATURDAY,	7000,	9000),
                new PerformanceRegistration ( SUNDAY,	10000,	10800)

        };
    }


}
