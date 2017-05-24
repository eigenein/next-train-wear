package me.eigenein.nexttrainwear

import android.location.Location

class Station(
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

    override fun toString(): String {
        return String.format("%s (%s): %s, %s", longName, code, latitude, longitude)
    }
}
