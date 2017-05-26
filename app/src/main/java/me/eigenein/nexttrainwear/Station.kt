package me.eigenein.nexttrainwear

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

    companion object {
        fun findNearestStation(latitude: Double, longitude: Double): Station? =
            Stations.STATIONS.minBy { it.distanceTo(latitude, longitude) }
        fun findNearestStation(location: Location): Station? =
            findNearestStation(location.latitude, location.longitude)
    }
}
