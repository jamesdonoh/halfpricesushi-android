package io.github.jamesdonoh.halfpricesushi.model;

/**
 * Represents a physical outlet from which goods are sold or distributed,
 * such as a shop or restaurant.
 */
public class Outlet {
    private final int id;

    private final String name;

    private final String openingTimes;

    public Outlet(int id, String name, String openingTimes) {
        this.id = id;
        this.name = name;
        this.openingTimes = openingTimes;
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
}
