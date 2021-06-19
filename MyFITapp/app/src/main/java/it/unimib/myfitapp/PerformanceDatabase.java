package it.unimib.myfitapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={PerformanceRegistration.class}, version = 1)
public abstract class PerformanceDatabase extends RoomDatabase {
    private static PerformanceDatabase INSTANCE;
    public abstract PerformanceDao performanceDao();

    public static PerformanceDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.inMemoryDatabaseBuilder(context.getApplicationContext(), PerformanceDatabase.class)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }
    public static PerformanceDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), PerformanceDatabase.class, "performance_db")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }

}
