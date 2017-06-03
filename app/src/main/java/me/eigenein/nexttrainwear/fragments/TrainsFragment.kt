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
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import me.eigenein.nexttrainwear.Preferences
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.adapters.RoutesAdapter
import me.eigenein.nexttrainwear.asFlowable
import me.eigenein.nexttrainwear.data.DetectedStation
import me.eigenein.nexttrainwear.data.Station
import me.eigenein.nexttrainwear.data.Stations
import java.util.concurrent.TimeUnit

class TrainsFragment : Fragment() {

    private val disposable = CompositeDisposable()

    private lateinit var progressLayout: View
    private lateinit var destinationsRecyclerView: WearableRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)

        progressLayout = view.findViewById(R.id.fragment_trains_progress_layout)

        destinationsRecyclerView = view.findViewById(R.id.fragment_trains_recycler_view) as WearableRecyclerView
        destinationsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(destinationsRecyclerView)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()

        GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .asFlowable()
            .flatMap {
                LocationRequest.create()
                    .setNumUpdates(1)
                    .asFlowable(it)
                    .take(1) // otherwise we'll get timeout error anyway
                    .timeout(locationTimeoutSeconds, TimeUnit.SECONDS)
            }
            .map { DetectedStation(true, Station.findNearestStation(it)) }
            .doOnError { Log.e(logTag, "Failed to detect station: " + it) }
            .onErrorReturn {
                val lastStation = Stations.stationByCode[Preferences.getLastStationCode(activity)]
                DetectedStation(false, lastStation ?: Stations.amsterdamCentraal)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onStationDetected(it) }
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * Updates the fragment based on the detected station.
     */
    private fun onStationDetected(detectedStation: DetectedStation) {
        Log.d(logTag, "Departure station: " + detectedStation)
        Preferences.setLastStationCode(activity, detectedStation.station.code)

        val destinations = selectDestinations(detectedStation.station)
        Log.d(logTag, "Found destinations: " + destinations.size)

        // Show routes view.
        progressLayout.visibility = View.GONE
        destinationsRecyclerView.visibility = View.VISIBLE
        destinationsRecyclerView.adapter = RoutesAdapter(
            detectedStation.usingLocation, destinations.map { detectedStation.station.routeTo(it) })
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
            .take(numberOfNearestStations)

        return favoriteStations + allStations
    }

    companion object {

        private const val numberOfNearestStations = 10 // TODO: make configurable
        private const val locationTimeoutSeconds = 5L

        private val logTag = TrainsFragment::class.java.simpleName
    }
}
