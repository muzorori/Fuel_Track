package com.whirlpoolsoftwares.fueltrack.ui.payments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whirlpoolsoftwares.fueltrack.Dashboard;
import com.whirlpoolsoftwares.fueltrack.Login;
import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.ui.home.HomeFragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import zw.co.paynow.constants.MobileMoneyMethod;
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;

import static com.whirlpoolsoftwares.fueltrack.Login.sharedPreferencesPayment;

public class Payments extends Fragment {

    String myPollURL;
    Payment payment;
    MobileInitResponse response;
    String pollUrl;
    Timer timer;
    FirebaseDatabase dbSearchFee;
    DatabaseReference myRef;
    TextView searchFee;
    ProgressDialog progressDialog;
    Paynow paynow = new Paynow("6668", "b0b170e0-c950-4800-b56c-9ce4e4e02e14");
    TextInputEditText phoneNumber;
    Boolean hasInternetConnection;
    double price;


    public static Payments newInstance() {
        return new Payments();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.payments_fragment, container, false);

        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Payment </font>"));



        phoneNumber = root.findViewById(R.id.phone_no);
        final Spinner spinner = root.findViewById(R.id.payments_options);
        MaterialButton pay = root.findViewById(R.id.make_payment);
        MaterialButton checkPayment = root.findViewById(R.id.check_payment);
        searchFee = root.findViewById(R.id.amount);

        dbSearchFee = FirebaseDatabase.getInstance();
        myRef = dbSearchFee.getReference("searchingFee");
        //uncomment this
        //addPrice();
        getSearchingFee();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CheckConnection().execute();
            }
        }, 500);


        timer = new Timer(  );

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        final StringBuilder builder = new StringBuilder();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                builder.setLength(0);
                String methodOfPayment = spinner.getSelectedItem().toString();
                builder.append(methodOfPayment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = phoneNumber.getText().toString().trim();

                if(phone_number.length()==10 ){

                    if(hasInternetConnection){

                        makePayment(phoneNumber.getText().toString(),MobileMoneyMethod.ECOCASH);
                        checkPayment.setVisibility(View.VISIBLE);
                        phoneNumber.setText("");


                    }else {
                        //show unavailable dialog
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle("No Internet Connection")
                            .setMessage("Please check you internet connectivity and try again.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            new CheckConnection().execute();
                                        }
                                    }, 2 * 500 );
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

                }else{
                    phoneNumber.setError("Enter Ecocash Number");

                }


            }
        });

        checkPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(myPollURL != null){
                            paynow = new Paynow("6668","b0b170e0-c950-4800-b56c-9ce4e4e02e14");
                            //StatusResponse response = paynow.pollTransaction(myPollURL);

                            //paid status response for testing
//                            StatusResponse response = paynow.pollTransaction("https://www.paynow.co.zw/Interface/CheckPayment/?guid=d8401614-c7e2-42cc-860e-6b1c35e34d56");
//                            Log.e("Recheck","clicked Done");

                            // Check the status of the transaction with the specified pollUrl
                            String string = sharedPreferencesPayment.getString("pollUrl",myPollURL);
                            StatusResponse response = paynow.pollTransaction(string);

                            if(response.isPaid()){

                                //making shared pref true to display available stations
                                sharedPreferencesPayment.edit().putBoolean("hasPayed",true).apply();

                                //clear pollUrl
                                sharedPreferencesPayment.edit().putString("pollUrl","");
                                //open paid fragment
                                Intent intent = new Intent(getContext(), Dashboard.class);
                                startActivity(intent);
                                getActivity().finish();

                                Log.e("Payment","Payment Done");
                            }
                            else{
                                //notify to check after a few minutes
                                Log.e("Payment","Payment Not Done");
                            }
                        }
                    }
                });

                thread.start();

            }
        });

        return root;
    }

    private void makePayment(String number, MobileMoneyMethod method){
        progressDialog = new ProgressDialog( getContext() );
        progressDialog.setMessage( "Please wait ..." );
        progressDialog.setCancelable( false );
        progressDialog.show();
        new MakePayment().execute();
        sharedPreferencesPayment.edit().putLong("date", Calendar.getInstance().getTimeInMillis()).apply();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(PaymentsViewModel.class);
        // TODO: Use the ViewModel
    }



    // Background class that initiate payment
    public class MakePayment extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            Log.e("pre exe","pre exe");
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            //theo
            paynow = new Paynow("6668","b0b170e0-c950-4800-b56c-9ce4e4e02e14");

            payment = paynow.createPayment("fuel payment", "lovemoremajaka@gmail.com");

            // Passing in the name of the item and the price of the item
            payment.add("Search fee", price);


            try {
                Log.e("try ","try cat");
                response = paynow.sendMobile(payment, phoneNumber.getText().toString().trim(), MobileMoneyMethod.ECOCASH);

            if (response.success()) {
                // Get the instructions to show to the user
                String instructions = response.instructions();

                //for testing
                pollUrl = response.pollUrl();
                //pollUrl = "https://www.paynow.co.zw/Interface/CheckPayment/?guid=d8401614-c7e2-42cc-860e-6b1c35e34d56";
                sharedPreferencesPayment.edit().putString("pollUrl", pollUrl).apply();

                myPollURL = pollUrl;

                Log.e("Response poll url",pollUrl);

                if(pollUrl==null){
                    pollUrl= sharedPreferencesPayment.getString("pollUrl",pollUrl);
                }
                // Check the status of the transaction with the specified pollUrl
                StatusResponse status = paynow.pollTransaction(pollUrl);

                timer.schedule( new TimerTask() {
                    @Override
                    public void run() {


                        Log.e("status",status.getStatus().toString());
                        if (status.isPaid()) {
                            // Yay! Transaction was paid for
                            //Toast.makeText(getBaseContext(),"// Yay! Transaction was paid for",Toast.LENGTH_LONG).show();
                            Log.e("Response","success payment");
                            sharedPreferencesPayment.edit().putBoolean("hasPayed",true).apply();
                            sharedPreferencesPayment.edit().putString("pollUrl", " ").apply();
                            sharedPreferencesPayment.edit().putLong("date", Calendar.getInstance().getTimeInMillis()).apply();

                            //open paid fragment
//                            Fragment home = new HomeFragment();
//                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                            fragmentTransaction.replace(R.id.nav_host_fragment, home);
//                            fragmentTransaction.addToBackStack(null);
//                            fragmentTransaction.commit();

                            Intent intent = new Intent(getContext(), Dashboard.class);
                            startActivity(intent);
                            getActivity().finish();

                            progressDialog.dismiss();


                        } else {

                            //Toast.makeText(getBaseContext(),"// Fuck! Transaction not ",Toast.LENGTH_LONG).show();
                            Log.e("Response","not success");
                            progressDialog.dismiss();

                        }

                    }
                },25000 );


            } else {
                // *freak out* //no connection likely
                Log.e("Response","failed connections from server");
                progressDialog.dismiss();
            }

            }catch (Exception e){

                e.printStackTrace();
                progressDialog.dismiss();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


    }

    void addPrice(){
        myRef.setValue("0.5");
    }

    void getSearchingFee(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                searchFee.setText(value);
                price = Double.parseDouble(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
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
                hasInternetConnection = true;
            } else {
                hasInternetConnection = false;
            }
        }



    }
}
