package me.eigenein.nexttrainwear.fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import me.eigenein.nexttrainwear.R;

public class TrainsFragment
    extends Fragment
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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
        return inflater.inflate(R.layout.fragment_trains, container, false);
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
        } catch (final SecurityException e) {
            // TODO
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // TODO
    }
}
