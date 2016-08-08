package com.example.deanc.digitalleashchildapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by DeanC on 5/3/2016.
 */
public class ReportSuccess extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_success);

        setFonts();


    }

    public void done(View view) {

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }

    public void setFonts() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "ANDYB.TTF");

        TextView tv1 = (TextView) findViewById(R.id.success_notification);
        tv1.setTypeface(tf);
        Button b1 = (Button) findViewById(R.id.done);
        b1.setTypeface(tf);

    }


}
