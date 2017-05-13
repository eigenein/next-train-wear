package me.eigenein.nexttrainwear;

import android.location.Location;
import android.support.annotation.Nullable;

public class Utils {

    public static <T> T coalesce(final @Nullable T t1, final @Nullable T t2) {
        return t1 != null ? t1 : t2;
    }

    public static float distanceBetween(
        final double latitude1, final double longitude1,
        final double latitude2, final double longitude2
    ) {
        final float[] distance = new float[1];
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, distance);
        return distance[0];
    }

    public static Station getNearestStation(final Location location) {
        Station nearestStation = null;
        float bestDistance = Float.MAX_VALUE;

        for (final Station station : StationCatalogue.STATIONS) {
            final float distance = distanceBetween(
                location.getLatitude(), location.getLongitude(), station.latitude, station.longitude);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearestStation = station;
            }
        }

        return nearestStation;
    }
}
