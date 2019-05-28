package com.quintessential.gypsy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class databaseHandler extends SQLiteOpenHelper {
////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int DATABASE_VERSION = 2;

    //Collected
    private static final String collected_TABLE				="collected";
    private static final String collected_id				="id";
    private static final String collected_adid				="adid";
    private static final String collected_passid			="passid";
    private static final String collected_dateEndplayed		="dateEndplayed";
    private static final String collected_timeEndplayed		="timeEndplayed";
    private static final String collected_rate				="rate";
    private static final String collected_location			="location";
    private static final String collected_latitude			="latitude";
    private static final String collected_longitude			="longitude";
    private static final String collected_age				="age";
    private static final String collected_gender			="gender";
    private static final String collected_email             ="email";
    private static final String collected_contentType		="contentType";//G.S,G.G,G.D,G.E,T.T,T.E,G.error,T.error
    private static final String collected_faceCount			="faceCount";
    private static final String collected_smileRate         ="smileRate";
    //count
    private static final String count_TABLE				="count";
    private static final String count_id				="id";
    private static final String count_cn				="cn";
    //dataPass
    private static final String dataPass_TABLE			="dataPass";
    private static final String dataPass_id				="id";
    private static final String dataPass_cn				="cn";
    //version
    private static final String version_TABLE           ="versions";
    private static final String version_id              ="id";
    private static final String version_name            ="name";
    private static final String version_date            ="date";
    private static final String version_time            ="time";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static databaseHandler sInstance;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private databaseHandler(Context context,String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static synchronized databaseHandler getInstance(Context context,String dbName) {
        if (sInstance == null) {
            sInstance = new databaseHandler(context.getApplicationContext(),dbName);
        }
        return sInstance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + collected_TABLE +
                " ( "
                + collected_id + " INTEGER PRIMARY KEY, "
                + collected_adid + " INTEGER, "
                + collected_passid + " INTEGER, "
                + collected_dateEndplayed + " TEXT, "
                + collected_timeEndplayed + " TEXT, "
                + collected_rate + " INTEGER, "
                + collected_location + " TEXT, "
                + collected_latitude + " REAL, "
                + collected_longitude + " REAL, "
                + collected_age + " INTEGER, "
                + collected_gender + " TEXT, "
                + collected_email + " TEXT, "
                + collected_contentType + " TEXT, "
                + collected_faceCount + " INTEGER, "
                + collected_smileRate + " REAL "
                +
                " ) ");
        db.execSQL("CREATE TABLE " + count_TABLE +
                " ( "
                + count_id + " INTEGER PRIMARY KEY, "
                + count_cn + " INTEGER "
                +
                " ) ");
        db.execSQL("CREATE TABLE " + dataPass_TABLE +
                " ( "
                + dataPass_id + " INTEGER PRIMARY KEY, "
                + dataPass_cn + " INTEGER "
                +
                " ) ");
        db.execSQL("CREATE TABLE "+ version_TABLE +
                " ( "
                + version_id + " INTEGER PRIMARY KEY, "
                + version_name + " TEXT, "
                + version_date + " TEXT, "
                + version_time + " TEXT"
                +
                " ) ");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     switch(newVersion){
         case 1:
             break;
         case 2:
             db.execSQL("CREATE TABLE "+ version_TABLE +
                     " ( "
                     + version_id + " INTEGER PRIMARY KEY, "
                     + version_name + " TEXT, "
                     + version_date + " TEXT, "
                     + version_time + " TEXT"
                     +
                     " ) ");
            break;
     }
        //onCreate(db);
    }

    public void cnAdd(String cn) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(count_cn, cn);
            db.insertOrThrow(count_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception er) {
        } finally {
            db.endTransaction();
        }
    }
    public void dataPassFailAdd(String cn) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(dataPass_cn, cn);
            db.insertOrThrow(dataPass_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception er) {
        } finally {
            db.endTransaction();
        }
    }

public void dbAdd(String adid, String passid, String dateEndplayed, String timeEndplayed, String rate, String location, String latitude, String longitude, String age, String gender,String email, String contentType, String faceCount,String smileRate) {
    SQLiteDatabase db = getWritableDatabase();
    db.beginTransaction();
    try {
        ContentValues values = new ContentValues();
        values.put(collected_adid, adid);
        values.put(collected_passid, passid);
        values.put(collected_dateEndplayed, dateEndplayed);
        values.put(collected_timeEndplayed, timeEndplayed);
        values.put(collected_rate, rate);
        values.put(collected_location, location);
        values.put(collected_latitude, latitude);
        values.put(collected_longitude, longitude);
        values.put(collected_age, age);
        values.put(collected_gender, gender);
        values.put(collected_email, email);
        values.put(collected_contentType, contentType);
        values.put(collected_faceCount, faceCount);
        values.put(collected_smileRate, smileRate);
        db.insertOrThrow(collected_TABLE, null, values);
        db.setTransactionSuccessful();
    } catch (Exception er) {
    } finally {
        db.endTransaction();
    }
}
public void versionAdd(String name, String date, String time){
    SQLiteDatabase db = getWritableDatabase();
    db.beginTransaction();
    try{
        ContentValues values= new ContentValues();
        values.put(version_name,name);
        values.put(version_date,date);
        values.put(version_time,time);
        db.insertOrThrow(version_TABLE,null,values);
        db.setTransactionSuccessful();
    } catch (Exception er) {
    } finally {
        db.endTransaction();
    }
}
}
