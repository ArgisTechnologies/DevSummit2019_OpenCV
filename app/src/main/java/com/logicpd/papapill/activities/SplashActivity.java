package com.logicpd.papapill.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.logicpd.papapill.R;
import com.logicpd.papapill.misc.AppConstants;

/**
 * Activity for displaying the splash screen
 *
 * @author alankilloren
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //proceed to MainActivity after SPLASH_SCREEN_TIMEOUT
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(AppConstants.SPLASH_SCREEN_TIMEOUT);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}