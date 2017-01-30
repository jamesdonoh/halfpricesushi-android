package io.github.jamesdonoh.halfpricesushi.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access layer that provides caching and hides database implementation.
 * TODO: rename class, confusing name?
 */
public class OutletCache {
    private static final String TAG = OutletCache.class.getSimpleName();

    private static List<Outlet> outlets = null;

    public static List<Outlet> getAllOutlets(Context context) {
        if (outlets == null) {
            outlets = OutletDatabaseHelper.getInstance(context).getAllOutlets();
        }

        return outlets;
    }

    public static Outlet getOutletById(Context context, int outletId) {
        Log.d(TAG, "getOutletById(" + outletId + ")");
        // TODO replace with more efficient implementation
        for (Outlet outlet : getAllOutlets(context)) {
            if (outlet.getId() == outletId) {
                return outlet;
            }
        }

        throw new IllegalArgumentException("Unknown outlet ID: " + outletId);
    }

    public static boolean hasOutletData(Context context) {
        return OutletDatabaseHelper.getInstance(context).hasOutletData();
    }

    public static void updateOutletData(Context context, JSONArray jsonArray) {
        OutletDatabaseHelper.getInstance(context).updateOutletData(jsonArray);
    }

    public static void storeOutletRating(Context context, Outlet outlet) {
        // Store rating in local database
        OutletDatabaseHelper.getInstance(context).replaceRating(outlet);

        // Send rating to API
        //OutletApi.getInstance(context).sendRating(outlet);
    }
}
