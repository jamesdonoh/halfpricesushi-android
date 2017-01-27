package io.github.jamesdonoh.halfpricesushi.model;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

/**
 * Represents a physical outlet from which goods are sold or distributed,
 * such as a shop or restaurant.
 */
public class Outlet {
    private final int id;

    private final String name;

    private final String openingTimes;

    private final double latitude, longitude;

    public Outlet(int id, String name, String openingTimes, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.openingTimes = openingTimes;
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

    public LocalTime getOpeningTime(int dayOfWeek) {
        String timeStr = "09:00";
        return LocalTime.parse(timeStr);
    }

    public LocalTime getClosingTime(int dayOfWeek) {
        String timeStr = "20:00";
        return LocalTime.parse(timeStr);
    }

    /** String representation about the opening times of the outlet on the specified day */
    public String getOpeningTimesAsString(int dayOfWeek) {
        String openingTime = getOpeningTime(dayOfWeek).toString("HH:mm");
        String closingTime = getClosingTime(dayOfWeek).toString("HH:mm");

        return openingTime + " - " + closingTime;
    }
}
