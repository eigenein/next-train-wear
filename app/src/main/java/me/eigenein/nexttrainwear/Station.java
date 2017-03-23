package me.eigenein.nexttrainwear;

public class Station {

    public String code;
    public String longName;
    public float latitude;
    public float longitude;

    @Override
    public String toString() {
        return String.format("%s (%s): %s, %s", longName, code, latitude, longitude);
    }
}
