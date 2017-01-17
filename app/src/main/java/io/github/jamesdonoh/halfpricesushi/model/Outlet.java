package io.github.jamesdonoh.halfpricesushi.model;

import com.google.android.gms.maps.model.LatLng;

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

    /** Information about the opening times of the outlet */
    public String getOpeningTimes() {
        return openingTimes;
    }

    /** The latitude of the location of the outlet */
    public double getLatitude() {
        return latitude;
    }

    /** The longitude of the location of the outlet */
    public double getLongitude() {
        return longitude;
    }
}
