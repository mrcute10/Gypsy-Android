package com.quintessential.gypsy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static String usernameOut, partidOut;
    private static float sizeResolutionHeight,sizeResolutionWidth,fontSize;
    private static int sizeResolutionBoolean;
    private static int exitAll = 0;
    private EditText usernameB, passwordB;
    private Button loginB, cancelB, updateBe;
    private CheckBox rememberMe;
    private int Permission_All = 1;
    private Handler updateResetH1, updateButtonH1,checkNet=new Handler();
    private retrofitInterface retrofitinterface;
    private TextView batteryTxt;
    private boolean checkUpPress=false;
    private BroadcastReceiver mBatInfoReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            batteryTxt.setText(String.valueOf(level)+"%");
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable updateR1 = new Runnable() {//Runnable update
        @Override
        public void run() {
            if (updateApp.getUpdateGo() == 1) {
                updateBe.setText("Check for updates");
                updateBe.setClickable(true);
                updateButtonH1.removeCallbacks(updateR1);
                updateNow();
            } else if (updateApp.getUpdateGo() == 2) {
                Toast.makeText(LoginActivity.this, "No Updates", Toast.LENGTH_SHORT).show();
                updateBe.setText("Check for updates");
                updateBe.setClickable(true);
                updateBe.setTextColor(Color.WHITE);
                updateBe.setBackgroundResource(R.drawable.roundbutton);
                updateButtonH1.removeCallbacks(updateR1);
            } else if (updateApp.getUpdateGo() == 0) {
                updateBe.setText("Downloading: " + String.valueOf(updateApp.getApkDownloaded()) + "%");
                updateBe.setClickable(false);
                updateButtonH1.post(updateR1);
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable updateResetR1 = new Runnable() {
        @Override
        public void run() {//Runnable button on press
            updateBe.setTextColor(Color.WHITE);
            updateBe.setBackgroundResource(R.drawable.roundbutton);
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable checkNetR=new Runnable() {
        @Override
        public void run() {//Runnable check updates
            if(checkInternet()){
                Toast.makeText(LoginActivity.this, "Internet connection established", Toast.LENGTH_LONG).show();
                checkForUpdates();
                checkNet.removeCallbacks(checkNetR);
            }else{
                checkNet.post(checkNetR);
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String[] Permissions = {//Permission Request
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Getters

    public static float getFontSize() {
        return fontSize;
    }
    public static String getUsername() {
        return usernameOut;
    }
    public static String getPartId() {
        return partidOut;
    }
    public static int getExitAll() {
        return exitAll;
    }
    public static float getSizeResolutionHeight() {return sizeResolutionHeight; }
    public static float getSizeResolutionWidth() { return sizeResolutionWidth; }
    public static int getSizeResolutionBoolean() { return sizeResolutionBoolean; }
////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean hasPermissions(Context context, String... permissions) {//Permission request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {//
        super.onCreate(savedInstanceState);
        //resetPreferredLauncherAndOpenChooser(LoginActivity.this);

        //hide
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
        setContentView(R.layout.activity_login);
        usernameB = findViewById(R.id.usernameB);
        passwordB = findViewById(R.id.passwordB);
        loginB = findViewById(R.id.loginB);
        cancelB = findViewById(R.id.cancelB);
        updateBe = findViewById(R.id.updateBe);
        rememberMe=findViewById(R.id.rememberMe);
        batteryTxt=findViewById(R.id.batteryTxt);
        batteryTxt.setVisibility(View.INVISIBLE);
        this.registerReceiver(this.mBatInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameB.getText().toString().trim();
                String password = passwordB.getText().toString().trim();
                if (!username.isEmpty() || !password.isEmpty()) {
                    loginB.setBackgroundResource(R.drawable.roundbuttonoff);
                    loginB.setTextColor(Color.rgb(102, 67, 122));
                    Login(username, password);
                    loginB.setEnabled(false);
                } else {
                    usernameB.setError("Insert Username");
                    passwordB.setError("Insert Password");
                }
            }
        });
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitme();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);

            }
        });
        usernameB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        passwordB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        updateBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpPress=true;
                checkForUpdates();
            }
        });
        //exit
        if (getIntent().getBooleanExtra("EXIT", false)) {
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }
        //exit--

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        try{
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                new preventStatusBarExpansion(this);
        }catch(Exception e){}


        //Check Internet
        wifiSwitch(true);
        data3GSwitch(true);

        //Check if phone or tablet--start
        DisplayMetrics metrics = new DisplayMetrics();
        LoginActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        Log.d("SIZE2ba",String.valueOf(metrics.heightPixels ));
        Log.d("SIZE2bb",String.valueOf(metrics.widthPixels ));

        if (metrics.heightPixels >= 1900 || metrics.widthPixels >= 1900) {
            usernameB.setTextSize(loginB.getTextSize() + 10);
            passwordB.setTextSize(loginB.getTextSize() + 10);
            fontSize = loginB.getTextSize() + 10;
            sizeResolutionBoolean=1;
            Log.d("SIZE2bc",String.valueOf(sizeResolutionBoolean ));
        } else {
            usernameB.setTextSize(13);
            passwordB.setTextSize(13);
            fontSize = 13;
            sizeResolutionBoolean=0;
            Log.d("SIZE2bc",String.valueOf(sizeResolutionBoolean ));
        }
        /*
        if(LoginActivity.getSizeResolution()==1){
            //change to another directory resoultion
            String test="https://gypsy.ph/appphp/tablet/dev/bin/admin/video_content/100.webm";
            String[] bits = test.split("\");
            Log.d("STRINGTEST", String.valueOf(bits[0]));
        }
*/

        File outputExist = new File(Environment.getExternalStorageDirectory(), "QIC.apk");
        if (outputExist.exists()) {
            outputExist.delete();
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        if (!hasPermissions(this, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }
        try{
            File fileEM = new File(getFilesDir() + "/", "EM");
            File filePW = new File(getFilesDir() + "/", "PW");
            if(fileEM.exists() && filePW.exists()){
                usernameB.setText(getSavedEmail());
                passwordB.setText(getSavedPass());
                rememberMe.setChecked(true);
            }
        }catch (Exception e){}
        //Retrofit Start
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://www.gypsy.ph:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface=retrofit.create(retrofitInterface.class);
        //Retrofit End

        //animate
        setupWindowAnimations();

        updateButtonH1 = new Handler();
        updateResetH1 = new Handler();
        checkNet.post(checkNetR);
        try{
            screenBright();
        }catch (Exception e){}




    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateNow() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "QIC.apk")), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //exitAll = 2;
        startActivity(i);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (FaceTrackerActivity.getResultOpen() == 1) {
            startActivity(new Intent(LoginActivity.this, FaceTrackerActivity.class));
        }*/
        if (exit_activityS.getExitAll() >= 1) {
            this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
            finish();
        }
        if (LoginActivity.getExitAll() == 2) {
            this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
            finish();
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void trustEveryone() {

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void Login(final String uname, final String passw) {
        trustEveryone();
        String pass64="";
        try{
            byte[] data = passw.getBytes("UTF-8");
            pass64 = Base64.encodeToString(data, Base64.DEFAULT);
        }catch (Exception e){

        }

        String lDFF=getDateLogout();
        if (lDFF.equals("")){
            Post post=new Post(uname,pass64);
            Call<Post> call =retrofitinterface.loginPost(post);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, retrofit2.Response<Post> response) {
                    if (!response.isSuccessful()){//Fail
                        Log.d("RETROFIT",String.valueOf(response.code()));
                        Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                        loginB.setEnabled(true);
                        loginB.setTextColor(Color.WHITE);
                        loginB.setBackgroundResource(R.drawable.roundbutton);
                        //return;
                    }else{//Pass
                        Post postResponse=response.body();
                        Log.d("RETROFIT",String.valueOf(response.code()));
                        Log.d("RETROFIT",String.valueOf(postResponse.getUserId6()));
                        try{
                            if(rememberMe.isChecked()){
                                setEmailToFile(LoginActivity.this,uname);
                                setPasswordToFile(LoginActivity.this,passw);
                            }else{
                                File fileEM = new File(getFilesDir() + "/", "EM");
                                File filePW = new File(getFilesDir() + "/", "PW");
                                fileEM.delete();
                                filePW.delete();
                            }
                        }catch (Exception e){}
                        setIdtoFile(LoginActivity.this,String.valueOf(postResponse.getUserId6()));
                        setCounttoFile(LoginActivity.this,String.valueOf(postResponse.getUsercount6Login()));
                        setIdToDB();

                        startActivity(new Intent(LoginActivity.this, syncfromserver.class));

                        loginB.setEnabled(true);
                        loginB.setTextColor(Color.WHITE);
                        loginB.setBackgroundResource(R.drawable.roundbutton);
                    }
                }


                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Connection Fail", Toast.LENGTH_SHORT).show();
                    loginB.setEnabled(true);
                    loginB.setTextColor(Color.WHITE);
                    loginB.setBackgroundResource(R.drawable.roundbutton);
                }
            });
        }else{//logoutFaile
            RequestBody userEmail6 = RequestBody.create(okhttp3.MultipartBody.FORM, uname);
            RequestBody userPass6 = RequestBody.create(okhttp3.MultipartBody.FORM, pass64);
            RequestBody logoutDate6 = RequestBody.create(okhttp3.MultipartBody.FORM, getLogoutDate());
            RequestBody logoutTime6 = RequestBody.create(okhttp3.MultipartBody.FORM, getLogoutTime());
            File file = new File(String.valueOf(getDatabasePath(setIdToDB())));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part tabletDatabaseFullBackup6 = MultipartBody.Part.createFormData("tabletDatabaseFullBackup6", file.getName(), requestFile);

            Call<Post> call =retrofitinterface.loginPostWithFailLogout(userEmail6,userPass6,logoutDate6,logoutTime6,tabletDatabaseFullBackup6);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, retrofit2.Response<Post> response) {
                    if (!response.isSuccessful()){//Fail
                        Log.d("RETROFIT",String.valueOf(response.code()));
                        Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                        loginB.setEnabled(true);
                        loginB.setTextColor(Color.WHITE);
                        loginB.setBackgroundResource(R.drawable.roundbutton);
                        //return;
                    }else{//Pass
                        Post postResponse=response.body();
                        Log.d("RETROFIT",String.valueOf(response.code()));
                        Log.d("RETROFIT",String.valueOf(postResponse.getUserId6()));
                        try{
                            if(rememberMe.isChecked()){
                                setEmailToFile(LoginActivity.this,uname);
                                setPasswordToFile(LoginActivity.this,passw);
                            }else{
                                File fileEM = new File(getFilesDir() + "/", "EM");
                                File filePW = new File(getFilesDir() + "/", "PW");
                                fileEM.delete();
                                filePW.delete();
                            }
                        }catch (Exception e){}
                        setIdtoFile(LoginActivity.this,String.valueOf(postResponse.getUserId6()));
                        setCounttoFile(LoginActivity.this,String.valueOf(postResponse.getUsercount6Login()));
                        setIdToDB();

                        startActivity(new Intent(LoginActivity.this, syncfromserver.class));

                        loginB.setEnabled(true);
                        loginB.setTextColor(Color.WHITE);
                        loginB.setBackgroundResource(R.drawable.roundbutton);
                    }
                }


                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Connection Fail", Toast.LENGTH_SHORT).show();
                    loginB.setEnabled(true);
                    loginB.setTextColor(Color.WHITE);
                    loginB.setBackgroundResource(R.drawable.roundbutton);
                }
            });
        }
    }

    private void setEmailToFile(Context context, String sBody) {
        try {
            File gpxfile = new File(getFilesDir() + "/", "EM");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setPasswordToFile(Context context, String sBody) {
        try {
            File gpxfile = new File(getFilesDir() + "/", "PW");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIdtoFile(Context context, String sBody) {
        try {
            /*
            File root = new File(getFilesDir() + "/", "ID");
            if (!root.exists()) {
                root.mkdirs();
            }*/
            File gpxfile = new File(getFilesDir() + "/", "ID");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setCounttoFile(Context context, String sBody) {
        try {
            /*
            File root = new File(getFilesDir() + "/", "ID");
            if (!root.exists()) {
                root.mkdirs();
            }*/
            File gpxfile = new File(getFilesDir() + "/", "COUNT");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
    public void exitme() {
        this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void wifiSwitch(boolean x) {
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
            wifiManager.setWifiEnabled(x);
        } catch (Exception e) {

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, com.quintessential.gypsy.LoginActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getLogoutDate(){

        File file = new File(getFilesDir() + "/", "LOGOUT_DATE");
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getLogoutTime(){

        File file = new File(getFilesDir() + "/", "LOGOUT_TIME");
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
    public String getDateLogout(){

        File file = new File(getFilesDir() + "/", "LOGOUT_DATE");
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
    public String getSavedEmail(){
        File file = new File(getFilesDir() + "/", "EM");
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
    public String getSavedPass(){
        File file = new File(getFilesDir() + "/", "PW");
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
    public void screenBright(){
        Settings.System.putInt(LoginActivity.this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =255.0f;// 100 / 100.0f;
        getWindow().setAttributes(lp);
    }
    public void checkForUpdates(){
        if(checkUpPress){
            Toast.makeText(LoginActivity.this, "Checking for updates...", Toast.LENGTH_SHORT).show();
            updateBe.setBackgroundResource(R.drawable.roundbuttonoff);
            updateBe.setTextColor(Color.rgb(102, 67, 122));
            checkUpPress=false;
        }

        new AppUpdater(LoginActivity.this)
                .setUpdateFrom(UpdateFrom.XML)
                .setDisplay(Display.DIALOG)
                .setUpdateXML("https://gypsy.ph/appdate/releases.xml")
                .setTitleOnUpdateAvailable("New update available")
                .setContentOnUpdateAvailable("Please, update GYPSY to a new version")
                .setButtonDismiss("Not now")

                .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new updateApp(LoginActivity.this);
                        updateButtonH1.post(updateR1);
                    }
                })
                .setButtonDismissClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateBe.setTextColor(Color.WHITE);
                        updateBe.setBackgroundResource(R.drawable.roundbutton);
                    }
                })
                .setButtonDoNotShowAgain(null)
                .setButtonUpdate("Update")
                .showAppUpdated(false)
                .setCancelable(false)
                .start();
        updateResetH1.postDelayed(updateResetR1, 1000);
    }
    public boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    private void setupWindowAnimations() {
        Fade fade = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
        }

    }
}


