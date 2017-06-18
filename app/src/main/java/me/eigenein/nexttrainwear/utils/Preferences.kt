package me.eigenein.nexttrainwear.utils

import android.content.Context
import android.preference.PreferenceManager

object Preferences {

    private const val STATIONS_KEY = "stations"
    private const val LAST_STATION_CODE_KEY = "last_station_code"

    private val emptySet = hashSetOf<String>()

    /**
     * Get codes of favorite stations.
     */
    fun getStations(context: android.content.Context): Set<String> =
        me.eigenein.nexttrainwear.utils.Preferences.getDefault(context).getStringSet(me.eigenein.nexttrainwear.utils.Preferences.STATIONS_KEY, me.eigenein.nexttrainwear.utils.Preferences.emptySet)

    /**
     * Set codes of favorite stations.
     */
    fun setStations(context: android.content.Context, stations: Set<String>) =
        me.eigenein.nexttrainwear.utils.Preferences.getDefault(context).edit().putStringSet(me.eigenein.nexttrainwear.utils.Preferences.STATIONS_KEY, stations).apply()

    fun getLastStationCode(context: android.content.Context): String? =
        me.eigenein.nexttrainwear.utils.Preferences.getDefault(context).getString(me.eigenein.nexttrainwear.utils.Preferences.LAST_STATION_CODE_KEY, null)

    fun setLastStationCode(context: android.content.Context, stationCode: String) =
        me.eigenein.nexttrainwear.utils.Preferences.getDefault(context).edit().putString(me.eigenein.nexttrainwear.utils.Preferences.LAST_STATION_CODE_KEY, stationCode).apply()

    private fun getDefault(context: android.content.Context) = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
}
