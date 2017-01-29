package io.github.jamesdonoh.halfpricesushi.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access layer that provides caching and hides database implementation.
 * TODO: rename class, confusing name?
 */
public class OutletStore {
    private static List<Outlet> outlets = null;

    public static List<Outlet> getAllOutlets(Context context) {
        if (outlets == null) {
            outlets = OutletDatabaseHelper.getInstance(context).getAllOutlets();
        }

        return outlets;
    }

    public static Outlet getOutletById(Context context, int outletId) {
        // TODO replace with more efficient implementation
        for (Outlet outlet : getAllOutlets(context)) {
            if (outlet.getId() == outletId) {
                return outlet;
            }
        }

        throw new IllegalArgumentException("Unknown outlet ID: " + outletId);
    }

    public static int getNumOutlets(Context context) {
        return getAllOutlets(context).size();
    }
}
