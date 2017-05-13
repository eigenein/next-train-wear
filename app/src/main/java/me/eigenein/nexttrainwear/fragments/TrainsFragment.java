package me.eigenein.nexttrainwear.fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import me.eigenein.nexttrainwear.R;
import me.eigenein.nexttrainwear.Station;
import me.eigenein.nexttrainwear.StationCatalogue;
import me.eigenein.nexttrainwear.Utils;

public class TrainsFragment
    extends Fragment
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final private static String TAG = TrainsFragment.class.getSimpleName();

    private View detectingLocationView;

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
        detectingLocationView = view.findViewById(R.id.fragment_trains_detecting_location);
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
                onStationDetected(Utils.getNearestStation(location));
            } else {
                Log.e(TAG, "Missing last known location");
                onStationDetected(Utils.DEFAULT_STATION);
            }
        } catch (final SecurityException e) {
            Log.e(TAG, "Forbidden to obtain last known location", e);
            onStationDetected(Utils.DEFAULT_STATION);
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult);
        onStationDetected(Utils.DEFAULT_STATION);
    }

    private void onStationDetected(final Station station) {
        Log.d(TAG, "Detected station: " + station);
        detectingLocationView.setVisibility(View.GONE);
    }
}
