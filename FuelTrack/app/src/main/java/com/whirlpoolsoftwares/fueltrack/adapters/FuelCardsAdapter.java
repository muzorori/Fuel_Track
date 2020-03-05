package com.whirlpoolsoftwares.fueltrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.models.FuelCards;

import java.util.ArrayList;
import java.util.List;

public class FuelCardsAdapter extends RecyclerView.Adapter<FuelCardsAdapter.ViewHolder> {

    private List<FuelCards> fuelCards = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView total,text;
        //private CardView cardView;

        public ViewHolder(View itemView){
            super(itemView);

            total = itemView.findViewById(R.id.total);
            text = itemView.findViewById(R.id.text);
        }
    }

    @NonNull
    @Override
    public FuelCardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summaries,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FuelCardsAdapter.ViewHolder holder, int position) {
        final FuelCards fuelCard = fuelCards.get(position);
        holder.total.setText(Long.toString(fuelCard.getTotal()));
        holder.text.setText(fuelCard.getText());
    }

    @Override
    public int getItemCount() {
        return fuelCards.size();
    }

    public FuelCardsAdapter(){

    }

    public FuelCardsAdapter(List<FuelCards> fuels){
        this.fuelCards.clear();
        this.fuelCards = fuels;
    }
}
