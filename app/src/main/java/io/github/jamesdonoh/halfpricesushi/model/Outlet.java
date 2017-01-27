package io.github.jamesdonoh.halfpricesushi.model;

import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

import java.util.HashMap;

/**
 * Represents a physical outlet from which goods are sold or distributed,
 * such as a shop or restaurant.
 */
public class Outlet {
    private final int id;

    private final String name;

    private final double latitude, longitude;

    private SparseArray<OpeningTimes> openingTimes = new SparseArray<OpeningTimes>();

    Outlet(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** The unique numeric identifier of the outlet */
    public int getId() {
        return id;
    }

    /** The name of the outlet */
    public String getName() {
        return name;
    }

    /** The latitude of the location of the outlet */
    public double getLatitude() {
        return latitude;
    }

    /** The longitude of the location of the outlet */
    public double getLongitude() {
        return longitude;
    }

    /** String representation about the opening times of the outlet on the specified day */
    public String getOpeningTimesAsString(int dayOfWeek) {
        OpeningTimes times = openingTimes.get(dayOfWeek);
        if (times == null)
            return null;

        LocalTime openingTime = times.opens;
        LocalTime closingTime = times.closes;
        if (openingTime == null || closingTime == null)
            return null;

        return openingTime.toString("HH:mm") + "-" + closingTime.toString("HH:mm");
    }

    void setOpeningTimes(int dayOfWeek, String openingTime, String closingTime) {
        LocalTime parsedOpening = LocalTime.parse(openingTime);
        LocalTime parsedClosing = LocalTime.parse(closingTime);

        openingTimes.put(dayOfWeek, new OpeningTimes(parsedOpening, parsedClosing));
    }

    private LocalTime getOpeningTime(int dayOfWeek) {
        return openingTimes.get(dayOfWeek).opens;
    }

    private LocalTime getClosingTime(int dayOfWeek) {
        return openingTimes.get(dayOfWeek).closes;
    }

    private class OpeningTimes {
        private LocalTime opens;
        private LocalTime closes;

        private OpeningTimes(LocalTime opens, LocalTime closes) {
            this.opens = opens;
            this.closes = closes;
        }
    }
}
