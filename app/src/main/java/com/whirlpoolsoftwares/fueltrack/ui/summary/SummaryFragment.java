package com.whirlpoolsoftwares.fueltrack.ui.summary;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whirlpoolsoftwares.fueltrack.R;
import com.whirlpoolsoftwares.fueltrack.adapters.FuelCardsAdapter;
import com.whirlpoolsoftwares.fueltrack.models.FuelCards;

import java.util.List;

public class SummaryFragment extends Fragment {

  private SummaryViewModel summaryViewModel;

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    summaryViewModel = new ViewModelProvider(this).get(SummaryViewModel.class);
    View root = inflater.inflate(R.layout.fragment_summary, container, false);

    final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Summary </font>"));

    final TextView totalStations = root.findViewById(R.id.total);
    final RecyclerView recyclerView = root.findViewById(R.id.summary);

    summaryViewModel.getFuelCards().observe(getViewLifecycleOwner(), new Observer<List<FuelCards>>() {
      @Override
      public void onChanged(List<FuelCards> fuelCards) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new FuelCardsAdapter(fuelCards));
      }
    });

    summaryViewModel.getTotalStations().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        totalStations.setText(s);
      }
    });
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference("stations");
//        db.push().setValue(new Stations("Total Samora Michael","2910021-293192301","Yes","No","Yes"));
//        db.push().setValue(new Stations("Zuva Borrowdale Road","22321021-298943292301","Yes","Yes","Yes"));
//        db.push().setValue(new Stations("Trek Kambuzuma","291001231-2931923242341","No","No","No"));

    return root;
  }
}