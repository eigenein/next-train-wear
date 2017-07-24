package me.eigenein.nexttrainwear.fragments

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
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
import me.eigenein.nexttrainwear.adapters.JourneyOptionsAdapter
import me.eigenein.nexttrainwear.adapters.RoutesAdapter
import me.eigenein.nexttrainwear.data.DetectedStation
import me.eigenein.nexttrainwear.data.Station
import me.eigenein.nexttrainwear.data.Stations
import me.eigenein.nexttrainwear.utils.*
import java.util.concurrent.TimeUnit

class TrainsFragment : Fragment() {

    private val handler = Handler()
    private val disposable = CompositeDisposable()
    private val adapter = RoutesAdapter()

    private lateinit var progressLayout: View
    private lateinit var routesRecyclerView: WearableRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)

        progressLayout = view.findViewById(R.id.fragment_trains_progress_layout)

        routesRecyclerView = view.findViewById(R.id.fragment_trains_recycler_view)
        routesRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        routesRecyclerView.adapter = adapter
        routesRecyclerView.setHasFixedSize(true)
        LinearSnapHelper().attachToRecyclerView(routesRecyclerView)

        return view
    }

    override fun onResume() {
        super.onResume()

        // Obtain current location and build possible route list.
        val resumeTime = System.currentTimeMillis()
        disposable.add(
            GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .asFlowable()
                .flatMap {
                    LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .asFlowable(it)
                        .filter { it.time >= resumeTime } // we only want fresh location fixes
                        .take(1) // otherwise we'll get timeout error anyway
                        .timeout(LOCATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                }
                .map { DetectedStation(true, Station.findNearestStation(it)) }
                .doOnError { Log.e(LOG_TAG, "Failed to detect station: " + it) }
                .onErrorReturn {
                    val lastStation = Stations.STATION_BY_CODE[Preferences.getLastStationCode(activity)]
                    DetectedStation(false, lastStation ?: Stations.AMSTERDAM_CENTRAAL)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onStationDetected(it) }
        )

        // Refresh countdown and auto-scroll journey options when train departs.
        disposable.add(
            handler.asFlowable(COUNTDOWN_UPDATE_INTERVAL_MILLIS).subscribe {
                val journeyOptionsRecyclerView = routesRecyclerView
                    .findFirstVisibleViewHolder<RoutesAdapter.ViewHolder>()
                    ?.journeyOptionsRecyclerView
                journeyOptionsRecyclerView
                    ?.findVisibleViewHolders<JourneyOptionsAdapter.ViewHolder>()
                    ?.forEach {
                        if (it.refreshCountDown() in AUTO_SCROLL_THRESHOLD_MILLIS..0 && journeyOptionsRecyclerView.isScrollIdle()) {
                            // Departed. Scroll to the next one.
                            journeyOptionsRecyclerView.smoothScrollToPosition(it.adapterPosition + 1)
                            @Suppress("DEPRECATION")
                            activity.getVibrator().vibrate(AUTO_SCROLL_VIBRATE_PATTERN, -1)
                        }
                    }
            }
        )

        // Refresh journey options.
        disposable.add(
            handler.asFlowable(JOURNEY_OPTIONS_REFRESH_INTERVAL_MILLIS).subscribe {
                // FIXME: this can lead to a pair of concurrent requests.
                routesRecyclerView.findFirstVisibleViewHolder<RoutesAdapter.ViewHolder>()?.refreshJourneyOptions()
            }
        )
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
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
        routesRecyclerView.visibility = View.VISIBLE
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
            .mapNotNull { Stations.STATION_BY_CODE[it] }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }

        // Select some other stations sorted by distance from current.
        val allStations = Stations.ALL_STATIONS
            .filter { it.code !in stationCodes && it.code != departureStation.code }
            .sortedBy { departureStation.distanceTo(it.latitude, it.longitude) }

        return favoriteStations + allStations
    }

    companion object {

        private const val LOCATION_TIMEOUT_SECONDS = 5L // FIXME: exponetial backoff.
        private const val COUNTDOWN_UPDATE_INTERVAL_MILLIS = 1000L
        private const val JOURNEY_OPTIONS_REFRESH_INTERVAL_MILLIS = 60000L
        private const val AUTO_SCROLL_THRESHOLD_MILLIS = -1500L // FIXME: better ideas?

        private val AUTO_SCROLL_VIBRATE_PATTERN = longArrayOf(0L, 200L, 200L, 200L)
        private val LOG_TAG = TrainsFragment::class.java.simpleName
    }
}
