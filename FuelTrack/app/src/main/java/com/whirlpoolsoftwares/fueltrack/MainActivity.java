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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;
    ImageView imageView;
    LinearLayout linearLayout;
    int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        timer = new Timer(  );
        imageView = findViewById(R.id.splashIcon);
        linearLayout = findViewById(R.id.poweredBy);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        linearLayout.startAnimation(animation);
        imageView.startAnimation(animation);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else{
            timer.schedule( new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent( getApplicationContext(),Login.class );
                    startActivity( intent );
                    finish();
                }
            },4000 );
        }
    }
}
