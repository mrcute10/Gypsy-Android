package com.quintessential.gypsy;
/**
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Affectiva Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.affectiva.android.affdex.sdk.detector.CameraDetector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static java.lang.Math.abs;


public class FaceTrackerActivity extends BaseActivity {
    //Object Declaration
    private ConstraintLayout passBack;
    private View dataInsertLayout;
    private TextView textTalk, offer, ageT1, genderT1, thankyou, regularTime, ageOut2, regularDate;
    private Button maleB2, femaleB2, signUp, lowAge, highAge, endTripB;
    private ImageView logoMid, gypsySmallIcon, adsImagePlay, thankyoubackground, faceBack, genAdsImage, gypsySmallIcon2,qrcodeImage,gypsySmallIcon3;
    private VideoView adsVideoPlay, genAdsVideo;
    private EditText emailB3;
    private ProgressBar ratingBar2, ratingBar1;

    //Regular Variable
    private String latitudeX, longtitudeX, cityX = "";
    private int ageGo = 18;
    private boolean ageChangeUp = false;
    private boolean ageChangeDown = false;
    private String ageOut = "";
    private String timeOut = "";
    private String dayOut = "";
    private String genderOut = "";
    private String passIdOut;
    private String emailOut = "";
    private String ratingOut = "";
    private String dateOut = "";
    private String[] videoPlayingList;
    private int countToExit = 0,countToBreak=0;
    private Uri url;
    private CameraDetector detector;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private SurfaceView cameraPreview;
    private boolean faceDetected = false;
    private int ran;
    private boolean isTailAds = false;
    private boolean isFirstAd = true;
    private boolean isEmailDone = false;
    private int currentPausePosition = 0;
    private static boolean loadFaceTrackerActivityDone = false;
    private databaseHandler dH;
    private String adid, passid = "1", dateEndplayed, timeEndplayed, rate, location, latitude, longitude, age, gender, email, contentType, faceCount, smileRate;
    private int maxFaceCount = 0, passengerCount = 1;
    private double smileRateVal = 0;
    private boolean thankYouTrigger = false;
    Button startSDKButton;
    Button surfaceViewVisibilityButton;
    TextView smileTextView,smileTextView2;
    TextView ageTextView;
    TextView genderTextView;
    ToggleButton toggleButton;
    boolean isCameraBack = false;
    boolean isSDKStarted = false;
    private final static int CAMERA_PERMISSIONS_REQUEST_CODE = 0;
    private final static String[] CAMERA_PERMISSIONS_REQUEST = new String[]{Manifest.permission.CAMERA};
    private boolean handleCameraPermissionGrant;
    private String facenow = "N", facecount, facesmile, facegender,faceage;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView batteryTxt;
    private boolean batCameraService=true;
    private WebView webView;
    private boolean coolDownB=false,breakModeOnAds=false;
    private QRGEncoder qrgEncoder;
    private Bitmap QRBitmap;
    private TextView detectionF;
    private int ranZ=0;
    //Static Variable

    //Setters

    //Getters
    public boolean getFaceTrackerActivityDone() {
        return loadFaceTrackerActivityDone;
    }

    //Handler
    public Handler gpsRefreshH, agePressRefreshH, faceH, deviceInterfaceH,coolDownH=new Handler(),randomPickerGenadsH=new Handler();
    //Broadcast
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryTxt.setText(String.valueOf(level) + "%");
            if(level<=10){
                Log.d("BAT2","LOW");
                try{
                    if(batCameraService){
                        //turn off screen
                        screenDim();
                        //Stops camera
                        ((MainApplication)getApplication()).onActivityStopped();
                        //Stops Gps
                        locationManager.removeUpdates(locationListener);
                        Log.d("BAT2","LOW1_GOOD");
                        batCameraService=false;
                    }

                }catch (Exception e){
                    Log.d("BAT2","LOW1_BAD");
                }
            }
            if(level>=20){
                Log.d("BAT2","HIGH");
                try{
                    if(!batCameraService){
                        //turn on screen
                        screenBright();
                        //Start camera
                        ((MainApplication)getApplication()).onActivityStarted();
                        //Start gps
                        locationManager = (LocationManager) FaceTrackerActivity.this.getSystemService(LOCATION_SERVICE);
                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location l) {
                                latitude=String.valueOf(l.getLatitude());
                                longitude=String.valueOf(l.getLongitude());
                                location=cityLoaded(l.getLatitude(),l.getLongitude());
                                Log.d("GPS_STATUS",String.valueOf(location));
                                smileTextView2.setText("GPS Lat:"+String.valueOf(l.getLatitude())+" Lon:"+String.valueOf(l.getLongitude()));
                            }
                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) { }
                            @Override
                            public void onProviderEnabled(String provider) { }
                            @Override
                            public void onProviderDisabled(String provider) { }
                        };

                        if (ActivityCompat.checkSelfPermission(FaceTrackerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FaceTrackerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(FaceTrackerActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                        }else{
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        batCameraService=true;
                    }
                    Log.d("BAT2","HIGH1_GOOD");
                }catch (Exception e){
                    Log.d("BAT2","HIGH1_BAD");
                }
            }

        }
    };
    //Runnable
    private Runnable randomPickerGenadsR=new Runnable() {
        @Override
        public void run() {
            try {
                Calendar calendar = Calendar.getInstance();
                int dayz = calendar.get(Calendar.DAY_OF_WEEK);
                DateFormat df2b = new SimpleDateFormat("HH:mm");
                String currentTime2 = df2b.format(Calendar.getInstance().getTime());
                String[] currentTime2b = currentTime2.split(":");
                int currentTime3 = Integer.valueOf(currentTime2b[0]) * 60 * 60 + Integer.valueOf(currentTime2b[1]) * 60;
                timeOut = String.valueOf(currentTime3);
                switch (dayz) {
                    case Calendar.SUNDAY:
                        dayOut = "N";
                        break;
                    case Calendar.MONDAY:
                        dayOut = "M";
                        break;
                    case Calendar.TUESDAY:
                        dayOut = "T";
                        break;
                    case Calendar.WEDNESDAY:
                        dayOut = "W";
                        break;
                    case Calendar.THURSDAY:
                        dayOut = "H";
                        break;
                    case Calendar.FRIDAY:
                        dayOut = "F";
                        break;
                    case Calendar.SATURDAY:
                        dayOut = "S";
                        break;
                }
                Random r = new Random();
                ran = r.nextInt(2);
                if (ran == 0) {
                    generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(7) + " AND maxAge >= " + Integer.valueOf(80) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'all\') AND dayPlay LIKE \'" + dayOut + "\' AND timeStartPlaySeconds <= " + Integer.valueOf(timeOut) + " AND timeEndPlaySeconds >= " + Integer.valueOf(timeOut) + "", "SELECT videoName FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(7) + " AND maxAge >= " + Integer.valueOf(80) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'all\') AND dayPlay LIKE \'" + dayOut + "\' AND timeStartPlaySeconds <= " + Integer.valueOf(timeOut) + " AND timeEndPlaySeconds >= " + Integer.valueOf(timeOut) + "");
                    if(videoPlayingList.length==0){
                        generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE contentType=\"G\"", "SELECT videoName FROM tabletG_videocontent WHERE  contentType=\"G\"");
                        Log.d("PICK", "BACKUP");
                    }
                    Log.d("PICK", "1 --> " + String.valueOf(videoPlayingList.length));
                } else {
                    generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(7) + " AND maxAge >= " + Integer.valueOf(80) + " AND (gender LIKE \'all\') AND dayPlay LIKE \'" + dayOut + "\' AND timeStartPlaySeconds <= " + Integer.valueOf(timeOut) + " AND timeEndPlaySeconds >= " + Integer.valueOf(timeOut) + "", "SELECT videoName FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(7) + " AND maxAge >= " + Integer.valueOf(80) + " AND (gender LIKE \'all\') AND dayPlay LIKE \'" + dayOut + "\' AND timeStartPlaySeconds <= " + Integer.valueOf(timeOut) + " AND timeEndPlaySeconds >= " + Integer.valueOf(timeOut) + "");
                    if(videoPlayingList.length==0){
                        generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE contentType=\"G\"", "SELECT videoName FROM tabletG_videocontent WHERE  contentType=\"G\"");
                        Log.d("PICK", "BACKUP");
                    }
                    Log.d("PICK", "2 --> " + String.valueOf(videoPlayingList.length));
                }

            } catch (Exception e) {
                generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE contentType=\"G\"", "SELECT videoName FROM tabletG_videocontent WHERE  contentType=\"G\"");
                Log.d("PICK", "3");
            }

        }
    };

    private Runnable trackerR = new Runnable() {
        @Override
        public void run() {
            updateGPS();
            //  gpsRefreshH.postDelayed(trackerR, 900000);
        }
    };
    private Runnable trackerR2 = new Runnable() {
        @Override
        public void run() {
            updateGPS();
        }
    };

    public Runnable ageR = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            if (ageChangeDown) {
                lowAge.setBackgroundResource(R.drawable.roundbuttonoff);
                lowAge.setTextColor(Color.rgb(102, 67, 122));
                if (ageGo >= 8) {
                    ageOut2.setText(" " + String.valueOf(--ageGo) + " ");
                }
                lowAge.setTextColor(Color.WHITE);
                lowAge.setBackgroundResource(R.drawable.roundbutton);
                agePressRefreshH.postDelayed(ageR, 100);

            }
            if (ageChangeUp) {
                highAge.setBackgroundResource(R.drawable.roundbuttonoff);
                highAge.setTextColor(Color.rgb(102, 67, 122));
                if (ageGo <= 79) {
                    ageOut2.setText(" " + String.valueOf(++ageGo) + " ");
                }
                highAge.setTextColor(Color.WHITE);
                highAge.setBackgroundResource(R.drawable.roundbutton);
                agePressRefreshH.postDelayed(ageR, 100);

            }
        }
    };
    private Runnable faceR1 = new Runnable() {
        @Override
        public void run() {
            logoMid.setVisibility(View.GONE);
            Random r = new Random();
            int ran = r.nextInt(4);
            generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE contentType=\"G\"", "SELECT videoName FROM tabletG_videocontent WHERE  contentType=\"G\"");
            File f = new File(String.valueOf(getDatabasePath(setIdToDB())));
            if (f.exists()) {
                passid = String.valueOf(lastPassengerId() + 1);
            }
            textTalk.setVisibility(View.VISIBLE);

            switch(ran){
                case 0:
                    textTalk.setText("Hello");
                    break;
                case 1:
                    textTalk.setText("Hi");
                    break;
                case 2:
                    textTalk.setText("Hey");
                    break;
                case 3:
                    textTalk.setText("Mabuhay");
                    break;
            }

            if (ran == 0) {

            } else {
            }
            smileRateVal = 0;
            smileRate = "";
            faceH.postDelayed(faceR2, 2000);
            breakModeOnAds=true;
        }
    };
    private Runnable faceR2 = new Runnable() {
        @Override
        public void run() {
            textTalk.setText("Welcome to GYPSY");
            faceH.postDelayed(faceR3, 3000);

        }
    };
    private Runnable faceR3 = new Runnable() {
        @Override
        @SuppressLint("ClickableViewAccessibility")
        public void run() {
            Log.d("CITYX", cityX);
            if (cityX.equals("OFF")) {

                dataInsertLayout.setVisibility(View.INVISIBLE);
            } else {
                gypsySmallIcon3.setVisibility(View.VISIBLE);
                qrcodeImage.setVisibility(View.VISIBLE);

                offer.setVisibility(View.VISIBLE);
                dataInsertLayout.setVisibility(View.VISIBLE);
                if(LoginActivity.getSizeResolutionBoolean()==1)  gypsySmallIcon.setVisibility(View.VISIBLE);
                ageT1.setVisibility(View.VISIBLE);
                genderT1.setVisibility(View.VISIBLE);
                lowAge.setVisibility(View.VISIBLE);
                ageOut2.setVisibility(View.VISIBLE);
                highAge.setVisibility(View.VISIBLE);
                maleB2.setVisibility(View.VISIBLE);
                femaleB2.setVisibility(View.VISIBLE);
                emailB3.setVisibility(View.VISIBLE);
                signUp.setVisibility(View.VISIBLE);
            }
            textTalk.setText(" ");
            genAdsVideo.setVisibility(View.VISIBLE);
            ratingBar1.setVisibility(View.VISIBLE);
            genAdsImage.setVisibility(View.GONE);
            //passIdOut = String.valueOf(passIdO);
            try {
                videoPlay();
            }catch(Exception e){
                genAdsImage.setVisibility(View.INVISIBLE);
                genAdsVideo.setVisibility(View.INVISIBLE);
                ratingBar1.setVisibility(View.INVISIBLE);
                logoMid.setVisibility(View.VISIBLE);
            }

        }
    };
    private Runnable faceR4 = new Runnable() {
        @Override
        public void run() {
            genAdsVideo.stopPlayback();
            adsVideoPlay.setVisibility(View.VISIBLE);
            adsImagePlay.setVisibility(View.VISIBLE);
            gypsySmallIcon2.setVisibility(View.VISIBLE);
            endTripB.setVisibility(View.INVISIBLE);
            regularTime.setVisibility(View.VISIBLE);
            regularDate.setVisibility(View.VISIBLE);
            ratingBar2.setVisibility(View.VISIBLE);

            textTalk.setVisibility(View.INVISIBLE);
            logoMid.setVisibility(View.INVISIBLE);
            //genAdsImage.setVisibility(View.GONE);
            genAdsVideo.setVisibility(View.GONE);
            dataInsertLayout.setVisibility(View.INVISIBLE);
            offer.setVisibility(View.INVISIBLE);
            ageT1.setVisibility(View.INVISIBLE);
            genderT1.setVisibility(View.INVISIBLE);

            maleB2.setVisibility(View.INVISIBLE);
            femaleB2.setVisibility(View.INVISIBLE);
            lowAge.setVisibility(View.INVISIBLE);
            highAge.setVisibility(View.INVISIBLE);
            ageOut2.setVisibility(View.INVISIBLE);
            emailB3.setVisibility(View.INVISIBLE);
            signUp.setVisibility(View.INVISIBLE);
            ratingBar1.setVisibility(View.INVISIBLE);

            gypsySmallIcon3.setVisibility(View.INVISIBLE);
            qrcodeImage.setVisibility(View.INVISIBLE);
            videoPlayTail();

        }
    };
    public Runnable nofaceR = new Runnable() {
        @Override
        public void run() {

            if (!thankYouTrigger) {
                thankYouTrigger = true;
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                String currentTime = df2.format(Calendar.getInstance().getTime());
                dateEndplayed = df.format(c);
                timeEndplayed = currentTime;
                if (!isTailAds) {
                    genAdsVideo.stopPlayback();
                    //datacollected G.E
                    rate = String.valueOf(ratingBar1.getProgress());
                    dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                    dH.dbAdd(
                            adid,
                            passid,
                            dateEndplayed,
                            timeEndplayed,
                            rate,
                            location,
                            latitude,
                            longitude,
                            age,
                            gender,
                            email,
                            "G.E",
                            faceCount,
                            smileRate
                    );
                } else {
                    adsVideoPlay.stopPlayback();
                    //datacollected T.E
                    rate = String.valueOf(ratingBar2.getProgress());
                    dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                    dH.dbAdd(
                            adid,
                            passid,
                            dateEndplayed,
                            timeEndplayed,
                            rate,
                            location,
                            latitude,
                            longitude,
                            age,
                            gender,
                            email,
                            "T.E",
                            faceCount,
                            smileRate
                    );
                }

                gypsySmallIcon3.setVisibility(View.INVISIBLE);
                qrcodeImage.setVisibility(View.INVISIBLE);

                dataInsertLayout.setVisibility(View.INVISIBLE);
                thankyou.setVisibility(View.VISIBLE);
                thankyoubackground.setVisibility(View.VISIBLE);
                adsImagePlay.setVisibility(View.INVISIBLE);
                adsVideoPlay.setVisibility(View.INVISIBLE);
                endTripB.setVisibility(View.INVISIBLE);
                ratingBar2.setVisibility(View.INVISIBLE);
                regularTime.setVisibility(View.INVISIBLE);
                regularDate.setVisibility(View.INVISIBLE);
                ageOut2.setText("18");
                emailB3.setText("");
                ageOut = "";
                age = "";
                genderOut = "";
                gender = "";
                emailOut = "";
                email = "";
                //agePressRefreshH.removeCallbacks(ageR);
                //faceH.removeCallbacks(nofaceR);
                faceH.postDelayed(nofaceR2, 5000);
            }/*else{
                faceH.removeCallbacks(nofaceR);
            }*/

        }
    };
    public Runnable nofaceR2 = new Runnable() {
        @Override
        public void run() {
            countToExit=0;

            gypsySmallIcon3.setVisibility(View.INVISIBLE);
            qrcodeImage.setVisibility(View.INVISIBLE);

            thankyou.setVisibility(View.INVISIBLE);
            thankyoubackground.setVisibility(View.INVISIBLE);
            dataInsertLayout.setVisibility(View.INVISIBLE);
            logoMid.setVisibility(View.VISIBLE);
            genAdsVideo.setVisibility(View.INVISIBLE);
            genAdsImage.setVisibility(View.GONE);
            adsVideoPlay.setVisibility(View.INVISIBLE);
            adsImagePlay.setVisibility(View.INVISIBLE);
            thankyoubackground.setVisibility(View.INVISIBLE);
            thankyou.setVisibility(View.INVISIBLE);
            if(LoginActivity.getSizeResolutionBoolean()==1)   gypsySmallIcon.setVisibility(View.INVISIBLE);
            gypsySmallIcon2.setVisibility(View.INVISIBLE);
            endTripB.setVisibility(View.INVISIBLE);
            regularTime.setVisibility(View.INVISIBLE);
            ratingBar2.setVisibility(View.INVISIBLE);
            maleB2.setTextColor(Color.WHITE);
            maleB2.setBackgroundResource(R.drawable.roundbutton);
            femaleB2.setTextColor(Color.WHITE);
            femaleB2.setBackgroundResource(R.drawable.roundbutton);
            regularDate.setVisibility(View.INVISIBLE);
            ratingBar1.setVisibility(View.INVISIBLE);
            // gpsRefreshH.post(trackerR);
            maxFaceCount = 0;
            // passengerCount++;
            // passid = String.valueOf(passengerCount);
            faceDetected = false;
            thankYouTrigger = false;
            smileRateVal = 0;
            ageGo = 18;
            genderOut = "";
            emailOut = "";
            breakModeOnAds=false;
            //cityX = "OFF";
            signUp.setEnabled(true);
            faceH.removeCallbacksAndMessages(null);
            faceH = new Handler();
        }
    };
    public Runnable timeR = new Runnable() {
        @Override
        public void run() {
            DateFormat df = new SimpleDateFormat("HH");
            String currentTime = df.format(Calendar.getInstance().getTime());
            /*
            if(Integer.valueOf(currentTime)==0){
                ServiceClass.setMidnightPass(true);
            }*/
            if (Integer.valueOf(currentTime) < 18 && Integer.valueOf(currentTime) >= 6) {
                //Day
                passBack.setBackgroundResource(R.drawable.backsync);
                textTalk.setTextColor(getResources().getColor(R.color.gypsyColor5));
                textTalk.setShadowLayer(5, 1, 1, R.color.gypsyColor1);
                regularTime.setTextColor(getResources().getColor(R.color.gypsyColor5));
                regularTime.setShadowLayer(5, 1, 1, R.color.gypsyColor1);
                regularDate.setTextColor(getResources().getColor(R.color.gypsyColor5));
                regularDate.setShadowLayer(5, 1, 1, R.color.gypsyColor1);
            } else {
                //night
                //passBack.setBackgroundResource(R.drawable.backsync2);
                passBack.setBackgroundColor(Color.WHITE);
                textTalk.setTextColor(getResources().getColor(R.color.gypsyColor1));
                textTalk.setShadowLayer(5, 1, 1, R.color.gypsyColor5);
                regularTime.setTextColor(getResources().getColor(R.color.gypsyColor1));
                regularTime.setShadowLayer(5, 1, 1, R.color.gypsyColor5);
                regularDate.setTextColor(getResources().getColor(R.color.gypsyColor1));
                regularDate.setShadowLayer(5, 1, 1, R.color.gypsyColor5);
            }
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat dfb = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            String currentTimeb = df2.format(Calendar.getInstance().getTime());
            dateEndplayed = dfb.format(c);
            timeEndplayed = currentTimeb;
            Date cz = Calendar.getInstance().getTime();
            SimpleDateFormat dfbz = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat df2z = new SimpleDateFormat("HH:mm");
            String currentTimebz = df2z.format(Calendar.getInstance().getTime());
            regularDate.setText(dfbz.format(cz));
            regularTime.setText(currentTimebz);
            if((exit_activityS.getExitAll() != 9) ){
                setLogoutDatetoFile(FaceTrackerActivity.this,dateEndplayed);
                setLogoutTimetoFile(FaceTrackerActivity.this,timeEndplayed);
            }

            deviceInterfaceH.post(screenR);
            //deviceInterfaceH.post(timeR);
        }
    };
    public Runnable screenR = new Runnable() {
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
            try{//BETA
                getActionBar().hide();
            }catch (Exception e){
                //Cant hide
            }
            deviceInterfaceH.post(timeR);
        }
    };
    public Runnable coolDownR = new Runnable() {
        @Override
        public void run() {
            coolDownB=false;
            coolDownH.removeCallbacks(coolDownR);
        }
    };

    //System Function
    @SuppressLint({"ClickableViewAccessibility", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_face_tracker);
        //Set Parameter to OBJECTS
        passBack = findViewById(R.id.passBack);
        dataInsertLayout = findViewById(R.id.dataInsertLayout);
        textTalk = findViewById(R.id.textTalk);
        maleB2 = findViewById(R.id.maleB2);
        femaleB2 = findViewById(R.id.femaleB2);
        gypsySmallIcon = findViewById(R.id.gypsySmallIcon);
        ageT1 = findViewById(R.id.ageT1);
        genderT1 = findViewById(R.id.genderT1);
        faceBack = findViewById(R.id.faceBack);
        signUp = findViewById(R.id.signUp);
        lowAge = findViewById(R.id.lowAge);
        highAge = findViewById(R.id.highAge);
        emailB3 = findViewById(R.id.emailB3);
        offer = findViewById(R.id.offer);
        regularTime = findViewById(R.id.regularTime);
        regularDate = findViewById(R.id.regularDate);
        endTripB = findViewById(R.id.endTripB);
        ratingBar2 = findViewById(R.id.ratingBar2);
        ratingBar1 = findViewById(R.id.ratingBar1);
        adsVideoPlay = findViewById(R.id.adsVideoPlay);
        adsImagePlay = findViewById(R.id.adsImagePlay);
        thankyou = findViewById(R.id.thankyou);
        thankyoubackground = findViewById(R.id.thankyoubackground);
        gypsySmallIcon2 = findViewById(R.id.gypsySmallIcon2);
        ageOut2 = findViewById(R.id.ageOut2);
        logoMid = findViewById(R.id.logoMid);
        genAdsImage = findViewById(R.id.genAdsImage);
        genAdsVideo = findViewById(R.id.genAdsVideo);
        qrcodeImage=findViewById(R.id.qrcodeImage);
        gypsySmallIcon3=findViewById(R.id.gypsySmallIcon3);
        detectionF=findViewById(R.id.detectionF);
        //Set OnCreate Objects Here
        gypsySmallIcon3.setVisibility(View.INVISIBLE);
        qrcodeImage.setVisibility(View.INVISIBLE);
        dataInsertLayout.setVisibility(View.INVISIBLE);
        logoMid.setVisibility(View.VISIBLE);
        adsVideoPlay.setVisibility(View.INVISIBLE);
        adsImagePlay.setVisibility(View.INVISIBLE);
        thankyoubackground.setVisibility(View.INVISIBLE);
        thankyou.setVisibility(View.INVISIBLE);
        if(LoginActivity.getSizeResolutionBoolean()==1)   gypsySmallIcon.setVisibility(View.INVISIBLE);
        gypsySmallIcon2.setVisibility(View.INVISIBLE);
        endTripB.setVisibility(View.INVISIBLE);
        regularTime.setVisibility(View.INVISIBLE);
        ratingBar2.setVisibility(View.INVISIBLE);
        maleB2.setTextColor(Color.WHITE);
        maleB2.setBackgroundResource(R.drawable.roundbutton);
        femaleB2.setTextColor(Color.WHITE);
        femaleB2.setBackgroundResource(R.drawable.roundbutton);
        regularDate.setVisibility(View.INVISIBLE);
        ratingBar1.setVisibility(View.INVISIBLE);
        ratingBar1.setProgress(10);
        ratingBar2.setProgress(10);
        regularDate.setTextSize(LoginActivity.getFontSize());
        regularTime.setTextSize(LoginActivity.getFontSize());
        if(LoginActivity.getSizeResolutionBoolean()==0) {
            dataInsertLayout.getLayoutParams().height = 120;
            offer.setText("");
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ageT1.getLayoutParams();
            params.topMargin = 5;
            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) regularTime.getLayoutParams();
            params2.rightMargin=20;
            ViewGroup.MarginLayoutParams params3 = (ViewGroup.MarginLayoutParams) ratingBar2.getLayoutParams();
            params3.leftMargin=20;

        }
        //Set System Function
        batteryTxt = (TextView) this.findViewById(R.id.batteryTxt);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                new preventStatusBarExpansion(this);
        }catch (Exception e){}
        if (KioskService.getHomePressed() == 1) {
            KioskService.setHomePressed(0);
            this.finish();
        }
        /*
        if(!isMyServiceRunning(ServiceClass.class)){
            startService(new Intent(this,ServiceClass.class));
        }*/
        if (getIntent().getBooleanExtra("EXIT", false)) { finish(); }

        //Set listener here
        passBack.setOnTouchListener(new OnSwipeTouchListener() {
            public void onSwipeBottom() {
                if(!breakModeOnAds) {
                    countToExit++;
                    if (countToExit == 3) {
                        Toast.makeText(FaceTrackerActivity.this, "Exit permission", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FaceTrackerActivity.this, exit_activityS.class));
                    }
                    countToBreak = 0;
                }
            }
            public  void onSwipeLeft(){
                if(!breakModeOnAds) {
                    countToBreak++;
                    Log.d("swipe", String.valueOf(countToBreak));
                    if (countToBreak == 5) {
                        Toast.makeText(FaceTrackerActivity.this, "Break mode ON", Toast.LENGTH_SHORT).show();

                        //turn off screen
                        screenDim();
                        //Stops camera
                        try {
                            ((MainApplication) getApplication()).onActivityStopped();
                        } catch (Exception e) {
                        }

                        //Stops Gps
                        locationManager.removeUpdates(locationListener);
                        // Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, 100);//turn off screen
                    }
                    coolDownB = true;
                    coolDownH.postDelayed(coolDownR, 10000);
                    countToExit = 0;
                }
            }
            public void onSwipeRight(){
                if(!breakModeOnAds) {
                    Log.d("swipe", String.valueOf(countToBreak));
                    if (!coolDownB) {
                        if (countToBreak >= 5) {
                            Toast.makeText(FaceTrackerActivity.this, "Break mode OFF", Toast.LENGTH_SHORT).show();

                            //turn on screen
                            screenBright();
                            //Start camera
                            try {
                                ((MainApplication) getApplication()).onActivityStarted();
                            } catch (Exception e) {
                            }

                            //Start gps
                            locationManager = (LocationManager) FaceTrackerActivity.this.getSystemService(LOCATION_SERVICE);
                            locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location l) {
                                    latitude = String.valueOf(l.getLatitude());
                                    longitude = String.valueOf(l.getLongitude());
                                    location = cityLoaded(l.getLatitude(), l.getLongitude());
                                    Log.d("GPS_STATUS", String.valueOf(location));
                                    smileTextView2.setText("GPS Lat:" + String.valueOf(l.getLatitude()) + " Lon:" + String.valueOf(l.getLongitude()));
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                }

                                @Override
                                public void onProviderEnabled(String provider) {
                                }

                                @Override
                                public void onProviderDisabled(String provider) {
                                }
                            };

                            if (ActivityCompat.checkSelfPermission(FaceTrackerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FaceTrackerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(FaceTrackerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            }
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                            countToBreak = 0;
                            countToExit = 0;
                        }
                    } else {
                        Toast.makeText(FaceTrackerActivity.this, "Camera is on cooldown, wait for a few seconds", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            public boolean onTouch(View v, MotionEvent event) {
                if (isEmailDone) {
                    genAdsVideo.seekTo(currentPausePosition);
                    genAdsVideo.start();
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
        emailB3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //keyInteract=true;
                    isEmailDone = true;
                    genAdsVideo.pause();
                    currentPausePosition = genAdsVideo.getCurrentPosition();
                } else {
                    hideKeyboard(v);


                }
            }
        });
        emailB3.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    genAdsVideo.seekTo(currentPausePosition);
                    genAdsVideo.start();
                }
                return false;
            }
        });
        lowAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowAge.setBackgroundResource(R.drawable.roundbuttonoff);
                lowAge.setTextColor(Color.rgb(102, 67, 122));
                if (ageGo >= 8) {
                    ageOut2.setText(" " + String.valueOf(--ageGo) + " ");
                }
                lowAge.setTextColor(Color.WHITE);
                lowAge.setBackgroundResource(R.drawable.roundbutton);
            }
        });
        highAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highAge.setBackgroundResource(R.drawable.roundbuttonoff);
                highAge.setTextColor(Color.rgb(102, 67, 122));
                if (ageGo <= 79) {
                    ageOut2.setText(" " + String.valueOf(++ageGo) + " ");
                }
                highAge.setTextColor(Color.WHITE);
                highAge.setBackgroundResource(R.drawable.roundbutton);
            }
        });

        lowAge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ageChangeDown = true;
                agePressRefreshH.post(ageR);
                return false;
            }
        });
        lowAge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ageChangeDown = false;
                }
                return false;
            }
        });
        highAge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ageChangeUp = false;
                }
                return false;
            }
        });
        highAge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ageChangeUp = true;
                agePressRefreshH.post(ageR);
                return false;
            }
        });
        maleB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleB2.setBackgroundResource(R.drawable.roundbuttonoff);
                maleB2.setTextColor(Color.rgb(102, 67, 122));
                femaleB2.setTextColor(Color.WHITE);
                femaleB2.setBackgroundResource(R.drawable.roundbutton);
                genderOut = "male";
            }
        });
        femaleB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleB2.setBackgroundResource(R.drawable.roundbuttonoff);
                femaleB2.setTextColor(Color.rgb(102, 67, 122));
                maleB2.setTextColor(Color.WHITE);
                maleB2.setBackgroundResource(R.drawable.roundbutton);
                genderOut = "female";
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CITYX", "PRESS");
                Log.d("CITYX", ageOut);
                Log.d("CITYX", genderOut);
                signUp.setBackgroundResource(R.drawable.roundbuttonoff);
                signUp.setTextColor(Color.rgb(102, 67, 122));
                signUp.setEnabled(false);
                ageOut = ageOut2.getText().toString().trim();
                age = ageOut;
                gender = genderOut;
                emailOut = emailB3.getText().toString().trim();
                email = emailB3.getText().toString().trim();
                rate = String.valueOf(ratingBar1.getProgress());
                if (ageOut != "" && genderOut != "") {

                    //Datacollected G.D
                    dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                    dH.dbAdd(
                            adid,
                            passid,
                            dateEndplayed,
                            timeEndplayed,
                            rate,
                            location,
                            latitude,
                            longitude,
                            age,
                            gender,
                            email,
                            "G.D",
                            faceCount,
                            smileRate
                    );
                    isTailAds = true;
                    signUp.setTextColor(Color.WHITE);
                    signUp.setBackgroundResource(R.drawable.roundbutton);
                    faceH.postDelayed(faceR4, 500);
                } else {
                    Toast.makeText(FaceTrackerActivity.this, "Invalid Data", Toast.LENGTH_LONG);
                    signUp.setTextColor(Color.WHITE);
                    signUp.setBackgroundResource(R.drawable.roundbutton);
                    signUp.setEnabled(true);
                }
            }
        });
        endTripB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceH.removeCallbacks(nofaceR);
                faceH.post(nofaceR);
            }
        });
        //Load Face Background
        Bitmap decoded = null;
        try {
            url = Uri.fromFile(new File(getFilesDir().toString() + "/" + "faceBack.png"));
            InputStream imageStream = getContentResolver().openInputStream(url);
            Bitmap original = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 100, out);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            faceBack.setVisibility(View.GONE);
            Log.d("Imggg", String.valueOf(e));
        }
        faceBack.setImageBitmap(decoded);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String fnow = intent.getStringExtra(DetectorService.FACENOW);
                        String[] parts = fnow.split("@");
                        facenow = parts[0];
                        facecount = parts[1];
                        facesmile = parts[2];
                        facegender = parts[3];
                        faceage=parts[4];
                        Log.d("AGE1",faceage);
                        //detectionF.setText("GENDER: "+facegender+" AGE: "+faceage);

                        if (facenow.equals("N")) {
                            smileTextView.setText("N");
                            ageTextView.setText("");
                            genderTextView.setText("");
                            if (faceDetected) {
                                faceH.postDelayed(nofaceR, 300000);
                            }
                        }
                        if (facenow.equals("Y")) {
                            if (Integer.valueOf(facecount) > maxFaceCount) {
                                maxFaceCount = Integer.valueOf(facecount);
                                faceCount = String.valueOf(maxFaceCount);
                            }
                            if (Double.valueOf(facesmile) > smileRateVal) {
                                smileRateVal = Double.valueOf(facesmile);
                                smileRate = facesmile;
                            }

                            if (!isTailAds) {
                                genderTextView.setText(String.valueOf(facegender));
                                gender = facegender;
                            }
                            if (!faceDetected) {
                                faceDetected = true;
                                faceH.post(faceR1);
                                //   gpsRefreshH.removeCallbacks(trackerR);
                            } else {
                                faceH.removeCallbacks(nofaceR);
                            }
                            smileTextView.setText("F " + facecount);

                        }

                    }
                }, new IntentFilter(DetectorService.FACEON)
        );
        //gps
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location l) {
                latitude=String.valueOf(l.getLatitude());
                longitude=String.valueOf(l.getLongitude());
                location=cityLoaded(l.getLatitude(),l.getLongitude());
                Log.d("GPS_STATUS",String.valueOf(location));
                smileTextView2.setText("GPS Lat:"+String.valueOf(l.getLatitude())+" Lon:"+String.valueOf(l.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        //Camera Setup
        smileTextView = findViewById(R.id.smile_textview);
        smileTextView2 = findViewById(R.id.smile_textview2);
        ageTextView = findViewById(R.id.age_textview);
        genderTextView = findViewById(R.id.gender_textview);
        toggleButton = findViewById(R.id.front_back_toggle_button);
        startSDKButton = findViewById(R.id.sdk_start_button);
        surfaceViewVisibilityButton = findViewById(R.id.surfaceview_visibility_button);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        cameraPreview = new SurfaceView(this) {
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
                setMeasuredDimension(1, 1);
            }
        };
        cameraPreview.setLayoutParams(params);
        passBack.addView(cameraPreview, 0);

        wifiSwitch(false);
        data3GSwitch(false);
        // gpsRefreshH = new Handler();
        //agePressRefreshH = new Handler();
        faceH = new Handler();
        deviceInterfaceH = new Handler();
        // gpsRefreshH.post(trackerR);
        deviceInterfaceH.post(timeR);
        loadFaceTrackerActivityDone = true;
        offerUpdate();


        webView=findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setAppCacheMaxSize(5*1024*1024);//5MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default
        webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        webView.addJavascriptInterface(new WebAppInterface(FaceTrackerActivity.this), "Android");
        webView.loadUrl("file:///android_asset/home.html");

        //webView.loadUrl( "http://www.google.com" );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

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

        if (handleCameraPermissionGrant) {
            // a response to our camera permission request was received
            if (CameraHelper.checkPermission(this)) {
                startService(new Intent(this, DetectorService.class));
            } else {
                ((TextView)findViewById(R.id.text)).setText("Camera Permission Denied");
            }
            handleCameraPermissionGrant = false;
        }
        faceH=new Handler();
        breakModeOnAds=false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        breakModeOnAds=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if((exit_activityS.getExitAll() != 9) ){
            //onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            File logoutTimeFailFile=new File(getFilesDir() + "/", "LOGOUT_TIME");
            File logoutDateFailFile=new File(getFilesDir() + "/", "LOGOUT_DATE");
            logoutDateFailFile.delete();
            logoutTimeFailFile.delete();
        }catch(Exception e){

        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void wifiSwitch(boolean x) {
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
            wifiManager.setWifiEnabled(x);
        } catch (Exception e) {
        }
    }

    public void data3GSwitch(boolean x) {
        try {
            ConnectivityManager dataManager;
            dataManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);
            dataMtd.invoke(dataManager, x);      //True - to enable data connectivity .
        } catch (Exception e) {
        }
    }

    private void updateGPS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            final Location[] locationX = new Location[1];
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    locationX[0] = location;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (locationX[0] == null) {
                //locationX[0] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationX[0] = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            try {
                latitudeX = String.valueOf(locationX[0].getLatitude());
                longtitudeX = String.valueOf(locationX[0].getLongitude());
                cityX = cityLoaded(locationX[0].getLatitude(), locationX[0].getLongitude());
                latitude = latitudeX;
                longitude = longtitudeX;
                location = cityX;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String cityLoaded(double Lat, double Lon) {
        String[] City = new String[70];
        double[] Latitude = new double[70];
        double[] Longitude = new double[70];

        City[0] = "Caloocan";
        Latitude[0] = 14.5176184;
        Longitude[0] = 121.05086449999999;
        City[1] = "Las Pinas";
        Latitude[1] = 14.444546;
        Longitude[1] = 120.99387360000003;
        City[2] = "Makati";
        Latitude[2] = 14.554729;
        Longitude[2] = 121.02444519999995;
        City[3] = "Malabon";
        Latitude[3] = 14.6680747;
        Longitude[3] = 120.96584540000003;
        City[4] = "Mandaluyong";
        Latitude[4] = 14.5794443;
        Longitude[4] = 121.03591740000002;
        City[5] = "Manila";
        Latitude[5] = 14.5995124;
        Longitude[5] = 120.9842195;
        City[6] = "Marikina";
        Latitude[6] = 14.65073;
        Longitude[6] = 121.1028546;
        City[7] = "Muntinlupa";
        Latitude[7] = 14.4081327;
        Longitude[7] = 121.0414667;
        City[8] = "Navotas";
        Latitude[8] = 14.6714904;
        Longitude[8] = 120.93984669999998;
        City[9] = "Paranaque";
        Latitude[9] = 14.4793095;
        Longitude[9] = 121.01982290000001;
        City[10] = "Pasay";
        Latitude[10] = 14.5377516;
        Longitude[10] = 121.00137940000002;
        City[11] = "Pasig";
        Latitude[11] = 14.5763768;
        Longitude[11] = 121.08510969999998;
        City[12] = "Quezon City";
        Latitude[12] = 14.6760413;
        Longitude[12] = 121.04370029999995;
        City[13] = "San Juan";
        Latitude[13] = 14.5994146;
        Longitude[13] = 121.03688929999998;
        City[14] = "Taguig";
        Latitude[14] = 14.5176184;
        Longitude[14] = 121.05086449999999;
        City[15] = "Valenzuela";
        Latitude[15] = 14.7010556;
        Longitude[15] = 120.98302250000006;
        City[16] = "Taguig";
        Latitude[16] = 14.551014;
        Longitude[16] = 121.051043;
        City[17] = "Makati";
        Latitude[17] = 14.537587;
        Longitude[17] = 121.060711;
        City[18]="Caloocan";
        City[19]="Caloocan";
        City[20]="Caloocan";
        City[21]="Las Pinas";
        City[22]="Las Pinas";
        City[23]="Las Pinas";
        City[24]="Makati";
        City[25]="Makati";
        City[26]="Makati";
        City[27]="Malabon";
        City[28]="Malabon";
        City[29]="Malabon";
        City[30]="Mandaluyong";
        City[31]="Mandaluyong";
        City[32]="Mandaluyong";
        City[33]="Manila";
        City[34]="Manila";
        City[35]="Manila";
        City[36]="Marikina";
        City[37]="Marikina";
        City[38]="Marikina";
        City[39]="Muntinlupa";
        City[40]="Muntinlupa";
        City[41]="Muntinlupa";
        City[42]="Navotas";
        City[43]="Navotas";
        City[44]="Navotas";
        City[45]="Paranaque";
        City[46]="Paranaque";
        City[47]="Paranaque";
        City[48]="Pasay";
        City[49]="Pasay";
        City[50]="Pasay";
        City[51]="Pasig";
        City[52]="Pasig";
        City[53]="Pasig";
        City[54]="Quezon City";
        City[55]="Quezon City";
        City[56]="Quezon City";
        City[57]="Quezon City";
        City[58]="Quezon City";
        City[59]="San Juan";
        City[60]="Taguig";
        City[61]="Taguig";
        City[62]="Taguig";
        City[63]="Valenzuela";
        City[64]="Valenzuela";
        City[65]="Valenzuela";
        City[66]="Taytay";
        City[67]="Cainta";
        City[68]="Antipolo";
        Latitude[18]=14.729387;
        Latitude[19]=14.64888;
        Latitude[20]=14.777207;
        Latitude[21]=14.46183;
        Latitude[22]=14.423101;
        Latitude[23]=14.41032;
        Latitude[24]=14.56373;
        Latitude[25]=14.539983;
        Latitude[26]=14.561361;
        Latitude[27]=14.665572;
        Latitude[28]=14.686733;
        Latitude[29]=14.69328;
        Latitude[30]=14.58825;
        Latitude[31]=14.592605;
        Latitude[32]=14.574935;
        Latitude[33]=14.621579;
        Latitude[34]=14.570048;
        Latitude[35]=14.611027;
        Latitude[36]=14.634638;
        Latitude[37]=14.639655;
        Latitude[38]=14.659799;
        Latitude[39]=14.372834;
        Latitude[40]=14.405959;
        Latitude[41]=14.44407;
        Latitude[42]=14.637601;
        Latitude[43]=14.649551;
        Latitude[44]=14.666561;
        Latitude[45]=14.45182;
        Latitude[46]=14.49154;
        Latitude[47]=14.516453;
        Latitude[48]=14.513304;
        Latitude[49]=14.535195;
        Latitude[50]=14.557894;
        Latitude[51]=14.569621;
        Latitude[52]=14.550676;
        Latitude[53]=14.602512;
        Latitude[54]=14.617457;
        Latitude[55]=14.655131;
        Latitude[56]=14.721135;
        Latitude[57]=14.709163;
        Latitude[58]=14.665311;
        Latitude[59]=14.5994146;
        Latitude[60]=14.540895;
        Latitude[61]=14.529915;
        Latitude[62]=14.476633;
        Latitude[63]=14.69037;
        Latitude[64]=14.735051;
        Latitude[65]=14.714131;
        Latitude[66]=14.557164;
        Latitude[67]=14.586254;
        Latitude[68]=14.625958;

        Longitude[18]=121.010933;
        Longitude[19]=120.970771;
        Longitude[20]=121.040406;
        Longitude[21]=120.984855;
        Longitude[22]=120.995777;
        Longitude[23]=121.006148;
        Longitude[24]=121.011303;
        Longitude[25]=121.024305;
        Longitude[26]=121.046678;
        Longitude[27]=120.978163;
        Longitude[28]=120.952886;
        Longitude[29]=120.934035;
        Longitude[30]=121.030107;
        Longitude[31]=121.054184;
        Longitude[32]=121.045327;
        Longitude[33]=120.966172;
        Longitude[34]=120.99072;
        Longitude[35]=120.995908;
        Longitude[36]=121.083291;
        Longitude[37]=121.10241;
        Longitude[38]=121.114968;
        Longitude[39]=121.03583;
        Longitude[40]=121.021399;
        Longitude[41]=121.05032;
        Longitude[42]=120.963851;
        Longitude[43]=120.950819;
        Longitude[44]=120.942438;
        Longitude[45]=121.024139;
        Longitude[46]=121.036674;
        Longitude[47]=120.989782;
        Longitude[48]=121.016119;
        Longitude[49]=120.98143;
        Longitude[50]=120.994916;
        Longitude[51]=121.065205;
        Longitude[52]=121.095415;
        Longitude[53]=121.095083;
        Longitude[54]=121.058189;
        Longitude[55]=121.008176;
        Longitude[56]=121.054976;
        Longitude[57]=121.09786;
        Longitude[58]=121.080898;
        Longitude[59]=121.03688929999998;
        Longitude[60]=121.050074;
        Longitude[61]=121.080897;
        Longitude[62]=121.054906;
        Longitude[63]=120.973069;
        Longitude[64]=120.995731;
        Longitude[65]=120.95809;
        Longitude[66]=121.134203;
        Longitude[67]=121.114728;
        Longitude[68]=121.123662;
        double result = 99999;
        int point = -1;
        double maxDistance = 0.0412661261703;     //0.06789950746390434; // from San Juan to exit range
        for (int x = 0; x < 69; x++) {
            double result2 = ((Latitude[x] - Lat) * (Latitude[x] - Lat)) + ((Longitude[x] - Lon) * (Longitude[x] - Lon));
            if (abs(result2) < abs(result)) {
                result = result2;
                point = x;
            }
        }
        double result3 = ((14.5994146 - Lat) * (14.5994146 - Lat)) + ((121.03688929999998- Lon) * (121.03688929999998 - Lon)); //from San Juan to current point
        if (abs(result3) > maxDistance) {
            return "OFF";
        }
        return City[point].toLowerCase();
    }


    public void generalAdsFilterIn(String x, String y) {
        //Count number of content
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(getFilesDir() + "/content.sqlite3", null, SQLiteDatabase.OPEN_READONLY);
        Log.d("FILEPATH", String.valueOf(getFilesDir() + "/content.sqlite3"));
        String searchQuery = x;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        int numOfContents = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            numOfContents = cursor.getInt(i);
                        }
                    } catch (Exception e) {
                        Log.d("COUNT_CONTENT_ERROR", e.getMessage());
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("COUNT_CONTENT: ", String.valueOf(numOfContents));
        //Display content in list
        searchQuery = y;
        cursor = myDataBase.rawQuery(searchQuery, null);
        String[] fileName = new String[numOfContents];

        int cnt = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            if (cursor.getColumnName(i).equals("videoName")) {
                                fileName[cnt] = cursor.getString(i);
                                Log.d("LIST_CONTENT(G)", fileName[cnt]);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("LIST_CONTENT_ERROR", e.getMessage());
                    }
                }
            }
            cnt++;
            cursor.moveToNext();
        }
        cursor.close();
        videoPlayingList = new String[cnt];
        videoPlayingList = fileName;

    }

    public void videoPlay() {//general
        // gpsRefreshH.post(trackerR2);


        Random r = new Random();
        ran = r.nextInt(videoPlayingList.length);
        Log.d("LOG_OUTPUT", "MAX>" + String.valueOf(videoPlayingList.length));
        Log.d("LOG_OUTPUT", "RAN>" + String.valueOf(ran));
        Log.d("LOG_OUTPUT", videoPlayingList[ran]);
        url = Uri.parse(getFilesDir().toString() + "/" + videoPlayingList[ran]);
        adid = videoPlayingList[ran];
        StringTokenizer adid_split = new StringTokenizer(adid, ".");
        String first_ad = adid_split.nextToken();
        Log.d("QR_4",first_ad);
        try{
            String json_str = "{\"ad_id\":"+first_ad+",\"part_id\":"+getPartID()+",\"passId\":"+passid+'}';//double check adid
            byte[] data = json_str.getBytes("UTF-8");
            String urlE = Base64.encodeToString(data, Base64.DEFAULT);
            //String urlReward="https://gypsy.ph/gypsy_traffic_controller/qr_scan/"+urlE;
            String urlReward, qrPick2;
            try {

                qrPick2 = qrPick(adid);
                Log.d("QR_1", first_ad);
                Log.d("QR_2", qrPick2);

                if (qrPick2.equals("2")) {
                    //urlReward = "http://qicd3v.gypsy.ph/gypsy_traffic_controller/qr_scan/" + urlE;
                    urlReward="https://gypsy.ph/gypsy_traffic_controller/qr_scan/"+urlE;
                } else {
                    urlReward = "http://www.gypsy.ph/register/advertiser";
                }
            }catch(Exception e){
                Log.d("QR_5", String.valueOf(e));
                urlReward = "http://www.gypsy.ph/register/advertiser";
            }
            qrgEncoder=new QRGEncoder(urlReward,null,QRGContents.Type.TEXT,500);
            QRBitmap=qrgEncoder.encodeAsBitmap();
            qrcodeImage.setImageBitmap(QRBitmap);
        }catch (Exception e){
            Log.d("QR_e",String.valueOf(e));
        }
        genAdsVideo.setVideoURI(url);
        //monday 1 april 2019
        randomPickerGenadsH.post(randomPickerGenadsR);
        //end
        genAdsVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                Log.d("LOG_OUTPUT", "ERROR>" + videoPlayingList[ran]);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                String currentTime = df2.format(Calendar.getInstance().getTime());
                dateEndplayed = df.format(c);
                timeEndplayed = currentTime;
                //datacollected G.error
                rate = String.valueOf(ratingBar1.getProgress());
                dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                dH.dbAdd(
                        adid,
                        passid,
                        dateEndplayed,
                        timeEndplayed,
                        rate,
                        location,
                        latitude,
                        longitude,
                        age,
                        gender,
                        email,
                        "G.error",
                        faceCount,
                        smileRate
                );
                mp.reset();

                videoPlay();
                return false;
            }
        });
        genAdsVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                String currentTime = df2.format(Calendar.getInstance().getTime());
                ratingOut = String.valueOf(ratingBar1.getProgress());
                dateEndplayed = df.format(c);
                timeEndplayed = currentTime;
                rate = ratingOut;
                if (!isFirstAd) {
                    //datacollected G.G
                    dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                    dH.dbAdd(
                            adid,
                            passid,
                            dateEndplayed,
                            timeEndplayed,
                            rate,
                            location,
                            latitude,
                            longitude,
                            age,
                            gender,
                            email,
                            "G.G",
                            faceCount,
                            smileRate
                    );

                } else {
                    //datacollected G.S
                    dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                    dH.dbAdd(
                            adid,
                            passid,
                            dateEndplayed,
                            timeEndplayed,
                            rate,
                            location,
                            latitude,
                            longitude,
                            age,
                            gender,
                            email,
                            "G.S",
                            faceCount,
                            smileRate
                    );
                    isFirstAd = false;
                }
                ratingBar1.setProgress(10);
                mp.reset();
                if(cityX.equals("OFF")){
                    if(LoginActivity.getSizeResolutionBoolean()==1)
                        gypsySmallIcon3.setVisibility(View.INVISIBLE);
                    qrcodeImage.setVisibility(View.INVISIBLE);
                    dataInsertLayout.setVisibility(View.INVISIBLE);
                }else{
                    gypsySmallIcon3.setVisibility(View.VISIBLE);
                    qrcodeImage.setVisibility(View.VISIBLE);

                    offer.setVisibility(View.VISIBLE);
                    dataInsertLayout.setVisibility(View.VISIBLE);
                    if(LoginActivity.getSizeResolutionBoolean()==1)  gypsySmallIcon.setVisibility(View.VISIBLE);
                    ageT1.setVisibility(View.VISIBLE);
                    genderT1.setVisibility(View.VISIBLE);
                    lowAge.setVisibility(View.VISIBLE);
                    ageOut2.setVisibility(View.VISIBLE);
                    highAge.setVisibility(View.VISIBLE);
                    maleB2.setVisibility(View.VISIBLE);
                    femaleB2.setVisibility(View.VISIBLE);
                    emailB3.setVisibility(View.VISIBLE);
                    signUp.setVisibility(View.VISIBLE);
                }
                videoPlay();
            }
        });
        genAdsVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(false);
                genAdsVideo.start();
            }
        });
    }

    public void videoPlayTail() {
        // gpsRefreshH.post(trackerR2);
        Calendar calendar = Calendar.getInstance();
        int dayz = calendar.get(Calendar.DAY_OF_WEEK);
        DateFormat df2 = new SimpleDateFormat("HH:mm");
        String currentTime2 = df2.format(Calendar.getInstance().getTime());
        String[] currentTime2b = currentTime2.split(":");
        int currentTime3 = Integer.valueOf(currentTime2b[0]) * 60 * 60 + Integer.valueOf(currentTime2b[1]) * 60;
        timeOut = String.valueOf(currentTime3);
        switch (dayz) {
            case Calendar.SUNDAY:
                dayOut = "N";
                break;
            case Calendar.MONDAY:
                dayOut = "M";
                break;
            case Calendar.TUESDAY:
                dayOut = "T";
                break;
            case Calendar.WEDNESDAY:
                dayOut = "W";
                break;
            case Calendar.THURSDAY:
                dayOut = "H";
                break;
            case Calendar.FRIDAY:
                dayOut = "F";
                break;
            case Calendar.SATURDAY:
                dayOut = "S";
                break;
        }

        //Random r = new Random();
        //int ran = r.nextInt(5);//SET RANDOM 5
        Log.d("tail_ran",String.valueOf(ran));
        switch (ranZ) {
            case 0: //Time,day,Age,Gender,Location (Target)
                generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') AND dayPlay LIKE \'" + dayOut + "\' AND timeStartPlaySeconds <= " + Integer.valueOf(timeOut) + " AND timeEndPlaySeconds >= " + Integer.valueOf(timeOut) + "", "SELECT videoName FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') AND dayPlay LIKE \'" + dayOut + "\' AND timeStartPlaySeconds <= " + Integer.valueOf(timeOut) + " AND timeEndPlaySeconds >= " + Integer.valueOf(timeOut) + "");
                if (videoPlayingList.length == 0) {
                    ranZ=1;
                    videoPlayTail();
                }
                ranZ=1;
                break;
            case 1://day,Age,Gender,Location (Target)
                generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') AND dayPlay LIKE \'" + dayOut + "\' ", "SELECT videoName FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') AND dayPlay LIKE \'" + dayOut + "\' ");
                if (videoPlayingList.length == 0) {
                    ranZ=2;
                    videoPlayTail();
                }
                ranZ=2;
                break;
            case 2://Age,Gender,Location (Target)
                generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE \'" + cityX + "\' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') ", "SELECT videoName FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE \'" + cityX + "\'AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') ");
                if (videoPlayingList.length == 0) {
                    ranZ=3;
                    videoPlayTail();
                }
                ranZ=3;
                break;
            case 3://Age,Gender,Location (All)
                generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE 'all' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') ", "SELECT videoName FROM tabletG_videocontent WHERE minAge <= " + Integer.valueOf(ageOut) + " AND maxAge >= " + Integer.valueOf(ageOut) + " AND location LIKE 'all' AND (gender LIKE \'" + genderOut + "\' OR gender LIKE 'all') ");
                if (videoPlayingList.length == 0) {
                    ranZ=4;
                    videoPlayTail();
                }
                ranZ=4;
                break;

            case 4://G
                generalAdsFilterIn("SELECT COUNT(*) FROM tabletG_videocontent WHERE contentType=\"G\"", "SELECT videoName FROM tabletG_videocontent WHERE  contentType=\"G\"");
                if (videoPlayingList.length == 0) {
                    ranZ=0;
                    videoPlayTail();
                }
                ranZ=0;
                break;
        }

        Random r2 = new Random();
        int ran2 = r2.nextInt(videoPlayingList.length);
        adsVideoPlay.setVisibility(View.VISIBLE);
        adsImagePlay.setVisibility(View.GONE);
        url = Uri.parse(getFilesDir().toString() + "/" + videoPlayingList[ran2]);
        adid = videoPlayingList[ran2];
        adsVideoPlay.setVideoURI(url);
        adsVideoPlay.start();
        adsVideoPlay.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                String currentTime = df2.format(Calendar.getInstance().getTime());
                dateEndplayed = df.format(c);
                timeEndplayed = currentTime;

                //datacollected T.error
                rate = String.valueOf(ratingBar2.getProgress());
                dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                dH.dbAdd(
                        adid,
                        passid,
                        dateEndplayed,
                        timeEndplayed,
                        rate,
                        location,
                        latitude,
                        longitude,
                        age,
                        gender,
                        email,
                        "T.error",
                        faceCount,
                        smileRate
                );
                mp.reset();
                videoPlayTail();
                return false;
            }
        });
        adsVideoPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                ratingOut = String.valueOf(ratingBar2.getProgress());
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                String currentTime = df2.format(Calendar.getInstance().getTime());
                dateEndplayed = df.format(c);
                timeEndplayed = currentTime;
                rate = ratingOut;
                //datacollected T.T
                ratingBar2.setProgress(10);
                dH = databaseHandler.getInstance(FaceTrackerActivity.this, setIdToDB());
                dH.dbAdd(
                        adid,
                        passid,
                        dateEndplayed,
                        timeEndplayed,
                        rate,
                        location,
                        latitude,
                        longitude,
                        age,
                        gender,
                        email,
                        "T.T",
                        faceCount,
                        smileRate
                );
                mp.reset();
                videoPlayTail();
            }
        });
        adsVideoPlay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(false);
                adsVideoPlay.start();
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Runnable recheckTimeR1 = new Runnable() {
        @Override
        public void run() {
            DateFormat df2 = new SimpleDateFormat("HH:mm");
            String currentTime2 = df2.format(Calendar.getInstance().getTime());
            String[] currentTime2b = currentTime2.split(":");
            int hours = Integer.valueOf(currentTime2b[0]);
            int mins = Integer.valueOf(currentTime2b[1]);
            Log.d("hours", String.valueOf(hours));
            if (hours < 9) {
                if (hours > ServiceClass.getStartTimeHours() + 15) {
                    //dialog box
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(FaceTrackerActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(FaceTrackerActivity.this);
                    }
                    builder.setTitle("Application overtime")
                            .setMessage("You have exceeded 15 hours in using the GYPSY app, please exit the app while network connection is available.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //mPreview.stop();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    //recheckTimeH1.postDelayed(recheckTimeR1,30000);
                }
            } else {
                if (hours > ((ServiceClass.getStartTimeHours() + 15) - 24) && ServiceClass.getMidnightPass()) {
                    //dialog box
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(FaceTrackerActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(FaceTrackerActivity.this);
                    }
                    builder.setTitle("Application overtime")
                            .setMessage("You have exceeded 15 hours in using the GYPSY app, please exit the app while network connection is available.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //mPreview.stop();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    //recheckTimeH1.removeCallbacks(recheckTimeR1);
                } else {
                    //   recheckTimeH1.postDelayed(recheckTimeR1,30000);
                }
            }
        }
    };


    public String setIdToDB() {

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
        } catch (IOException e) {
        }
        String idToDB = text.toString() + "_.db";
        Log.d("RETROFIT", idToDB);
        return idToDB;
    }

    public int lastPassengerId() {
        //Count number of content
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(String.valueOf(getDatabasePath(setIdToDB())), null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT MAX(passid) FROM collected";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        int lastPassId = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            lastPassId = cursor.getInt(i);
                        }
                    } catch (Exception e) {
                        Log.d("COUNT_CONTENT_ERROR", e.getMessage());
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        return lastPassId;
    }
    public void offerUpdate(){
        String offerName="";
        if(LoginActivity.getSizeResolutionBoolean()==1){
            offerName="Get an offer now";

        }
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c);

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(getFilesDir() + "/content.sqlite3", null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT quote FROM tabletG_bannerquotes WHERE date_start2<='"+date+"' and date_end2>='"+date+"'";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            offerName = cursor.getString(i);
                            break;
                        }
                    } catch (Exception e) {
                        Log.d("COUNT_CONTENT_ERROR", e.getMessage());
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        offer.setText(offerName);

    }
    public void setLogoutDatetoFile(Context context, String sBody) {
        try {

            File gpxfile = new File(getFilesDir() + "/", "LOGOUT_DATE");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setLogoutTimetoFile(Context context, String sBody) {
        try {

            File gpxfile = new File(getFilesDir() + "/", "LOGOUT_TIME");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class WebAppInterface {
        Context mContext;
        String data;

        WebAppInterface(Context ctx){
            this.mContext=ctx;
        }
        @JavascriptInterface
        public void sendData(String data) {
            //Get the string value to process
            this.data=data;
            Log.d("WEB_PRESS",String.valueOf(data));
        }
    }
    public void screenDim(){
        Settings.System.putInt(FaceTrackerActivity.this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 20);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =0.2f;// 100 / 100.0f;
        getWindow().setAttributes(lp);
    }
    public void screenBright(){
        Settings.System.putInt(FaceTrackerActivity.this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =255.0f;// 100 / 100.0f;
        getWindow().setAttributes(lp);
    }
    public String getPartID(){
        File file = new File(getFilesDir() + "/", "ID");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
        }
        return text.toString();
    }

    public String qrPick(String name){
        String qrchoice="";

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(getFilesDir() + "/content.sqlite3", null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT adGoals FROM tabletG_videocontent WHERE videoName=\""+name+"\"";
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            qrchoice = cursor.getString(i);
                            break;
                        }
                    } catch (Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("QR_3",qrchoice);
        return qrchoice;

    }
}
