package com.quintessential.gypsy;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class syncfromserver extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int syncMax = 0, syncCounter = 0;
    private ConstraintLayout relativeLayout;
    private databaseHandler dH;
    private TextView syncStatus;
    private int numRows = 0;
    private Handler screenOffH1;
    private int countToExit = 0;
    private ProgressBar loading_indicator2;
    private String[] fileName,fileUrl;
    public static String[]fileFromFolder;
    private static Boolean continueDown=false,continueDown2=false;
    public void setFileFromFolder(String[] x,int y){
        fileFromFolder=new String[y];
        fileFromFolder=x;
        Log.d("FILENAME2",fileFromFolder[0]);
    }
    public void setContinueDown(Boolean x){
        continueDown=x;
    }
    public void setContinueDown2(Boolean x){
        continueDown2=x;
    }
    public Handler waitContentListH = new Handler();
    public Handler waitContentListH2 = new Handler();
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable screenOffR1 = new Runnable() {
        @Override
        public void run() {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();

            if (!isScreenOn) {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = ((PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE)).newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                wakeLock.acquire();
                KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                keyguardLock.disableKeyguard();
            }
            screenOffH1.post(screenOffR1);
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable waitContentListR = new Runnable() {
        @Override
        public void run() {
            if(continueDown){
                downloadContent();
            }else{
                waitContentListH.post(waitContentListR);
            }
        }
    };
    public Runnable waitContentListR2 = new Runnable() {
        @Override
        public void run() {
            if(continueDown2){


                deleteUnwanted_continueToFace();
            }else{
                waitContentListH2.post(waitContentListR2);
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        if (exit_activityS.getExitAll() >= 1) {
            finish();
        }
        if (LoginActivity.getExitAll() == 2) {
            this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
            finish();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide
        continueDown=false;continueDown2=false;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        //hide--
        setContentView(R.layout.activity_syncfromserver);

        relativeLayout = findViewById(R.id.syncServer);
        syncStatus = findViewById(R.id.syncStatus);
        loading_indicator2 = findViewById(R.id.loading_indicator2);

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener() {
            public void onSwipeBottom() {
                if (syncStatus.getText() == "Synchronize Fail" || syncStatus.getText() == "No Contents available") {
                    countToExit++;
                    if (countToExit == 3) {
                        Toast.makeText(syncfromserver.this, "Exit permission", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(syncfromserver.this, exit_activityS.class));
                    }
                }
            }

            public void onSwipeTop() {
                if (syncStatus.getText() == "Synchronize Fail" || syncStatus.getText() == "No Contents available") {
                    Toast.makeText(syncfromserver.this, "Refreshing", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }

            public void onSwipeLeft() {
                if (syncStatus.getText() == "Synchronize Fail" || syncStatus.getText() == "No Contents available") {
                    Toast.makeText(syncfromserver.this, "Refreshing", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }

            public void onSwipeRight() {
                if (syncStatus.getText() == "Synchronize Fail" || syncStatus.getText() == "No Contents available") {
                    Toast.makeText(syncfromserver.this, "Refreshing", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }

            public boolean onTouch(View v, MotionEvent event) {
                if (syncStatus.getText() == "Synchronize Fail" || syncStatus.getText() == "No Contents available") {
                    Toast.makeText(syncfromserver.this, "Refreshing", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
        // screenOffH1=new Handler();
        //screenOffH1.post(screenOffR1);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                new preventStatusBarExpansion(this);
        }catch(Exception e){}
//        syncStatus.setTextSize(LoginActivity.getFontSize());
        dH = databaseHandler.getInstance(syncfromserver.this,setIdToDB());
        //ver
        String version="unknown";
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfb = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        String currentTimeb = df2.format(Calendar.getInstance().getTime());
        String datex = dfb.format(c);
        String timex = currentTimeb;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Log.d("VER1",version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        dH.versionAdd(version,datex,timex);
        //ver
        dH.cnAdd(setCNToDB());

        waitContentListH.post(waitContentListR);
        waitContentListH2.post(waitContentListR2);

        downloadContentList();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void downloadContentList() {
        //Download list

        Log.d("FILEPATH",String.valueOf(getFilesDir()+"/content.sqlite3"));
        syncStatus.setText("Checking server list...");
        File gypsyDir = new File(getFilesDir() + "/");
        File outputFile_content = new File(gypsyDir, "content.sqlite3");
        if (outputFile_content.exists()) {
            outputFile_content.delete();
        }
        new DownloadTask("https://gypsy.ph/appphp/tablet/dev/bin/admin/content.sqlite3", "content.sqlite3", String.valueOf(gypsyDir), syncfromserver.this, syncStatus, 1, 1, loading_indicator2,1);
        syncCounter = 0;


    }
    public void downloadContent(){
        //Download contents wait --> waitContentListR
        //Count number of content
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(getFilesDir()+"/content.sqlite3", null, SQLiteDatabase.OPEN_READONLY);
        Log.d("FILEPATH",String.valueOf(getFilesDir()+"/content.sqlite3"));
        String searchQuery = "SELECT COUNT(*) FROM tabletG_videocontent WHERE enabledStatus=1";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        int numOfContents=0;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            numOfContents=cursor.getInt(i);
                        }
                    } catch (Exception e) {
                        Log.d("COUNT_CONTENT_ERROR", e.getMessage());
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        syncMax=numOfContents;
        Log.d("COUNT_CONTENT: ", String.valueOf(numOfContents));
        //Display content in list
        searchQuery = "SELECT videoName,videoLink FROM tabletG_videocontent WHERE enabledStatus=1";
        cursor = myDataBase.rawQuery(searchQuery, null);
        fileName=new String[numOfContents];
        fileUrl=new String[numOfContents];

        int cnt=0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            if (cursor.getColumnName(i).equals("videoName")){
                                fileName[cnt]=cursor.getString(i);
                                Log.d("LIST_CONTENT", fileName[cnt]);
                            }
                            if (cursor.getColumnName(i).equals("videoLink")){
                                fileUrl[cnt]=cursor.getString(i);
                                Log.d("LIST_URL", fileUrl[cnt]);
                                //--
                                if(LoginActivity.getSizeResolutionBoolean()==0) {
                                    String data[] = fileUrl[cnt].split("/");
                                    String doneZ = "https:";
                                    for (String da : data) {
                                        Log.d("DATAL", String.valueOf(da));
                                        if (!da.equals("https:")){
                                            if(da.equals("video_content")){
                                                da="video_content2";
                                            }
                                            doneZ += "/" + da;
                                        }
                                    }
                                    Log.d("DATAL2", String.valueOf(doneZ));
                                    fileUrl[cnt]=doneZ;
                                }
                                //--
                            }
                        }
                    } catch (Exception e) {
                        Log.d("LIST_CONTENT_ERROR", e.getMessage());
                    }
                }
            }
            download2directory2(fileUrl[cnt], fileName[cnt]);
            cnt++;
            cursor.moveToNext();
        }
        cursor.close();

        //Delete unwanted contents --> Downloaind task onpostexecute
        //Next Page
    }

    private void deleteUnwanted_continueToFace() {
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(getFilesDir()+"/content.sqlite3", null, SQLiteDatabase.OPEN_READONLY);
        Log.d("FILEPATH",String.valueOf(getFilesDir()+"/content.sqlite3"));

        for (String oldFileName:fileFromFolder) {
            String searchQuery = "SELECT COUNT(*) FROM tabletG_videocontent WHERE videoName=\""+oldFileName+"\"";
            Cursor cursor = myDataBase.rawQuery(searchQuery, null);
            int needContent=0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int totalColumn = cursor.getColumnCount();
                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                needContent=cursor.getInt(i);
                            }
                        } catch (Exception e) {
                            Log.d("COUNTNEED_CONTENT_ERROR", e.getMessage());
                        }
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
            ///Checking
            if(needContent==0){
                File deleteFile = new File(getFilesDir() + "/",oldFileName);
                deleteFile.delete();
            }
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void download2directory2(String urlHere, String fileName) {
        syncCounter++;
        File gypsyDir = new File(getFilesDir() + "/");
        new DownloadTask(urlHere, fileName, String.valueOf(gypsyDir), syncfromserver.this, syncStatus, syncCounter, syncMax, loading_indicator2,0);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            event.startTracking(); // Needed to track long presses
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void faceBack() {
        String myPath = String.valueOf(getFilesDir().toString() + "/" + "dbServer.sqlite3");
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT file_link, file_type from tabletG_tabletg where date_start <= date('now') AND date_end >= date('now')";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        String faceB = cursor.getString(0);
        String faceT = cursor.getString(1);
        cursor.close();
        download2directory2(faceB, "faceBack");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String loginBack() {
        String myPath = String.valueOf(getFilesDir().toString() + "/" + "dbServer.sqlite3");
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT file_link from tabletG_tabletg_login where date_start <= date('now') AND date_end >= date('now')";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        String loginB = cursor.getString(0);
        cursor.close();
        download2directory2(loginB, "loginBack.png");
        return loginB;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String syncBack() {
        String myPath = String.valueOf(getFilesDir().toString() + "/" + "dbServer.sqlite3");
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT file_link from tabletG_tabletg_sync where date_start <= date('now') AND date_end >= date('now')";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        String syncB = cursor.getString(0);
        cursor.close();
        download2directory2(syncB, "syncBack");
        return syncB;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String exitBack() {
        String myPath = String.valueOf(getFilesDir().toString() + "/" + "dbServer.sqlite3");
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT file_link from tabletG_tabletg_exit where date_start <= date('now') AND date_end >= date('now')";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        String exitB = cursor.getString(0);
        cursor.close();
        download2directory2(exitB, "exitBack");
        return exitB;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String setIdToDB(){

        File file = new File(getFilesDir() + "/", "ID");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
        }
        String idToDB=text.toString()+"_.db";
        Log.d("RETROFIT",idToDB);
        return idToDB;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String setCNToDB(){

        File file = new File(getFilesDir() + "/", "COUNT");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
        }
        String cn=text.toString();
        Log.d("RETROFIT",cn);
        return cn;
    }
    public void versionADD(){

    }
}