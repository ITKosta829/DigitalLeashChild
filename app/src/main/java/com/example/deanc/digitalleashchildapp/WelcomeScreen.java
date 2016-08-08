package com.example.deanc.digitalleashchildapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by DeanC on 5/2/2016.
 */
public class WelcomeScreen extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        setFonts();

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {

            public void run() {
                startActivity(new Intent(WelcomeScreen.this, MainActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }

    public void setFonts() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "ANDYB.TTF");

        TextView tv1 = (TextView)findViewById(R.id.welcome_screen__title);
        tv1.setTypeface(tf);
        tv1.setTextSize(40);

        TextView tv2 = (TextView)findViewById(R.id.welcome_screen_tv);
        tv2.setTypeface(tf);
        tv2.setTextSize(30);
    }
}
