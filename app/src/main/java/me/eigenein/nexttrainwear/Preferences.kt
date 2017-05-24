package me.eigenein.nexttrainwear

import android.content.Context
import android.preference.PreferenceManager

import java.util.HashSet

object Preferences {

    private val KEY_FAVORITE_STATIONS = "favorite_stations"
    private val EMPTY_SET = HashSet<String>()

    fun getFavoriteStations(context: Context): MutableSet<String> {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getStringSet(KEY_FAVORITE_STATIONS, EMPTY_SET)
    }

    fun setFavoriteStations(context: Context, stations: Set<String>) {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putStringSet(KEY_FAVORITE_STATIONS, stations)
            .apply()
    }
}
