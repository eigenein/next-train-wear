package me.eigenein.nexttrainwear;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class Preferences {

    private static final String KEY_FAVORITE_STATIONS = "favorite_stations";
    private static final Set<String> EMPTY_SET = new HashSet<>();

    public static Set<String> getFavoriteStations(final Context context) {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getStringSet(KEY_FAVORITE_STATIONS, EMPTY_SET);
    }

    public static void setFavoriteStations(final Context context, final Set<String> stations) {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putStringSet(KEY_FAVORITE_STATIONS, stations)
            .apply();
    }
}
