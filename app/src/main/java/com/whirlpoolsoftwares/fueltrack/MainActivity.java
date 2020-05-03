package com.whirlpoolsoftwares.fueltrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;
    ImageView imageView;
    ImageView poweredBy;
    int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
            Log.e("permission","permission 1 Done");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            Log.e("permission","permission 1 Done");
        }

        getSupportActionBar().hide();

        timer = new Timer(  );
        imageView = findViewById(R.id.splashIcon);
        poweredBy = findViewById(R.id.splashImageWhirlpool);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        poweredBy.startAnimation(animation);
        imageView.startAnimation(animation);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            Log.e("permission","permission 1 Done");
        }


        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent( getApplicationContext(),Login.class );
                startActivity( intent );
                finish();
            }
        },6000 );



    }
}
