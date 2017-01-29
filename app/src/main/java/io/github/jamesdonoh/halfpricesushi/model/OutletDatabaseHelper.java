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

    // TODO deal with warning (see below)
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

    private static final String SQL_CREATE_ENTRIES =
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

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OutletEntry.TABLE_NAME;

    private Context mContext;

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
        mContext = context; // can be deleted once OutletJsonLoader is no longer used

        Log.i(TAG, "Instance created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database " + DATABASE_NAME);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database " + DATABASE_NAME + " to v" + newVersion);

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // TODO: Should this live in a separate class (to hide SQLLiteOpenHelper interface)?
    List<Outlet> getAllOutlets() {
        Log.i(TAG, "getAllOutlets called");
        List<Outlet> outlets = new ArrayList<>();

        // TODO: Make async? Should not be called from main thread according to https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#getReadableDatabase()
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
            OutletEntry._ID,
            OutletEntry.COLUMN_NAME_NAME,
            OutletEntry.COLUMN_NAME_LONGITUDE,
            OutletEntry.COLUMN_NAME_LATITUDE,
            OutletEntry.COLUMN_NAME_TIMES_MON,
            OutletEntry.COLUMN_NAME_TIMES_TUE,
            OutletEntry.COLUMN_NAME_TIMES_WED,
            OutletEntry.COLUMN_NAME_TIMES_THU,
            OutletEntry.COLUMN_NAME_TIMES_FRI,
            OutletEntry.COLUMN_NAME_TIMES_SAT,
            OutletEntry.COLUMN_NAME_TIMES_SUN
        };

        // TODO: What about error handling?
        Cursor cursor = db.query(OutletEntry.TABLE_NAME, projection, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int outletId = cursor.getInt(cursor.getColumnIndexOrThrow(OutletEntry._ID));
            String outletName = cursor.getString(cursor.getColumnIndexOrThrow(OutletEntry.COLUMN_NAME_NAME));

            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(OutletEntry.COLUMN_NAME_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(OutletEntry.COLUMN_NAME_LONGITUDE));

            Outlet outlet = new Outlet(outletId, outletName, latitude, longitude);

            // FIXME urgh
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

            outlets.add(outlet);
        }
        cursor.close();

        return outlets;
    }

    boolean hasOutletData() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), OutletEntry.TABLE_NAME) > 0;
    }

    void storeOutletData(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();

        for (Outlet outlet : OutletJsonLoader.fromJsonArray(jsonArray)) {
            insertOutlet(db, outlet);
        }
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
