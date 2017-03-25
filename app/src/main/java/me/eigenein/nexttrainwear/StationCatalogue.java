package me.eigenein.nexttrainwear;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Station catalogue.
 */
public class StationCatalogue {

    private static final String TAG = StationCatalogue.class.getSimpleName();

    private static StationCatalogue instance;

    public final ArrayList<Station> stations = new ArrayList<>();

    public static StationCatalogue newInstance(final Context context) {
        if (instance == null) {
            instance = new StationCatalogue(context);
        }
        return instance;
    }

    /**
     * Loads built-in station catalogue.
     * http://webservices.ns.nl/ns-api-stations-v2
     */
    private StationCatalogue(final Context context) {
        final XmlResourceParser parser = context.getResources().getXml(R.xml.stations);

        Station station = null;
        while (true) {
            try {
                switch (parser.getEventType()) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case "Station":
                                station = new Station();
                                break;

                            case "Code":
                                assert station != null;
                                station.code = parser.nextText();
                                break;

                            case "Land":
                                assert station != null;
                                station.land = parser.nextText();
                                break;

                            case "Lang":
                                assert station != null;
                                station.longName = parser.nextText();
                                break;

                            case "Lat":
                                assert station != null;
                                station.latitude = Float.parseFloat(parser.nextText());
                                break;

                            case "Lon":
                                assert station != null;
                                station.longitude = Float.parseFloat(parser.nextText());
                                break;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Station")) {
                            assert station != null;
                            if (station.land.equals("NL")) {
                                stations.add(station);
                            }
                            station = null;
                        }
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        Collections.sort(stations, new Comparator<Station>() {
                            @Override
                            public int compare(final Station station1, final Station station2) {
                                return station1.longName.compareTo(station2.longName);
                            }
                        });
                        return;
                }
                parser.next();
            } catch (final XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
