package com.google.gps.ui.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.gps.R;
import com.google.gps.model.events.Events;
import com.google.gps.model.events.EventlBus;
import com.google.gps.ui.services.GpsTrackerService;
import org.greenrobot.eventbus.Subscribe;



public class MainActivity extends AppCompatActivity {



    private TextView location;
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION = 10;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventlBus.getgBus().register(this);

        location=findViewById(R.id.txt_location);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.INTERNET ,Manifest.permission.ACCESS_COARSE_LOCATION
                }, LOCATION_PERMISSION);

            } else {
                startLocationUpdateService();
            }

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdateService();

                }
             break;
        } }




public void startLocationUpdateService(){
        ActivityCompat.startForegroundService(this , new Intent(this , GpsTrackerService.class));
}


    @Subscribe
    public void getLocation(Events.ServiceMainActivityLocation  serviceMainActivityLocation){

        location.setText(serviceMainActivityLocation.getLocation());
    }

    private void stopService(){
       Intent intent=new Intent(this , GpsTrackerService.class);
       intent.putExtra("" , "");
       stopService(intent);
   }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventlBus.getgBus().unregister(this);
    }
}
