package com.whirlpoolsoftwares.fueltrack.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.adapters.StationAdapter;
import com.whirlpoolsoftwares.fueltrack.models.Fuel;
import com.whirlpoolsoftwares.fueltrack.models.Stations;

import org.apache.commons.math3.geometry.euclidean.threed.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.StatusResponse;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private HomeViewModel homeViewModel;

    private boolean mLocationPermissionGranted = true;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341,151.2106085);
    private static final int DEFAULT_ZOOM = 10;
    private static final int PERMISSIONS_REQUEST_ACCESS = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_TOO = 0;
    private Location mLastKnownLocation;
    private int REQUEST_CHECK_SETTINGS = 1;
    private CameraPosition mCameraPosition;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private double latitude;
    private double longitude;
    private List<Stations> stationsToo = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_TOO);
        }

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLocations();

//        homeViewModel.getLiveStations().observe(getViewLifecycleOwner(), new Observer<List<Stations>>() {
//            @Override
//            public void onChanged(List<Stations> stations) {
//                stationsToo = stations;
//            }
//        });

        backgroundChecking(root);

        ImageView reload = root.findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check payment status
                stationsToo.clear();
                backgroundChecking(root);
            }
        });

        return root;
    }

    private void getLocations(){
        final SharedPreferences preferences = getActivity().getSharedPreferences("fuels", Context.MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query stations = database.getReference("stations");

        stations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Stations station = snapshot.getValue(Stations.class);

                    if (station.getDiesel().equals("Yes") && preferences.getBoolean("Diesel", false)) {
                        mMap.addMarker(new MarkerOptions()
                                .title(station.getName())
                                .snippet(station.getAddress())
                                .position(new LatLng(station.getLatitude(),station.getLongitude()))
                        );
                    }

                    else if (station.getPetrol().equals("Yes") && preferences.getBoolean("Petrol", false)) {
                        mMap.addMarker(new MarkerOptions()
                                .title(station.getName())
                                .snippet(station.getAddress())
                                .position(new LatLng(station.getLatitude(),station.getLongitude()))
                        );
                    }

                    else if (station.getGas().equals("Yes") && preferences.getBoolean("Gas", false)) {
                        mMap.addMarker(new MarkerOptions()
                                .title(station.getName())
                                .snippet(station.getAddress())
                                .position(new LatLng(station.getLatitude(),station.getLongitude()))
                        );
                    }
                }
                //get current location
                try {
                    if (mLocationPermissionGranted) {
                        LocationRequest locationRequest = LocationRequest.create();
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                        SettingsClient client = LocationServices.getSettingsClient(getActivity());
                        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isSuccessful()) {
                                            mLastKnownLocation = task.getResult();
                                            LatLng lastLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                            if (mLastKnownLocation != null) {
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, DEFAULT_ZOOM));
                                                getStations(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                            } else {
                                                //ask for location to be turned on
                                            }
                                        }
                                    }
                                });
                            }
                        });

                        task.addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof ResolvableApiException) {
                                    // Location settings are not satisfied, but this can be fixed
                                    // by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        ResolvableApiException resolvable = (ResolvableApiException) e;
                                        resolvable.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException sendEx) {
                                        // Ignore the error.
                                    }
                                }
                            }
                        });
                    }
                } catch (SecurityException e) {
                    Log.e("Exception : ", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;
        updateLocationUI();
        getLocations();
    }

    private void updateLocationUI(){
        if(mMap == null){
            return;
        }
        try{
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }catch (SecurityException e){
            Log.e("Exception",e.getMessage());
        }
    }

    private void addStations(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("stations");

        Stations station = new Stations("Total Samora Michael","Yes","No","No",78.090890,780.90998,"21 Samora Michael Road");
        ref.push().setValue(station);
        station = new Stations("Total Kwame Nkurumah","No","Yes","Yes",-17.82995,31.043605,"Corner Kwame Nkrumah & Park Street, Harare, Harare");
        ref.push().setValue(station);
        station = new Stations("Total Chinhoyi","No","No","No",-17.834111,31.043250,"36 Robert Mugabe Road, Robert Mugabe Rd, Harare");
        ref.push().setValue(station);
        station = new Stations("Zuva Prestol!","No","Yes","No",-17.818180,31.037477,"ZUVA Montague 1490 Cnr J Chinamano, Harare St, Harare");
        ref.push().setValue(station);
        station = new Stations("Zuva Matthews","No","yes","No",-17.844229,31.038574,"19-21 Lobengula Rd, Harare");
        ref.push().setValue(station);
        station = new Stations("Engen Mbare","yes","No","No",-17.857711,31.036579,"Ardbennie Rd, Harare");
        ref.push().setValue(station);

        ref = FirebaseDatabase.getInstance().getReference("fuels");
        Fuel fuel = new Fuel("Diesel","Available");
        ref.push().setValue(fuel);
        fuel = new Fuel("Petrol","Not Available");
        ref.push().setValue(fuel);
        fuel = new Fuel("Gas","Available");
        ref.push().setValue(fuel);
        fuel = new Fuel("Parrafin","Available");
        ref.push().setValue(fuel);
    }

    private void getStations(double latitude,double longitude){
        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(getContext(),locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
            //get current city
            Log.d("Current City",addresses.get(0).getAddressLine(0));
            //calculate short distances from current location of service stations
            //show service stations

        }catch (Exception e){
            Log.e("Address Error",e.getLocalizedMessage());
        }
    }

    private void backgroundChecking(View root){
        SharedPreferences preferences = getActivity().getSharedPreferences("pollUrl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(preferences.getString("url","").isEmpty()){
            editor.putString("url","https://www.paynow.co.zw/Interface/CheckPayment/?guid=d8401614-c7e2-42cc-860e-6b1c35e34d56");
            editor.apply();
        }

        String pollUrl = preferences.getString("url","");
        LinearLayout loader = root.findViewById(R.id.loader);
        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!pollUrl.isEmpty()){
                    Paynow paynow = new Paynow("6668","b0b170e0-c950-4800-b56c-9ce4e4e02e14");
                    //StatusResponse response = paynow.pollTransaction(myPollURL);
                    //paid status response for testing
                    StatusResponse response = paynow.pollTransaction(pollUrl);

                    if(response.isPaid()){
                        //open paid fragment
                        //connect to database
                        Query all = database.getReference("stations");
                        //get records
                        all.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Stations station;
                                //add each record to stations
                                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                    station = snapshot.getValue(Stations.class);

                                    if(station.getDiesel().equals("Yes") || station.getGas().equals("Yes") || station.getPetrol().equals("Yes")){
                                        stationsToo.add(station);
                                    }
                                }
                                LinearLayout loader = root.findViewById(R.id.loader);
                                loader.setVisibility(View.GONE);

                                RecyclerView recyclerView = root.findViewById(R.id.recycler);
                                StationAdapter stationAdapter = new StationAdapter(stationsToo,mMap);
                                GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1, RecyclerView.HORIZONTAL,false);
                                recyclerView.setAdapter(stationAdapter);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Log.e("Payment","Payment Done");
                    }
                    else{
                        //notify to check after a few minutes

                        Log.e("Payment","Payment Not Done");
                    }
                }
            }
        });

        //if working is network detected
        thread.start();
    }
}