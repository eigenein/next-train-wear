package me.eigenein.nexttrainwear

import android.location.Location

object Utils {

    fun getNearestStation(location: Location): Station? {
        return StationCatalogue.STATIONS.minBy { it.distanceTo(location) }
    }
}
