package io.github.jamesdonoh.halfpricesushi.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test for Outlet class.
 *
 * This will execute on the development machine.
 */
public class OutletTest {
    Outlet outlet;

    @Before
    public void initialise() {
        outlet = new Outlet(1, "Oxford Circus", 51.515419, -0.141099);
    }

    @Test
    public void outlet_isRated_WithoutRating() {
        assertFalse(outlet.isRated());
    }

    @Test
    public void outlet_isRated_WithRating() {
        outlet.setRating(1);
        assertTrue(outlet.isRated());
    }
}