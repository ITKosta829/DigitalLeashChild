package com.example.deanc.digitalleashchildapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText userName = null;

    public static String UN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFonts();

        userName = (EditText) findViewById(R.id.get_username);


    }

    public void reportLocation(View view) {

        UN = userName.getText().toString();

        startService(new Intent(this, GPS_Broadcast.class));

    }

    public void setFonts() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "ANDYB.TTF");

        TextView tv1 = (TextView) findViewById(R.id.header);
        tv1.setTypeface(tf);
        TextView tv2 = (TextView) findViewById(R.id.username);
        tv2.setTypeface(tf);
        EditText e1 = (EditText)findViewById(R.id.get_username);
        e1.setTypeface(tf);
        Button b1 = (Button) findViewById(R.id.report);
        b1.setTypeface(tf);

    }
}
