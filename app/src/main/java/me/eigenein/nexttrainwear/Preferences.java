package me.eigenein.nexttrainwear;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class Preferences {

    private static final String KEY_STATIONS = "stations";
    private static final Set<String> EMPTY_SET = new HashSet<>();

    public static Set<String> getStations(final Context context) {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getStringSet(KEY_STATIONS, EMPTY_SET);
    }

    public static void setStations(final Context context, final Set<String> stations) {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putStringSet(KEY_STATIONS, stations)
            .apply();
    }
}
