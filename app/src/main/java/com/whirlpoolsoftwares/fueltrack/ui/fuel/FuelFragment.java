package com.whirlpoolsoftwares.fueltrack.ui.fuel;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.adapters.FuelAdapter;
import com.whirlpoolsoftwares.fueltrack.models.Fuel;

import java.util.List;

public class FuelFragment extends Fragment {

  private FuelViewModel fuelViewModel;

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    fuelViewModel = new ViewModelProvider(this).get(FuelViewModel.class);

    final View root = inflater.inflate(R.layout.fragment_fuel, container, false);


    final RecyclerView recyclerView = root.findViewById(R.id.fuel_options);
    final LinearLayout progress = root.findViewById(R.id.progress);
    final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Fuels </font>"));

    fuelViewModel.getFuels().observe(getViewLifecycleOwner(), new Observer<List<Fuel>>() {
      @Override
      public void onChanged(List<Fuel> fuels) {
        FuelAdapter fuelAdapter = new FuelAdapter(fuels,getActivity().getSupportFragmentManager(), actionBar);

        if(fuels.isEmpty()){
          fuelAdapter  = new FuelAdapter(getActivity().getSupportFragmentManager(),actionBar);
        }

        recyclerView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(fuelAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
      }
    });

    recyclerView.setVisibility(View.VISIBLE);
    progress.setVisibility(View.GONE);
    //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setAdapter(new FuelAdapter(getActivity().getSupportFragmentManager(), actionBar));
    recyclerView.setLayoutManager(linearLayoutManager);

    return root;
  }
}