package com.whirlpoolsoftwares.fueltrack.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.whirlpoolsoftwares.fueltrack.Login;
import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.models.Fuel;
import com.whirlpoolsoftwares.fueltrack.ui.fuel.FuelFragment;
import com.whirlpoolsoftwares.fueltrack.ui.payments.Payments;

import java.util.ArrayList;
import java.util.List;

public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.ViewHolder> {

  private List<Fuel> fuels = new ArrayList<>();
  private FragmentManager fragmentManager;
  private ActionBar toolbar;
  private FuelAdapter context = this;

  public class ViewHolder extends RecyclerView.ViewHolder{
    //private ImageView icon;
    private TextView result,fuel;
    private CardView card;

    public ViewHolder(View itemView){
      super(itemView);

      //icon = itemView.findViewById(R.id.icon);
      result = itemView.findViewById(R.id.result);
      fuel = itemView.findViewById(R.id.fuel);
      card = itemView.findViewById(R.id.fuel_card);
    }
  }

  @NonNull
  @Override
  public FuelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fuel_card,parent,false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull FuelAdapter.ViewHolder holder, int position) {
    final Fuel fuel = fuels.get(position);
    holder.fuel.setText(fuel.getName());
    //holder.icon.setImageResource(fuel.getIcon());
    holder.result.setText(fuel.getStatus());
    holder.card.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(fuel.getStatus().equals("Available")){
          Activity activity = (Activity) v.getContext();
          Fragment payments = new Payments();
          Bundle bundle = new Bundle();
          bundle.putString("fuel",fuel.getName());
          if(toolbar != null){
            toolbar.setTitle("Search payment for " + fuel.getName());
          }

          payments.setArguments(bundle);
          FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
          fragmentTransaction.replace(R.id.fuel, payments);
          fragmentTransaction.addToBackStack(null);
          fragmentTransaction.commit();


        }

        else{

          //show unavailable dialog
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                    builder.setTitle("No Internet Connection")
                            .setMessage("Please check you internet connectivity and try again.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
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

      }
    });
  }

  @Override
  public int getItemCount() {
    return fuels.size();
  }

  public FuelAdapter(FragmentManager f, ActionBar toolbar){
    this.toolbar = toolbar;
    this.fragmentManager = f;
    Fuel fuel = new Fuel("Diesel","Not Available");
    fuels.add(fuel);
    fuel = new Fuel("Petrol","Not Available");
    fuels.add(fuel);
    fuel = new Fuel("Gas","Not Available");
    fuels.add(fuel);
  }

  public FuelAdapter(List<Fuel> fuels, FragmentManager f, ActionBar toolbar){
    this.fuels.clear();
    this.toolbar = toolbar;
    this.fragmentManager = f;
    this.fuels = fuels;
  }
}
