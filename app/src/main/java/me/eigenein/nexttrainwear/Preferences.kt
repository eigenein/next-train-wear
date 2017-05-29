package me.eigenein.nexttrainwear

import android.content.Context
import android.preference.PreferenceManager

object Preferences {

    private const val keyStations = "stations"
    private const val keyLastStation = "last_station_code"

    private val emptySet = hashSetOf<String>()

    /**
     * Get codes of favorite stations.
     */
    fun getStations(context: Context): Set<String> =
        getDefault(context).getStringSet(keyStations, emptySet)

    /**
     * Set codes of favorite stations.
     */
    fun setStations(context: Context, stations: Set<String>) =
        getDefault(context).edit().putStringSet(keyStations, stations).apply()

    fun getLastStationCode(context: Context): String? =
        getDefault(context).getString(keyLastStation, null)

    fun setLastStationCode(context: Context, stationCode: String) =
        getDefault(context).edit().putString(keyLastStation, stationCode).apply()

    private fun getDefault(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
}
