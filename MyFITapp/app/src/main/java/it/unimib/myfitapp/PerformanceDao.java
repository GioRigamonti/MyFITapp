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

    @Insert
    void insertAll(PerformanceRegistration ... performanceRegistrations);
    @Query("SELECT * FROM `attività settimanale`")
    List<PerformanceRegistration> getAll();

    @Delete
    void delete(PerformanceRegistration performanceRegistration);

    @Query("DELETE FROM `attività settimanale`")
    void deleteAll();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'MONDAY' ")
    public int readSteps1();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'TUESDAY' ")
    public int readSteps2();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'WEDNESDAY' ")
    public int readSteps3();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'THURSDAY' ")
    public int readSteps4();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'FRIDAY' ")
    public int readSteps5();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'SATURDAY' ")
    public int readSteps6();

    @Query("SELECT steps FROM `attività settimanale` WHERE  day == 'SUNDAY' ")
    public int readSteps7();

    @Query("SELECT time FROM `attività settimanale` WHERE  day == 'SUNDAY' ")
    public double readTime7();




}
