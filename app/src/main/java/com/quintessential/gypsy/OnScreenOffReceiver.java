package com.quintessential.gypsy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

public class OnScreenOffReceiver extends BroadcastReceiver {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            AppContext ctx = (AppContext) context.getApplicationContext();
            // is Kiosk Mode active?
            if (isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void wakeUpDevice(AppContext context) {
        PowerManager.WakeLock wakeLock = context.getWakeLock();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        wakeLock.acquire();
        wakeLock.release();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isKioskModeActive(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_KIOSK_MODE, false);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class AppContext extends Application {
        private AppContext instance;
        private PowerManager.WakeLock wakeLock;
        private OnScreenOffReceiver onScreenOffReceiver;

        @Override
        public void onCreate() {
            super.onCreate();
            instance = this;
            registerKioskModeScreenOffReceiver();
            startKioskService();
        }

        private void startKioskService() {
            startService(new Intent(this, KioskService.class));
        }
        private void registerKioskModeScreenOffReceiver() {
            // register screen off receiver
            final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            onScreenOffReceiver = new OnScreenOffReceiver();
            registerReceiver(onScreenOffReceiver, filter);
        }
        @SuppressLint("InvalidWakeLockTag")
        public PowerManager.WakeLock getWakeLock() {
            if (wakeLock == null) {
                // lazy loading: first call, create wakeLock via PowerManager.
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
            }
            return wakeLock;
        }
    }
}