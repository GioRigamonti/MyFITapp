package it.unimib.myfitapp;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

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

    public synchronized static PerformanceDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }


    public static PerformanceDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context,
                PerformanceDatabase.class,
                "my-database")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                getInstance(context).performanceDao().insertAll(PerformanceRegistration.populateData());
                            }
                        });
                    }
                })
                .build();
    }

}
