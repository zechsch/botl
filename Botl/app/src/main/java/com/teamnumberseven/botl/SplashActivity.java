package com.teamnumberseven.botl;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * Created by Kevin on 3/30/2017.
 */

public class SplashActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int SPLASH_DISPLAY_LENGTH = 2000;
    public boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        else {
            return true;
        }
    }

   // @Override
   // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
   //     switch (requestCode) {
   //             case MY_PERMISSIONS_REQUEST_LOCATION:
   //             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
     //               Log.d("FXN", "GOOOOOOD");
     //           } else {
    //                Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
  //              }
//
    //            break;
    //    }
    //}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Log.d("FXN", "LOC WAS DISABLED, NOW ENABLED");
            //while( !checkLocationPermission())
            //{
            //    Log.d("FXN", "CHECKING PERMISSION CURRENTLY FALSE");
            //};
            checkLocationPermission();
        }

        final Intent intent = new Intent(this, MainActivity.class);

        Log.d("FXN", "1");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}
