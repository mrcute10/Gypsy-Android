package com.quintessential.gypsy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.tcking.giraffecompressor.GiraffeCompressor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class DownloadTask {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int syncCount = 0, syncMax = 0;
    private String url, fileName, fileType, directory;
    private Context syncfromserver;
    private TextView syncStatus;
    private int z = 0;
    private Handler downloadButtonH1 = new Handler();
    private Handler syncDoneH1 = new Handler();
    private ProgressBar loading_indicator2;
    private int fileLength;private long total;
    private int isList=0;
    private syncfromserver ss=new syncfromserver();
    private boolean runOnceFaceTrack=false;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public DownloadTask(String url2, String fileName, String directory, Context syncfromserver, TextView syncStatus, int syncCount, int syncMax , ProgressBar loading_indicator2,int isList) {
        this.url = url2;
        this.fileName = fileName;
        this.directory = directory;
        this.syncfromserver = syncfromserver;
        this.syncStatus = syncStatus;
        this.syncCount = syncCount;
        this.syncMax = syncMax;
        this.loading_indicator2=loading_indicator2;
        this.isList=isList;
        new DownloadingTask().execute();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private class DownloadingTask extends AsyncTask<Void, Integer, Void> {
        private File gypsyDir = new File(directory + "/");
        private File outputFile = null;
        public void delete2directory2(String ad_id2) {
            File outputFile_jpg = new File(gypsyDir, ad_id2 + ".jpg");
            File outputFile_jpeg = new File(gypsyDir, ad_id2 + ".jpeg");
            File outputFile_mp4 = new File(gypsyDir, ad_id2 + ".mp4");
            File outputFile_png = new File(gypsyDir, ad_id2 + ".png");
            File outputFile_sqlite3 = new File(gypsyDir, ad_id2 + ".sqlite3");
            File outputFile_webm = new File(gypsyDir, ad_id2 + ".webm");
            if (outputFile_jpg.exists()) {
                outputFile_jpg.delete();
            }
            if (outputFile_jpeg.exists()) {
                outputFile_jpeg.delete();
            }
            if (outputFile_mp4.exists()) {
                outputFile_mp4.delete();
            }
            if (outputFile_png.exists()) {
                outputFile_png.delete();
            }
            if (outputFile_sqlite3.exists()) {
                outputFile_sqlite3.delete();
            }
            if (outputFile_webm.exists()) {
                outputFile_webm.delete();
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        public Runnable syncDoneR1 = new Runnable() {
            @Override
            public void run() {
                if(!runOnceFaceTrack){
                    runOnceFaceTrack=true;
                    syncStatus.setText("Synchronize Complete");
                    Intent intent = new Intent(syncfromserver, FaceTrackerActivity.class);
                    syncfromserver.startActivity(intent);

                }
                FaceTrackerActivity f=new FaceTrackerActivity();
                if(f.getFaceTrackerActivityDone()){
                    ((Activity) syncfromserver).finish();
                }else{
                    syncDoneH1.post(syncDoneR1);
                }

            }
        };
        ////////////////////////////////////////////////////////////////////////////////////////////////
        public Runnable downloadButtonR1 = new Runnable() {
            @Override
            public void run() {
                if(isList==1){
                    syncStatus.setText("Downloading list ( " + String.valueOf(syncCount) + " out of " + String.valueOf(syncMax) + " )");
                }else{
                    syncStatus.setText("Downloading Contents ( " + String.valueOf(syncCount) + " out of " + String.valueOf(syncMax) + " )");
                }

            }
        };

        ////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try{
                Log.d("POSTEXEC",String.valueOf(total * 100 / fileLength));
                if((total * 100 / fileLength)<100){
                    delete2directory2(fileName);
                    new DownloadingTask().execute();
                }else if((total * 100 / fileLength)==100){
                    try{
                        if(isList==1){
                            ss.setContinueDown(true);
                        }
                    }catch (Exception e){}
                }

            } catch (Exception e) { e.printStackTrace(); }

            try {
                if (outputFile.exists() && syncCount == syncMax && isList==0) {
                    File gypsyDir = new File(directory + "/");
                    String[] nameslist= gypsyDir.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".webm");
                        }
                    });
                    ss.setFileFromFolder(nameslist,syncMax);
                    ss.setContinueDown2(true);
                    downloadButtonH1.removeCallbacks(downloadButtonR1);
                    syncDoneH1.post(syncDoneR1);
                }
            }catch (Exception e){}
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(fileLength>0){
                loading_indicator2.setMax(100);
                loading_indicator2.setProgress((int) (total * 100 / fileLength));            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                outputFile = new File(gypsyDir, fileName);
                if (outputFile.exists()) {
                    return null;
                } else {
                    String url3 = url;
                    URL url4 = new URL(url3);
                    HttpURLConnection c = (HttpURLConnection) url4.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();

                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e("D8", "Server returned HTTP " + c.getResponseCode()
                                + " " + c.getResponseMessage());
                    }
                    //If File is not present create directory
                    if (!gypsyDir.exists()) {
                        gypsyDir.mkdirs();
                    }
                    //Create New File if not present
                    if (!outputFile.exists()) {
                        downloadButtonH1.post(downloadButtonR1);

                        outputFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                        fileLength = c.getContentLength();
                        InputStream is = c.getInputStream();//Get InputStream for connection
                        byte[] buffer = new byte[1024];//Set buffer type
                        int len1 = 0;//init length
                        total=0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);//Write new file
                            total += len1;
                            // publishing the progress....
                            if(fileLength>0){
                                publishProgress((int) (total * 100 / fileLength));
                            }

                        }

                        fos.close();
                        is.close();

                    } else {
                        if (outputFile.exists() && syncCount == syncMax) {
                            syncDoneH1.post(syncDoneR1);
                        } else {
                        }
                    }
                }
            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                try{
                    outputFile.delete();
                }catch(Exception f){

                }

                new DownloadingTask().execute();
            }

            return null;
        }
    }
}
