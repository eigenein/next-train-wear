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
                updateStation(Station.findNearestStation(location))
            } else {
                Log.e(TAG, "Missing last known location")
                updateStation(null)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Forbidden to obtain last known location", e)
            updateStation(null)
        }

    }

    override fun onConnectionSuspended(i: Int) {
        // Do nothing.
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult)
        updateStation(null)
    }

    /**
     * Updates the fragment based on the current station.
     */
    private fun updateStation(station: Station?) {
        val departureStation = station ?: Stations.AMSTERDAM_CENTRAAL // FIXME
        Log.d(TAG, "Update station: " + departureStation)

        val destinations = selectDestinations(departureStation)
        Log.d(TAG, "Found destinations: " + destinations.size)

        // TODO: destinations adapter.
        // TODO: ride adapter.
    }

    /**
     * Select destination stations to go to from the specified departureStation.
     */
    private fun selectDestinations(departureStation: Station): List<Station> {
        // Select favorite stations sorted by distance.
        val stations = Preferences.getStations(activity)
            .filter { it != departureStation.code }
            .mapNotNull { Stations.STATION_BY_CODE[it] }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }
        if (!stations.isEmpty()) {
            return stations
        }

        // Select some nearby stations.
        return Stations.STATIONS
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }
            .take(NEARBY_STATION_COUNT)
    }

    companion object {

        private val TAG = TrainsFragment::class.java.simpleName
        private val NEARBY_STATION_COUNT = 3 // FIXME
    }
}
