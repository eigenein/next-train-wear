package me.eigenein.nexttrainwear.data

import android.location.Location

data class Station(
    val code: String,
    val longName: String,
    val land: String,
    val latitude: Double,
    val longitude: Double
) {

    fun distanceTo(latitude: Double, longitude: Double): Float {
        val distance = FloatArray(1)
        Location.distanceBetween(this.latitude, this.longitude, latitude, longitude, distance)
        return distance[0]
    }

    fun routeTo(station: Station): Route = Route(this, station)

    companion object {
        fun findNearestStation(latitude: Double, longitude: Double): Station? =
            Stations.allStations.minBy { it.distanceTo(latitude, longitude) }
        fun findNearestStation(location: Location): Station? =
            findNearestStation(location.latitude, location.longitude)
    }
}
