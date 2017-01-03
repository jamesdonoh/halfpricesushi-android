package io.github.jamesdonoh.halfpricesushi.model;

/**
 * Represents a physical outlet from which goods are sold or distributed,
 * such as a shop or restaurant.
 */
public class Outlet {
    private int id;

    private String name;

    private String address;

    public Outlet(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    /** The unique numeric identifier of the outlet */
    public int getId() {
        return id;
    }

    /** The name of the outlet */
    public String getName() {
        return name;
    }

    /** The physical address of the outlet */
    public String getAddress() {
        return address;
    }
}
