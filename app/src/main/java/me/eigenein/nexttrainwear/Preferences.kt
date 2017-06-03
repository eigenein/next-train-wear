package me.eigenein.nexttrainwear

import android.content.Context
import android.preference.PreferenceManager

object Preferences {

    private const val STATIONS_KEY = "stations"
    private const val LAST_STATION_CODE_KEY = "last_station_code"

    private val emptySet = hashSetOf<String>()

    /**
     * Get codes of favorite stations.
     */
    fun getStations(context: Context): Set<String> =
        getDefault(context).getStringSet(STATIONS_KEY, emptySet)

    /**
     * Set codes of favorite stations.
     */
    fun setStations(context: Context, stations: Set<String>) =
        getDefault(context).edit().putStringSet(STATIONS_KEY, stations).apply()

    fun getLastStationCode(context: Context): String? =
        getDefault(context).getString(LAST_STATION_CODE_KEY, null)

    fun setLastStationCode(context: Context, stationCode: String) =
        getDefault(context).edit().putString(LAST_STATION_CODE_KEY, stationCode).apply()

    private fun getDefault(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
}
