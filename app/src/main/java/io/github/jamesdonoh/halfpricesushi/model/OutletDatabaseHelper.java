package io.github.jamesdonoh.halfpricesushi.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

class OutletDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // TODO address linter with warning (see also below)
    private static OutletDatabaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "outlets.db";

    private static class OutletEntry implements BaseColumns {
        static final String TABLE_NAME = "outlet";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_LATITUDE= "latitude";
        static final String COLUMN_NAME_LONGITUDE = "longitude";
        static final String COLUMN_NAME_TIMES_MON = "times_mon";
        static final String COLUMN_NAME_TIMES_TUE = "times_tue";
        static final String COLUMN_NAME_TIMES_WED = "times_wed";
        static final String COLUMN_NAME_TIMES_THU = "times_thu";
        static final String COLUMN_NAME_TIMES_FRI = "times_fri";
        static final String COLUMN_NAME_TIMES_SAT = "times_sat";
        static final String COLUMN_NAME_TIMES_SUN = "times_sun";
    }

    private static class RatingEntry implements BaseColumns {
        static final String TABLE_NAME = "rating";
        static final String COLUMN_NAME_RATING = "rating";
    }

    private static final String SQL_CREATE_TABLE_OUTLET =
            "CREATE TABLE " + OutletEntry.TABLE_NAME + " (" +
                    OutletEntry._ID + " INTEGER PRIMARY KEY, " +
                    OutletEntry.COLUMN_NAME_NAME + " TEXT, " +
                    OutletEntry.COLUMN_NAME_LONGITUDE + " REAL, " +
                    OutletEntry.COLUMN_NAME_LATITUDE + " REAL, " +
                    OutletEntry.COLUMN_NAME_TIMES_MON + " TEXT, " +
                    OutletEntry.COLUMN_NAME_TIMES_TUE + " TEXT, " +
                    OutletEntry.COLUMN_NAME_TIMES_WED + " TEXT, " +
                    OutletEntry.COLUMN_NAME_TIMES_THU + " TEXT, " +
                    OutletEntry.COLUMN_NAME_TIMES_FRI + " TEXT, " +
                    OutletEntry.COLUMN_NAME_TIMES_SAT + " TEXT, " +
                    OutletEntry.COLUMN_NAME_TIMES_SUN + " TEXT)";

    private static final String SQL_CREATE_TABLE_RATING =
            "CREATE TABLE " + RatingEntry.TABLE_NAME + " (" +
                    RatingEntry._ID + " INTEGER PRIMARY KEY, " +
                    RatingEntry.COLUMN_NAME_RATING + " INTEGER)";

    private static final String SQL_DROP_TABLE_OUTLET =
            "DROP TABLE IF EXISTS " + OutletEntry.TABLE_NAME;

    // NB singleton pattern to ensure only a single database connection per app
    static synchronized OutletDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            // Use application context to avoid memory leaks, see http://bit.ly/6LRzfx
            sInstance = new OutletDatabaseHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    private OutletDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.i(TAG, "Instance created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database " + DATABASE_NAME);
        db.execSQL(SQL_CREATE_TABLE_OUTLET);
        db.execSQL(SQL_CREATE_TABLE_RATING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database " + DATABASE_NAME + " to v" + newVersion);

        // The outlet table is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DROP_TABLE_OUTLET);
        db.execSQL(SQL_CREATE_TABLE_OUTLET);

        // However, ratings data should persist across database upgrades
    }

    // TODO: Should this live in a separate class (to hide SQLLiteOpenHelper interface)?
    List<Outlet> getAllOutlets() {
        Log.i(TAG, "getAllOutlets called");
        List<Outlet> outlets = new ArrayList<>();

        // TODO: Make async? See https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#getReadableDatabase()
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " + OutletEntry.TABLE_NAME + ".*, " + RatingEntry.COLUMN_NAME_RATING +
                " FROM " + OutletEntry.TABLE_NAME +
                " LEFT JOIN " + RatingEntry.TABLE_NAME + " ON " +
                OutletEntry.TABLE_NAME + "." + OutletEntry._ID + " = " +
                RatingEntry.TABLE_NAME + "." + RatingEntry._ID;

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int outletId = cursor.getInt(cursor.getColumnIndexOrThrow(OutletEntry._ID));
            String outletName = cursor.getString(cursor.getColumnIndexOrThrow(OutletEntry.COLUMN_NAME_NAME));

            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(OutletEntry.COLUMN_NAME_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(OutletEntry.COLUMN_NAME_LONGITUDE));

            Outlet outlet = new Outlet(outletId, outletName, latitude, longitude);

            String[] columnNames = new String[] {
                    OutletEntry.COLUMN_NAME_TIMES_MON,
                    OutletEntry.COLUMN_NAME_TIMES_TUE,
                    OutletEntry.COLUMN_NAME_TIMES_WED,
                    OutletEntry.COLUMN_NAME_TIMES_THU,
                    OutletEntry.COLUMN_NAME_TIMES_FRI,
                    OutletEntry.COLUMN_NAME_TIMES_SAT,
                    OutletEntry.COLUMN_NAME_TIMES_SUN
            };
            for (int day = 1; day <= 7; day++) {
                String times = cursor.getString(cursor.getColumnIndexOrThrow(columnNames[day - 1]));
                if (times != null) {
                    String[] parts = times.split("-");
                    outlet.setOpeningTimes(day, parts[0], parts[1]);
                }
            }

            int rating = cursor.getInt(cursor.getColumnIndexOrThrow(RatingEntry.COLUMN_NAME_RATING));
            outlet.setRating(rating);

            outlets.add(outlet);
        }
        cursor.close();

        return outlets;
    }

    boolean hasOutletData() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), OutletEntry.TABLE_NAME) > 0;
    }

    void updateOutletData(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0)
            return;

        SQLiteDatabase db = getWritableDatabase();
        deleteAllOutletData(db);

        for (Outlet outlet : OutletJsonLoader.fromJsonArray(jsonArray)) {
            insertOutlet(db, outlet);
        }
    }

    void replaceRating(Outlet outlet) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction(); // Note: uses EXCLUSIVE mode
        try {
            ContentValues values = new ContentValues();
            values.put(RatingEntry._ID, outlet.getId());
            values.put(RatingEntry.COLUMN_NAME_RATING, outlet.getRating());

            db.replaceOrThrow(RatingEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void deleteAllOutletData(SQLiteDatabase db) {
        db.execSQL("DELETE FROM "+ OutletEntry.TABLE_NAME);
    }

    private void insertOutlet(SQLiteDatabase db, Outlet outlet) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(OutletEntry._ID, outlet.getId());
            values.put(OutletEntry.COLUMN_NAME_NAME, outlet.getName());
            values.put(OutletEntry.COLUMN_NAME_LATITUDE, outlet.getLatitude());
            values.put(OutletEntry.COLUMN_NAME_LONGITUDE, outlet.getLongitude());

            values.put(OutletEntry.COLUMN_NAME_TIMES_MON, outlet.getOpeningTimesAsString(1));
            values.put(OutletEntry.COLUMN_NAME_TIMES_TUE, outlet.getOpeningTimesAsString(2));
            values.put(OutletEntry.COLUMN_NAME_TIMES_WED, outlet.getOpeningTimesAsString(3));
            values.put(OutletEntry.COLUMN_NAME_TIMES_THU, outlet.getOpeningTimesAsString(4));
            values.put(OutletEntry.COLUMN_NAME_TIMES_FRI, outlet.getOpeningTimesAsString(5));
            values.put(OutletEntry.COLUMN_NAME_TIMES_SAT, outlet.getOpeningTimesAsString(6));
            values.put(OutletEntry.COLUMN_NAME_TIMES_SUN, outlet.getOpeningTimesAsString(7));

            db.insertOrThrow(OutletEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
