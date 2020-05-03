package com.whirlpoolsoftwares.fueltrack.ui.summary;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.whirlpoolsoftwares.fueltrack.models.FuelCards;
import com.whirlpoolsoftwares.fueltrack.models.Stations;

import java.util.ArrayList;
import java.util.List;

public class SummaryViewModel extends ViewModel {

  private MutableLiveData<String> totalStations;
  private MutableLiveData<List<FuelCards>> fuelCards;
  private List<FuelCards> cards = new ArrayList<FuelCards>();
  private FirebaseDatabase database = FirebaseDatabase.getInstance();

  public LiveData<String> getTotalStations(){
    totalStations = new MutableLiveData<String>();

    DatabaseReference stations = database.getReference("stations");
    stations.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        totalStations.setValue(Long.toString(dataSnapshot.getChildrenCount()));
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    return totalStations;
  }

  public LiveData<List<FuelCards>> getFuelCards(){
    if(fuelCards == null){
      fuelCards = new MutableLiveData<List<FuelCards>>();
      getCards();
    }
    return fuelCards;
  }

  private void getCards(){

    Query diesel = database.getReference("stations");
    diesel.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Stations station;
        int d = 0,p = 0,g = 0;
        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
          station = snapshot.getValue(Stations.class);

          if(station.getDiesel().equals("Yes")){
            d += 1;
          }

          if(station.getPetrol().equals("Yes")){
            p+=1;
          }

          if(station.getGas().equals("Yes")){
            g+=1;
          }
        }

        cards.clear();
        FuelCards card = new FuelCards(d,"Station(s) with Diesel now");
        cards.add(card);
        card = new FuelCards(p,"Station(s) with Petrol now");
        cards.add(card);
        card = new FuelCards(g,"Station(s) with Gas now");
        cards.add(card);
        //GET STATIONS
        //FILTER STATIONS
        //CREATE FUEL CARDS
        //RETURN FUEL CARD

        fuelCards.setValue(cards);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }
}