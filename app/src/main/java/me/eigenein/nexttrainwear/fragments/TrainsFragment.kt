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
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import me.eigenein.nexttrainwear.*
import me.eigenein.nexttrainwear.adapters.RoutesAdapter

class TrainsFragment : Fragment() {

    private var apiClient: GoogleApiClient? = null

    /**
     * Scrolls destinations horizontally.
     */
    private var destinationsRecyclerView: WearableRecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)

        destinationsRecyclerView = view.findViewById(R.id.fragment_trains_recycler_view) as WearableRecyclerView?
        destinationsRecyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(destinationsRecyclerView)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        apiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnectionSuspended(i: Int) = Unit
                override fun onConnected(bundle: Bundle?) = onApiClientConnected()
            })
            .addOnConnectionFailedListener { connectionResult ->
                Log.e(TAG, "Connection failed: " + connectionResult)
                updateStation(null)
            }
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

    private fun onApiClientConnected() {
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

    /**
     * Updates the fragment based on the current station.
     */
    private fun updateStation(station: Station?) {
        val departureStation = station ?: Stations.AMSTERDAM_CENTRAAL // FIXME
        Log.d(TAG, "Update station: " + departureStation)

        val destinations = selectDestinations(departureStation)
        Log.d(TAG, "Found destinations: " + destinations.size)

        destinationsRecyclerView!!.adapter = RoutesAdapter(destinations.map { Route(departureStation, it) })
    }

    /**
     * Select destinationStation stations to go to from the specified departureStation.
     */
    private fun selectDestinations(departureStation: Station): List<Station> {
        val stationCodes = Preferences.getStations(activity)

        // Select favorite stations sorted by distance.
        val favoriteStations = stationCodes
            .filter { it != departureStation.code }
            .mapNotNull { Stations.STATION_BY_CODE[it] }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }

        // Select all other stations sorted by distance from current.
        val allStations = Stations.STATIONS
            .filter { it.code !in stationCodes && it.code != departureStation.code }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }

        return favoriteStations + allStations
    }

    companion object {

        private val TAG = TrainsFragment::class.java.simpleName
    }
}
