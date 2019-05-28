package com.quintessential.gypsy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServiceClass extends Service {
    private static int startTimeHours;
    private static boolean midnightPass=false;
    private static boolean setOnce=false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!setOnce){
            DateFormat df2 = new SimpleDateFormat("HH:mm");
            String currentTime2 = df2.format(Calendar.getInstance().getTime());
            String[] currentTime2b = currentTime2.split(":");
            startTimeHours=Integer.valueOf(currentTime2b[0]);
            Log.d("Servicez", String.valueOf(("RUN1234")));
            setOnce = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static int getStartTimeHours(){
        Log.d("Servicez", String.valueOf((startTimeHours)));
        return startTimeHours;
    }
    public static void setMidnightPass(boolean x){
        midnightPass=x;
    }

    public static boolean getMidnightPass(){
        return midnightPass;
    }
}
