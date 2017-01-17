package io.github.jamesdonoh.halfpricesushi.model;

import android.provider.BaseColumns;

/**
 * Contract class to specify database schema.
 *
 * See: https://developer.android.com/training/basics/data-storage/databases.html
 */

public class OutletContract {
    private OutletContract() {}

    public static class OutletEntry implements BaseColumns {
        public static final String TABLE_NAME = "outlet";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LATITUDE= "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
}
