package it.unimib.myfitapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.Map;

@Dao
public interface PerformanceDao {

    @Insert
    void insertPerformance(PerformanceRegistration performanceRegistration);

    @Delete
    void delete(PerformanceRegistration performanceRegistration);

    @Query("DELETE FROM `attività settimanale`")
    void deleteAll();

    @Query("SELECT steps FROM `attività settimanale` WHERE day== 6")
    public int[] readSteps();

    @Query("SELECT time FROM `attività settimanale` WHERE day== 6")
    public double[] readTime();

}
