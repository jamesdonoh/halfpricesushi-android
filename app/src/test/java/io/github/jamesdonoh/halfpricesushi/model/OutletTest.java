package io.github.jamesdonoh.halfpricesushi.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Local unit tests for Outlet class.
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
        assertEquals(1, outlet.getId());
    }

    @Test
    public void outlet_getName() {
        assertEquals("Oxford Circus", outlet.getName());
    }

    @Test
    public void outlet_getLatitude() {
        assertEquals(51.515419, outlet.getLatitude(), 0);
    }

    @Test
    public void outlet_getLongitude() {
        assertEquals(-0.141099, outlet.getLongitude(), 0);
    }

    @Test
    public void outlet_getRating_WithoutRating() {
        assertEquals(0, outlet.getRating());
    }

    @Test
    public void outlet_getRating_WithRating() {
        outlet.setRating(5);
        assertEquals(5, outlet.getRating());
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

    @Test
    public void outlet_getOpeningTimesAsString_Open() {
        outlet.setOpeningTimes(1, "09:00", "19:30");
        assertEquals("09:00-19:30", outlet.getOpeningTimesAsString(1));
    }

    @Test
    public void outlet_getOpeningTimesAsString_NullWhenNotOpen() {
        assertNull(outlet.getOpeningTimesAsString(1));
    }

    @Test
    public void outlet_getClosingTime_Open() {
        outlet.setOpeningTimes(1, "09:00", "19:30");
        assertEquals("19:30", outlet.getClosingTime(1));
    }

    @Test
    public void outlet_getClosingTime_NullWhenNotOpen() {
        assertNull(outlet.getClosingTime(1), null);
    }

    @Test
    public void outlet_getMinsToClosingTime_Open() {
        outlet.setOpeningTimes(1, "09:00", "19:30");
        DateTime refTime = new DateTime(2017, 1, 30, 19, 0, 1);
        assertEquals(30, outlet.getMinsToClosingTime(refTime));
    }

    @Test
    public void outlet_getMinsToClosingTime_ZeroWhenNotOpen() {
        DateTime refTime = new DateTime(2017, 1, 30, 19, 0);
        assertEquals(0, outlet.getMinsToClosingTime(refTime));
    }
}