package com.quintessential.gypsy;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class start extends AppCompatActivity {
private Handler handler=new Handler();
private Runnable runnable=new Runnable() {
    @Override
    public void run() {
        startActivity(new Intent(start.this,LoginActivity.class));
    }
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        handler.postDelayed(runnable,3000);
    }
}
