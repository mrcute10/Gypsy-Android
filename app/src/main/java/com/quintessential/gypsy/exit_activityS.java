package com.quintessential.gypsy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.IOException;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class exit_activityS extends AppCompatActivity {
    private static int exitAll = 0;
    private static Context mContext;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable finalR = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    };
    private EditText passwordBe;
    private Button exitBe, returnBe;
    private retrofitInterface retrofitInterface;
    private Handler screenDetect;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable scDet = new Runnable() {
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
            screenDetect.post(scDet);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext2) {
        mContext = mContext2;
    }

    public static int getExitAll() {
        return exitAll;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_exit_s);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                new preventStatusBarExpansion(this);
        }catch (Exception e){}
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://www.gypsy.ph:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface=retrofit.create(retrofitInterface.class);
        wifiSwitch(true);
        data3GSwitch(true);
        passwordBe = findViewById(R.id.passwordBe);
        exitBe = findViewById(R.id.exitBe);
        returnBe = findViewById(R.id.returnBe);
        exitBe.setTextSize(LoginActivity.getFontSize());
        returnBe.setTextSize(LoginActivity.getFontSize());
        passwordBe.setTextSize(LoginActivity.getFontSize());
        exitBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitBe.setEnabled(false);
                exitBe.setBackgroundResource(R.drawable.roundbuttonoff);
                exitBe.setTextColor(Color.rgb(102, 67, 122));
                wifiSwitch(true);
                data3GSwitch(true);
                String pass64="";
                String pass=passwordBe.getText().toString().trim();
                try{
                    byte[] data = pass.getBytes("UTF-8");
                    pass64 = Base64.encodeToString(data, Base64.DEFAULT);
                }catch (Exception e){

                }
                RequestBody userPass6 =
                        RequestBody.create(
                                okhttp3.MultipartBody.FORM, pass64);

                File file = new File(String.valueOf(getDatabasePath(setIdToDB())));
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part tabletDatabaseFullBackup6 = MultipartBody.Part.createFormData("tabletDatabaseFullBackup6", file.getName(), requestFile);


                final File oldFile = new File(String.valueOf(getDatabasePath("gypsyDB.db")));
                if (oldFile.exists()){
                    RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), oldFile);
                    MultipartBody.Part tabletDatabaseProcess6 = MultipartBody.Part.createFormData("tabletDatabaseProcess6", oldFile.getName(), requestFile2);

                    Call<Post> call=retrofitInterface.logoutPostWithGypsyDB(userPass6,tabletDatabaseFullBackup6,tabletDatabaseProcess6);
                    call.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, retrofit2.Response<Post> response) {
                            if(!response.isSuccessful()){
                                Toast.makeText(exit_activityS.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                exitBe.setEnabled(true);
                                exitBe.setTextColor(Color.WHITE);
                                exitBe.setBackgroundResource(R.drawable.roundbutton);
                                //textViewResult.setText("Code: "+response.code());
                                return;
                            }else{
                                oldFile.delete();
                                LogOUT();

                            }
                            Post postResponse=response.body();
                            String content="";
                            content+="Code: "+response.code()+"\n";
                            content+="userId6: "+postResponse.getUserId6()+"\n";
                            content+="userEmail6: "+postResponse.getUserEmail6()+"\n";
                            //textViewResult.setText(content);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(exit_activityS.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            exitBe.setEnabled(true);
                            exitBe.setTextColor(Color.WHITE);
                            exitBe.setBackgroundResource(R.drawable.roundbutton);
                        }
                    });
                }else{
                    Call<Post> call=retrofitInterface.logoutPost(userPass6,tabletDatabaseFullBackup6);
                    call.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, retrofit2.Response<Post> response) {
                            if(!response.isSuccessful()){
                                Toast.makeText(exit_activityS.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                exitBe.setEnabled(true);
                                exitBe.setTextColor(Color.WHITE);
                                exitBe.setBackgroundResource(R.drawable.roundbutton);
                                //textViewResult.setText("Code: "+response.code());
                                return;
                            }else{
                                LogOUT();

                            }
                            Post postResponse=response.body();
                            String content="";
                            content+="Code: "+response.code()+"\n";
                            content+="userId6: "+postResponse.getUserId6()+"\n";
                            content+="userEmail6: "+postResponse.getUserEmail6()+"\n";
                            //textViewResult.setText(content);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(exit_activityS.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            exitBe.setEnabled(true);
                            exitBe.setTextColor(Color.WHITE);
                            exitBe.setBackgroundResource(R.drawable.roundbutton);
                        }
                    });
                }


            }
        });
        returnBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBe.setBackgroundResource(R.drawable.roundbuttonoff);
                returnBe.setTextColor(Color.rgb(102, 67, 122));
                startActivity(new Intent(exit_activityS.this, FaceTrackerActivity.class));
                finish();
            }
        });
        passwordBe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        screenDetect = new Handler();
        screenDetect.post(scDet);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void LogOUT() {
        exitAll=9;
        try{
            File logoutTimeFailFile=new File(getFilesDir() + "/", "LOGOUT_TIME");
            File logoutDateFailFile=new File(getFilesDir() + "/", "LOGOUT_DATE");
            while(logoutDateFailFile.exists()){
                logoutDateFailFile.delete();
            }
            while(logoutTimeFailFile.exists()){
                logoutTimeFailFile.delete();
            }
        }catch(Exception e){
        }


        wifiSwitch(true);
        data3GSwitch(true);

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);        intent.putExtra("EXIT", true);
        startActivity(intent);
       // finishAffinity();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
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
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (exit_activityS.getExitAll() == 1) {
            finish();
        }
        */
        if (LoginActivity.getExitAll() == 2) {
            this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
            finish();
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
    public boolean checkInternet() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            return true;
        }
        return false;
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

}
