package me.eigenein.nexttrainwear

import android.location.Location

data class Station(
    val code: String,
    val longName: String,
    val land: String,
    val latitude: Float,
    val longitude: Float
) {

    fun distanceTo(location: Location): Float {
        val distance = FloatArray(1)
        Location.distanceBetween(latitude.toDouble(), longitude.toDouble(), location.latitude, location.longitude, distance)
        return distance[0]
    }
}
