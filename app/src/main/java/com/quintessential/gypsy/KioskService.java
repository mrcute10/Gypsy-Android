package com.quintessential.gypsy;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KioskService extends Service {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(1); // periodic interval to check in seconds -> 2 seconds
    private static final String TAG = KioskService.class.getSimpleName();
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";
    private static Context ctx = null;
    private static int homePressed = 0;
    private Thread t = null;
    private boolean running = false;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Context getCtx() {
        return ctx;
    }
    public static int getHomePressed() {
        return homePressed;
    }
    public static void setHomePressed(int i) {
        homePressed = i;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        ctx = this;
        // start a thread that periodically checks if your app is in the foreground
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    handleKioskMode();
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) { }
                } while (running);
                stopSelf();
            }
        });

        t.start();
        return Service.START_NOT_STICKY;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleKioskMode() {
        // is Kiosk Mode active?
        if (isKioskModeActive(this)) {
            // is App in background?
            if (isInBackground()) {
                restoreApp(); // restore!
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isInBackground() {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return (!ctx.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void restoreApp() {
        // Restart activity
        Intent i = new Intent(ctx, FaceTrackerActivity.class);
        homePressed = 1;
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isKioskModeActive(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_KIOSK_MODE, false);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}