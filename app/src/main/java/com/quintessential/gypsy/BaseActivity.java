package com.quintessential.gypsy;

import android.support.v7.app.AppCompatActivity;

/**
 * A base activity for all activities in the app.  It relays start and stop events to
 * MainApplication, so that it can keep count of the number of started Activities in the app.
 */
class BaseActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        ((MainApplication)getApplication()).onActivityStarted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((MainApplication)getApplication()).onActivityStopped();
    }
}
