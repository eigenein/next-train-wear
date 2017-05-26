package me.eigenein.nexttrainwear.fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.wearable.view.WearableRecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import me.eigenein.nexttrainwear.*

class TrainsFragment : Fragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var apiClient: GoogleApiClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)

        val stationsRecyclerView = view.findViewById(R.id.fragment_trains_stations_recycler_view) as WearableRecyclerView
        stationsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(stationsRecyclerView)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        apiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
    }

    override fun onResume() {
        super.onResume()
        apiClient!!.connect()
    }

    override fun onPause() {
        super.onPause()
        apiClient!!.disconnect()
    }

    override fun onConnected(bundle: Bundle?) {
        try {
            val location = LocationServices.FusedLocationApi.getLastLocation(apiClient)
            if (location != null) {
                Log.d(TAG, "Found location: " + location)
                onStationDetected(Station.findNearestStation(location))
            } else {
                Log.e(TAG, "Missing last known location")
                onStationDetected(null)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Forbidden to obtain last known location", e)
            onStationDetected(null)
        }

    }

    override fun onConnectionSuspended(i: Int) {
        // Do nothing.
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult)
        onStationDetected(null)
    }

    private fun onStationDetected(station: Station?) {
        val currentStation = station ?: Stations.AMSTERDAM_CENTRAAL
        Log.d(TAG, "Detected station: " + currentStation)

        val favoriteStations = Preferences.getStations(activity)
        // TODO: exclude current station.
        // TODO: sort stations by distance from the current station.
        // TODO: empty station set (pick some nearest ones).
        // TODO: destinations adapter.
        // TODO: ride adapter.
    }

    companion object {

        private val TAG = TrainsFragment::class.java.simpleName
    }
}
