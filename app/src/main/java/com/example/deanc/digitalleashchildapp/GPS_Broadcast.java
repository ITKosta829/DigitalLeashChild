package com.example.deanc.digitalleashchildapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DeanC on 5/3/2016.
 */
public class GPS_Broadcast extends Service {

    String tag = "TestService";

    public CounterClass updater;

    String jsonString, UN, RAD, LAT, LON, childLAT, childLON, currentTime;

    public double latitude;
    public double longitude;

    LocationManager locationManager;
    Location location;

    boolean isConnected;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {

        checkNetwork();
        getLocation();
        updater = new CounterClass(600000, 300000);
    }

    protected void checkNetwork() {

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    protected void getLocation() {

        if (isConnected){

            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 1000, (float) 10, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

            });

            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 1.2 called from Main Activity - startNewService
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.

        // For time consuming an long tasks you can launch a new thread here...

        //Toast.makeText(this, "GPS Updater has started.", Toast.LENGTH_LONG).show();

        updater.start();

        Intent intent1 = new Intent(GPS_Broadcast.this, ReportSuccess.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        /*
        2.2
        onDestroy Called by the system to notify a Service that it is no longer used and is being removed.
        when stopNewService called from Main Activity,  it calls stopService. So no service is running
        at that moment and so onDestroy event is invoked.
        */

        // Service destroyed.
        updater.cancel();

    }

    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            if (latitude != 0.0){
                childLAT = String.valueOf(latitude);
                childLON = String.valueOf(longitude);
            }else{
                childLAT = String.valueOf(location.getLatitude());
                childLON = String.valueOf(location.getLongitude());
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

            currentTime = sdf.format(date);

            try {
                createJSON();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendJsonDataToServer().execute(jsonString);
        }

        @Override
        public void onFinish() {
            updater.start();
        }
    }

    public void createJSON() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("child_latitude", childLAT);
        jsonObject.put("child_longitude", childLON);
        jsonObject.put("child_current_time", currentTime);

        jsonString = jsonObject.toString();

    }

    private class SendJsonDataToServer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();

            String http = "https://turntotech.firebaseio.com/digitalleash/" + MainActivity.UN + ".json";

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PATCH");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonString);
                out.close();

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    System.out.println("" + sb.toString());

                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }

}
