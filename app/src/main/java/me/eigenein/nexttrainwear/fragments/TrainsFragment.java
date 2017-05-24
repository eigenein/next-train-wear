package me.eigenein.nexttrainwear.fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Set;

import me.eigenein.nexttrainwear.Preferences;
import me.eigenein.nexttrainwear.R;
import me.eigenein.nexttrainwear.Station;
import me.eigenein.nexttrainwear.StationCatalogue;
import me.eigenein.nexttrainwear.Utils;

public class TrainsFragment
    extends Fragment
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final private static String TAG = TrainsFragment.class.getSimpleName();
    final private static Station DEFAULT_STATION = StationCatalogue.STATION_BY_CODE.get("ASD");

    public static TrainsFragment newInstance() {
        return new TrainsFragment();
    }

    private GoogleApiClient apiClient;

    public TrainsFragment() {
        // Do nothing.
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_trains, container, false);

        final WearableRecyclerView stationsRecyclerView =
            (WearableRecyclerView)view.findViewById(R.id.fragment_trains_stations_recycler_view);
        stationsRecyclerView.setLayoutManager(
            new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        new LinearSnapHelper().attachToRecyclerView(stationsRecyclerView);

        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        apiClient = new GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        apiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        apiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        try {
            final Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            if (location != null) {
                Log.d(TAG, "Found location: " + location);
                onStationDetected(Utils.INSTANCE.getNearestStation(location));
            } else {
                Log.e(TAG, "Missing last known location");
                onStationDetected(null);
            }
        } catch (final SecurityException e) {
            Log.e(TAG, "Forbidden to obtain last known location", e);
            onStationDetected(null);
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult);
        onStationDetected(null);
    }

    private void onStationDetected(Station station) {
        Log.d(TAG, "Detected station: " + station);
        if (station == null) {
            Log.w(TAG, "Using default station");
            station = DEFAULT_STATION;
        }

        final Set<String> favoriteStations = Preferences.INSTANCE.getFavoriteStations(getActivity());
        favoriteStations.remove(station.getCode()); // don't go to the current station
        // TODO: sort stations by distance from the current station.
        // TODO: empty station set (pick some nearest ones).
        // TODO: destinations adapter.
        // TODO: ride adapter.
    }
}
