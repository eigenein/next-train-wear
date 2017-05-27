package me.eigenein.nexttrainwear

import android.content.Context
import android.preference.PreferenceManager

import java.util.HashSet

object Preferences {

    private const val KEY_FAVORITE_STATIONS = "stations"
    private val EMPTY_SET = HashSet<String>()

    /**
     * Get codes of favorite stations.
     */
    fun getStations(context: Context): Set<String> {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getStringSet(KEY_FAVORITE_STATIONS, EMPTY_SET)
    }

    /**
     * Set codes of favorite stations.
     */
    fun setStations(context: Context, stations: Set<String>) {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putStringSet(KEY_FAVORITE_STATIONS, stations)
            .apply()
    }
}
