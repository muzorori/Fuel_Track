package com.whirlpoolsoftwares.fueltrack.ui.payments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.ui.home.HomeFragment;

import java.util.Timer;
import java.util.TimerTask;

import zw.co.paynow.constants.MobileMoneyMethod;
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;

public class Payments extends Fragment {

    String myPollURL;
    Payment payment;
    MobileInitResponse response;
    String pollUrl;
    Timer timer;
    private PaymentsViewModel mViewModel;
    private String instructions;
    private boolean paymentSuccess = false;
    Paynow paynow = new Paynow("6668", "b0b170e0-c950-4800-b56c-9ce4e4e02e14");

    public static Payments newInstance() {
        return new Payments();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.payments_fragment, container, false);
        final TextInputEditText phoneNumber = root.findViewById(R.id.phone_no);
        final Spinner spinner = root.findViewById(R.id.payments_options);
        MaterialButton pay = root.findViewById(R.id.make_payment);
        MaterialButton checkPayment = root.findViewById(R.id.check_payment);
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
                makePayment(phoneNumber.getText().toString(),MobileMoneyMethod.ECOCASH);
                checkPayment.setVisibility(View.VISIBLE);
            }
        });

        checkPayment.setVisibility(View.VISIBLE);

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
                            StatusResponse response = paynow.pollTransaction("https://www.paynow.co.zw/Interface/CheckPayment/?guid=d8401614-c7e2-42cc-860e-6b1c35e34d56");

                            if(response.isPaid()){
                                //open paid fragment
                                Fragment home = new HomeFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, home);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

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

        new MakePayment().execute();
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

        @Override
        protected Void doInBackground(Void... params) {


            //theo
            paynow = new Paynow("6668","b0b170e0-c950-4800-b56c-9ce4e4e02e14");


            payment = paynow.createPayment("Invoice 4hy2", "lovemoremajaka@gmail.com");

            // Passing in the name of the item and the price of the item
            payment.add("Search fee", 0.5);

            // test number
            response = paynow.sendMobile(payment,"0773330550" , MobileMoneyMethod.ECOCASH);

            Log.e("paying","paying");
            if (response.success()) {
                // Get the instructions to show to the user
                String instructions = response.instructions();

                Log.e("instructions",response.getInstructions());
                // Get the poll URL of the transaction
                pollUrl = response.pollUrl();

                myPollURL = pollUrl;

                Log.e("Response",pollUrl);

                timer.schedule( new TimerTask() {
                    @Override
                    public void run() {

                        // Check the status of the transaction with the specified pollUrl
                        StatusResponse status = paynow.pollTransaction(pollUrl);

                        Log.e("status",status.getStatus().toString());
                        if (status.isPaid()) {
                            // Yay! Transaction was paid for
                            //Toast.makeText(getBaseContext(),"// Yay! Transaction was paid for",Toast.LENGTH_LONG).show();
                            Log.e("Response","suuccess payment");


                        } else {

                            //Toast.makeText(getBaseContext(),"// Fuck! Transaction not ",Toast.LENGTH_LONG).show();
                            Log.e("Response","not succucess");

                        }

                    }
                },4000 );


            } else {
                // *freak out* //no connection likely
                Log.e("Response","failed connections from server");
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }




    }

}
