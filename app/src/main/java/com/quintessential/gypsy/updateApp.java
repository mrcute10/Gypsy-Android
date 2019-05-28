package com.quintessential.gypsy;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class updateApp {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static int apkDownloaded = 0;
    private static int updateGo;
    private Context context;
    private long total = 0;
    private int count, fileLength;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public updateApp(Context con) {
        this.context = con;
        updateGo = 0;
        new updatingApp().execute();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int getApkDownloaded() {
        return apkDownloaded;
    }
    public static int getUpdateGo() {
        return updateGo;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private class updatingApp extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            File outputExist = null;
            try {
                Log.d("U1", "Run update");
                outputExist = new File(Environment.getExternalStorageDirectory(), "QIC.apk");
                if (outputExist.exists()) {
                    outputExist.delete();
                }
                URL url = new URL("https://gypsy.ph/appdate/QIC.apk");
                URLConnection connection = url.openConnection();
                connection.connect();
                fileLength = connection.getContentLength();
                Log.d("U4", String.valueOf(fileLength));

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/QIC.apk");
                byte data[] = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    total += count;
                    apkDownloaded = (int) (total * 100 / fileLength);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                updateGo = 1;
            } catch (Exception e) {
                Log.d("UPDATE_error",String.valueOf(e));

                updateGo = 2;
            }
            return null;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
