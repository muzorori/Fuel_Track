package com.whirlpoolsoftwares.fueltrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.models.Stations;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder>{
    private List<Stations> stationsList = new ArrayList<>();
    private GoogleMap mMap;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name,address;
        private CardView card;
        private ImageView imageView;

        public ViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.station_name);
            address = itemView.findViewById(R.id.station_address);
            card = itemView.findViewById(R.id.station_card);
            imageView = itemView.findViewById(R.id.station_logo);
        }
    }

    @NonNull
    @Override
    public StationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.station,parent,false);
        return new StationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stations station = stationsList.get(position);
        String name = station.getName();
        holder.name.setText(name);
        holder.address.setText(station.getAddress());

        if(name.contains("Total")){
            int image = R.drawable.total;
            holder.imageView.setImageResource(image);
        }

        else if(name.contains("Zuva")){
            int image = R.drawable.zuva;
            holder.imageView.setImageResource(image);
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null){
                    mMap.clear();
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .title(station.getName())
                            .snippet(station.getAddress())
                            .position(new LatLng(station.getLatitude(),station.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(station.getLatitude(),station.getLongitude()), 12));
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stationsList.size();
    }

    public StationAdapter(List<Stations> stations,GoogleMap mMap){
        this.stationsList = stations;
        this.mMap = mMap;
    }
}
