package com.chiragzq.barcodebatteryscanner;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SyncBatteryService extends Service {
    public static final int INTERVAL = 1000 * 60; // 1 minute

    private Handler handler = new Handler();
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if(timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("IDK", "RUNNING SERVICE");
                        Toast.makeText(getApplicationContext(), "Synced " + DataManager.getInstance().syncBatteries(getApplicationContext()) + " batteries", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 0, INTERVAL);
    }

}
