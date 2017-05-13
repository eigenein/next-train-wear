package me.eigenein.nexttrainwear;

import android.location.Location;

public class Utils {

    final public static Station DEFAULT_STATION = StationCatalogue.STATION_BY_CODE.get("ASD");

    public static Station getNearestStation(final Location location) {
        Station nearestStation = DEFAULT_STATION;
        float bestDistance = Float.MAX_VALUE;

        for (final Station station : StationCatalogue.STATIONS) {
            final float[] distance = new float[1];
            Location.distanceBetween(
                location.getLatitude(), location.getLongitude(),
                station.latitude, station.longitude,
                distance
            );
            if (distance[0] < bestDistance) {
                bestDistance = distance[0];
                nearestStation = station;
            }
        }

        return nearestStation;
    }
}
