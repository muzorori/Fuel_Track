package com.whirlpoolsoftwares.fueltrack.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.whirlpoolsoftwares.fueltrack.models.FuelCards;
import com.whirlpoolsoftwares.fueltrack.models.Stations;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> totalStations;
    private MutableLiveData<List<Stations>> stations;
    private List<Stations> stationsToo = new ArrayList<Stations>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public HomeViewModel() {

    }

    public LiveData<List<Stations>> getLiveStations() {
        stations = new MutableLiveData<List<Stations>>();
        getStations();
        return stations;
    }

    private void getStations(){
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

                //set value
                stations.setValue(stationsToo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}