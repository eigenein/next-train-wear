package me.eigenein.nexttrainwear.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.eigenein.nexttrainwear.Preferences;
import me.eigenein.nexttrainwear.R;
import me.eigenein.nexttrainwear.StationCatalogue;
import me.eigenein.nexttrainwear.adapters.StationsAdapter;

public class StationsFragment extends Fragment {

    private WearableRecyclerView recyclerView;

    public StationsFragment() {
        // Do nothing.
    }

    public static StationsFragment newInstance() {
        return new StationsFragment();
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        recyclerView = (WearableRecyclerView)inflater.inflate(R.layout.fragment_stations, container, false);
        return recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setAdapter(new StationsAdapter(Preferences.getStations(getActivity())));
    }
}
