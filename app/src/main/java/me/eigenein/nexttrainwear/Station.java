package me.eigenein.nexttrainwear;

public class Station {

    public final String code;
    public final String longName;
    public final String land;
    public final float latitude;
    public final float longitude;

    public Station(
        final String code,
        final String longName,
        final String land,
        final float latitude,
        final float longitude
    ) {
        this.code = code;
        this.longName = longName;
        this.land = land;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %s, %s", longName, code, latitude, longitude);
    }
}
