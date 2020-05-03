package com.whirlpoolsoftwares.fueltrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlpoolsoftwares.fueltrack.models.Person;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences sharedPreferencesPayment;

    EditText name, phoneNumber;
    Button login;
    Context context;
    boolean isInternetConnected;
    int MY_PERMISSIONS_REQUEST_INTERNET = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                new CheckConnection().execute();
            }
        }, 500);

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
                    MY_PERMISSIONS_REQUEST_INTERNET);
            Log.e("permission","permission 1 Done");
        }
        //
        sharedPreferences = getSharedPreferences("isLogged",MODE_PRIVATE);
        sharedPreferencesPayment = getSharedPreferences("hasPayed",MODE_PRIVATE);
        sharedPreferencesPayment = getSharedPreferences("pollUrl",MODE_PRIVATE);

        // hiding tools bar
        getSupportActionBar().hide();

        //prevent aoto displaying of the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //checking if logged .if logged then go to dashboard
        if(sharedPreferences.getBoolean("isLogged",false)){

            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            startActivity(intent);
            finish();
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_LOCATION);
//            Log.e("permission","permission 2 Done");
//        }

        context = this;
        name = findViewById(R.id.login_name);
        phoneNumber = findViewById(R.id.login_phone_number);

        login = findViewById(R.id.btLogin);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Intent intent = new Intent(context,Dashboard.class);
//                startActivity(intent);
//                sharedPreferences.edit().putBoolean("isLogged",true).apply();
//                finish();


                if (isInternetConnected) {
                    String name_ = name.getText().toString().trim();
                    String phone_ = phoneNumber.getText().toString().trim();

                    if (!(name_.length() == 0)) {
                        if (phone_.length() == 10) {
                            final ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Please wait ...");
                            progressDialog.setCancelable(true);
                            progressDialog.show();

                            Person person = new Person(name_, phone_);
                            databaseReference.push().setValue(person);

                            Intent intent = new Intent(context, Dashboard.class);
                            startActivity(intent);
                            sharedPreferences.edit().putBoolean("isLogged", true).apply();
                            finish();
                            progressDialog.dismiss();
                        } else {
                            phoneNumber.setError("Enter Phone Number");
                        }
                    } else {

                        name.setError("Enter Valid Name");
                    }
                }else {
                    //show unavailable dialog
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("No Internet Connection")
                            .setMessage("Please check you internet connectivity and try again.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(context,Login.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 500 );
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }

            });


    }


    public class CheckConnection extends AsyncTask<Void, Void, String> {
        URL url;
        HttpURLConnection httpURLConnection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                url = new URL( "http://www.google.com/" );
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty( "User-Agent", "test" );
                httpURLConnection.setRequestProperty( "Connection", "close" );
                httpURLConnection.setConnectTimeout( 3000 );
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == 200) {
                    return "connected";
                } else {
                    return "not connected";
                }
            } catch (IOException e) {
                return "not connected";
            } finally {
                url = null;
                httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute( s );

            if (s.equalsIgnoreCase( "connected" )) {
                isInternetConnected = true;
            } else {
                isInternetConnected = false;
            }
        }



    }


}
