package it.unimib.myfitapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.DayOfWeek;
import java.util.List;

@Dao
public interface PerformanceDao {

    @Insert
    void insertPerformance(PerformanceRegistration performanceRegistration);

    @Insert
    void insertAll(PerformanceRegistration... performanceRegistrations);

    @Query("SELECT * FROM `attività settimanale`")
    List<PerformanceRegistration> getAll();

    @Delete
    void delete(PerformanceRegistration performanceRegistration);

    @Query("DELETE FROM `attività settimanale`")
    void deleteAll();

    @Query("SELECT steps FROM `attività settimanale` WHERE DAY = :day ")
    public int [] readSteps(DayOfWeek day);




}
