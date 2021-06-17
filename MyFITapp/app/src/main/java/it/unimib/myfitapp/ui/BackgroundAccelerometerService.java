package it.unimib.myfitapp.ui;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import it.unimib.adl_library.*;

public class BackgroundAccelerometerService extends Service {
    private Observer observer;

    public BackgroundAccelerometerService() throws Exception {
        this.observer = new Observer(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Observer getObserver() {
        return observer;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG)
                .show();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Start Detecting", Toast.LENGTH_LONG).show();
        observer.getRecognizer().startReadingAccelerometer();
        return Service.START_STICKY;
    }
    public void startForeground() {
        startForeground(1, null);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }

}
