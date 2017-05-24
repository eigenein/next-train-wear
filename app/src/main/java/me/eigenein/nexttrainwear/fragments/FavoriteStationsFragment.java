package me.eigenein.nexttrainwear.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.eigenein.nexttrainwear.Preferences;
import me.eigenein.nexttrainwear.R;
import me.eigenein.nexttrainwear.adapters.StationCatalogueAdapter;

public class FavoriteStationsFragment extends Fragment {

    private WearableRecyclerView recyclerView;

    public FavoriteStationsFragment() {
        // Do nothing.
    }

    public static FavoriteStationsFragment newInstance() {
        return new FavoriteStationsFragment();
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        recyclerView = (WearableRecyclerView)inflater.inflate(R.layout.fragment_favorite_stations, container, false);
        return recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setAdapter(new StationCatalogueAdapter(Preferences.INSTANCE.getFavoriteStations(getActivity())));
    }
}
