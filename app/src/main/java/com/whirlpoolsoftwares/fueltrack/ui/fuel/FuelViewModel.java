package com.whirlpoolsoftwares.fueltrack.ui.fuel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whirlpoolsoftwares.fueltrack.models.Fuel;

import java.util.ArrayList;
import java.util.List;

public class FuelViewModel extends ViewModel {

  private MutableLiveData<List<Fuel>> fuels;

  public LiveData<List<Fuel>> getFuels() {
    if(fuels == null) {
      fuels = new MutableLiveData<List<Fuel>>();
      loadData();
    }

    return fuels;
  }

  private void loadData(){
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("fuels");

    dbRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<Fuel> fuelList = new ArrayList<>();
        for (DataSnapshot snapShot:dataSnapshot.getChildren()) {
          Fuel fuel = snapShot.getValue(Fuel.class);
          fuelList.add(fuel);
        }

        fuels.setValue(fuelList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    dbRef.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}