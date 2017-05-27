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
import me.eigenein.nexttrainwear.interfaces.AmbientListenable
import me.eigenein.nexttrainwear.interfaces.AmbientListener

class TrainsFragment : Fragment(), AmbientListener {

    private var apiClient: GoogleApiClient? = null
    private var ambientListenable: AmbientListenable? = null

    /**
     * Scrolls destinations horizontally.
     */
    private lateinit var destinationsRecyclerView: WearableRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)

        destinationsRecyclerView = view.findViewById(R.id.fragment_trains_recycler_view) as WearableRecyclerView
        destinationsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
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
                updateDepartureStation(null)
            }
            .build()
        ambientListenable = activity as AmbientListenable
    }

    override fun onResume() {
        super.onResume()

        // FIXME: show and hide "detecting location" progress.
        apiClient!!.connect()
        ambientListenable?.ambientListener = this
    }

    override fun onPause() {
        super.onPause()
        
        apiClient!!.disconnect()
        ambientListenable?.ambientListener = null
    }

    override fun onEnterAmbient() {
        // TODO: try to go with app theme changes.
    }

    override fun onUpdateAmbient() {
        // TODO: try to go with app theme changes.
    }

    override fun onExitAmbient() {
        // TODO: try to go with app theme changes.
    }

    private fun onApiClientConnected() {
        // FIXME: show and hide "detecting location" progress.
        try {
            val location = LocationServices.FusedLocationApi.getLastLocation(apiClient)
            if (location != null) {
                Log.d(TAG, "Found location: " + location)
                updateDepartureStation(Station.findNearestStation(location))
            } else {
                Log.e(TAG, "Missing last known location")
                updateDepartureStation(null)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Forbidden to obtain last known location", e)
            updateDepartureStation(null)
        }
    }

    /**
     * Updates the fragment based on the departure station.
     */
    private fun updateDepartureStation(station: Station?) {
        val departureStation = station ?: Stations.AMSTERDAM_CENTRAAL // FIXME
        Log.d(TAG, "Update departure station: " + departureStation)

        val destinations = selectDestinations(departureStation)
        Log.d(TAG, "Found destinations: " + destinations.size)

        destinationsRecyclerView.adapter = RoutesAdapter(destinations.map { Route(departureStation, it) })
    }

    /**
     * Select destination stations to go to from the specified departure station.
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
