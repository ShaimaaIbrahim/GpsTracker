package com.google.gps.ui.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.google.gps.R;
import com.google.gps.model.events.Events;
import com.google.gps.model.events.EventlBus;
import com.google.gps.ui.activities.main.MainActivity;
import static org.greenrobot.eventbus.EventBus.TAG;



public class GpsTrackerService extends Service implements LocationListener {

    private static final String CHANNEL_ID = "foregroundChannel";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;


    private boolean isGpsEnabled = false;
    private boolean isNetWorkEnabled = false;
    private boolean canGetLocation = false;
    private static Location location;
    private  static double longitude;
    private  static double latitude;
    private LocationManager locationManager;
    private Context mContect;

    public GpsTrackerService() {

    }



    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("service", "service still running");
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(runnable, 1000);

        sendLocationFromServiceToMainActivity();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CreateNotificationChannel();
        initNotification(intent);
        return START_STICKY;

    }

    private void initNotification(Intent intent) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler = null;
    }

    private void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "FOREGROUND SERVICE", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * Called when the location has changed.
     *
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }


    public  void getGeoLocation() {
        try {

            locationManager = (LocationManager) mContect.getSystemService(LOCATION_SERVICE);

            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetWorkEnabled && !isGpsEnabled) {
                Toast.makeText(this, "Not Enable", Toast.LENGTH_LONG).show();
            } else {

                this.canGetLocation = true;
                if (isNetWorkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGpsEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager!=null){
                    location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location!=null){
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                    }
                }}
        }

    }catch (Exception e){
     e.getStackTrace();
 }
}


    public  void sendLocationFromServiceToMainActivity(){

        Events.ServiceMainActivityLocation serviceMainActivityLocation
                =new Events.ServiceMainActivityLocation(getFinalLocation());

        EventlBus.getgBus().post(serviceMainActivityLocation);
    }


    public String getFinalLocation(){

        getGeoLocation();

        Log.e(TAG ,latitude + " , " + longitude  );

        if (location !=null){
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }
        return  latitude+ " , " + longitude ;
    }




}
