package me.eigenein.nexttrainwear.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.wearable.view.WearableRecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.adapters.RoutesAdapter
import me.eigenein.nexttrainwear.data.DetectedStation
import me.eigenein.nexttrainwear.data.Route
import me.eigenein.nexttrainwear.data.Station
import me.eigenein.nexttrainwear.data.Stations
import me.eigenein.nexttrainwear.utils.Preferences
import me.eigenein.nexttrainwear.utils.asFlowable
import java.util.concurrent.TimeUnit

class TrainsFragment : Fragment() {

    private val disposable = CompositeDisposable()
    private val adapter = RoutesAdapter()

    private lateinit var progressLayout: View
    private lateinit var destinationsRecyclerView: WearableRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)

        progressLayout = view.findViewById(R.id.fragment_trains_progress_layout)

        destinationsRecyclerView = view.findViewById(R.id.fragment_trains_recycler_view)
        destinationsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        destinationsRecyclerView.adapter = adapter
        destinationsRecyclerView.recycledViewPool.setMaxRecycledViews(RoutesAdapter.VIEW_TYPE, MAX_RECYCLED_VIEW_NUMBER)
        LinearSnapHelper().attachToRecyclerView(destinationsRecyclerView)

        return view
    }

    override fun onResume() {
        super.onResume()

        disposable.add(
            GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .asFlowable()
                .flatMap {
                    LocationRequest.create()
                        .setNumUpdates(1)
                        .asFlowable(it)
                        .take(1) // otherwise we'll get timeout error anyway
                        .timeout(LOCATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                }
                .map { DetectedStation(true, Station.findNearestStation(it)) }
                .doOnError { Log.e(LOG_TAG, "Failed to detect station: " + it) }
                .onErrorReturn {
                    val lastStation = Stations.stationByCode[Preferences.getLastStationCode(activity)]
                    DetectedStation(false, lastStation ?: Stations.amsterdamCentraal)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onStationDetected(it) }
        )
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
        // Important: remove all items so that all delayed callbacks are removed.
        // Otherwise onViewRecycled is not called and the app continues to send the requests.
        adapter.swap(false, listOf<Route>())
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * Updates the fragment based on the detected station.
     */
    private fun onStationDetected(detectedStation: DetectedStation) {
        Log.d(LOG_TAG, "Departure station: " + detectedStation)
        Preferences.setLastStationCode(activity, detectedStation.station.code)

        val destinations = selectDestinations(detectedStation.station)
        Log.d(LOG_TAG, "Selected destinations: " + destinations.size)

        // Show routes view.
        progressLayout.visibility = View.GONE
        destinationsRecyclerView.visibility = View.VISIBLE
        adapter.swap(
            detectedStation.usingLocation,
            destinations.map { detectedStation.station.routeTo(it) }
        )
    }

    /**
     * Select destination stations to go to from the specified departure station.
     */
    private fun selectDestinations(departureStation: Station): List<Station> {
        val stationCodes = Preferences.getStations(activity)

        // Select favorite stations sorted by distance.
        val favoriteStations = stationCodes
            .filter { it != departureStation.code }
            .mapNotNull { Stations.stationByCode[it] }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }

        // Select some other stations sorted by distance from current.
        val allStations = Stations.allStations
            .filter { it.code !in stationCodes && it.code != departureStation.code }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }
            .take(NUMBER_OF_NEAREST_STATIONS)

        return favoriteStations + allStations
    }

    companion object {

        private const val NUMBER_OF_NEAREST_STATIONS = 10 // TODO: make configurable
        private const val LOCATION_TIMEOUT_SECONDS = 5L
        private const val MAX_RECYCLED_VIEW_NUMBER = 0

        private val LOG_TAG = TrainsFragment::class.java.simpleName
    }
}
