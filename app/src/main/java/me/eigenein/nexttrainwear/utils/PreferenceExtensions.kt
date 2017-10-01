package me.eigenein.nexttrainwear.utils

import android.content.Context

private const val STATIONS_KEY = "stations"
private const val LAST_STATION_CODE_KEY = "last_station_code"

private val emptySet = hashSetOf<String>()

fun Context.getStations(): Set<String> = this.getPreferences().getStringSet(STATIONS_KEY, emptySet)
fun Context.setStations(stations: Set<String>) = this.editPreferences { putStringSet(STATIONS_KEY, stations) }

fun Context.getLastStationCode(): String? = this.getPreferences().getString(LAST_STATION_CODE_KEY, null)
fun Context.setLastStationCode(stationCode: String) = this.editPreferences { putString(LAST_STATION_CODE_KEY, stationCode) }
