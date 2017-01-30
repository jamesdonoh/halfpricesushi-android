package io.github.jamesdonoh.halfpricesushi.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example partial local unit test for Outlet class.
 *
 * This will execute on the development machine.
 */
public class OutletTest {
    private Outlet outlet;

    @Before
    public void initialise() {
        outlet = new Outlet(1, "Oxford Circus", 51.515419, -0.141099);
    }

    @Test
    public void outlet_getId() {
        assertEquals(outlet.getId(), 1);
    }

    @Test
    public void outlet_getName() {
        assertEquals(outlet.getName(), "Oxford Circus");
    }

    @Test
    public void outlet_getLatitude() {
        assertEquals(outlet.getLatitude(), 51.515419, 0);
    }

    @Test
    public void outlet_getLongitude() {
        assertEquals(outlet.getLongitude(), -0.141099, 0);
    }

    @Test
    public void outlet_getRating_WithoutRating() {
        assertEquals(outlet.getRating(), 0);
    }

    @Test
    public void outlet_getRating_WithRating() {
        outlet.setRating(5);
        assertEquals(outlet.getRating(), 5);
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