package io.github.jamesdonoh.halfpricesushi.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OutletDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static OutletDatabaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "outlets.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + OutletContract.OutletEntry.TABLE_NAME + " (" +
                    OutletContract.OutletEntry._ID + " INTEGER PRIMARY KEY," +
                    OutletContract.OutletEntry.COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OutletContract.OutletEntry.TABLE_NAME;

    private Context mContext;

    // NB singleton pattern to ensure only a single database connection per app
    public static synchronized OutletDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            // Use application context to avoid memory leaks, see http://bit.ly/6LRzfx
            sInstance = new OutletDatabaseHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    private OutletDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context; // can be deleted once OutletFileLoader is no longer used

        Log.i(TAG, "Instance created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database " + DATABASE_NAME);
        db.execSQL(SQL_CREATE_ENTRIES);

        importJsonData(db);
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
    public List<Outlet> getAllOutlets() {
        Log.i(TAG, "getAllOutlets called");
        List<Outlet> outlets = new ArrayList<>();

        // TODO: Make async? Should not be called from main thread according to https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#getReadableDatabase()
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
            OutletContract.OutletEntry._ID,
            OutletContract.OutletEntry.COLUMN_NAME_NAME
        };

        // TODO: Error handling?
        Cursor cursor = db.query(OutletContract.OutletEntry.TABLE_NAME, projection, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int outletId = cursor.getInt(cursor.getColumnIndexOrThrow(OutletContract.OutletEntry._ID));
            String outletName = cursor.getString(cursor.getColumnIndexOrThrow(OutletContract.OutletEntry.COLUMN_NAME_NAME));
            Outlet outlet = new Outlet(outletId, outletName, "never");
            outlets.add(outlet);
        }
        cursor.close();

        return outlets;
    }

    private void importJsonData(SQLiteDatabase db) {
        Log.i(TAG, "Importing JSON data");

        for (Outlet outlet : OutletFileLoader.getOutlets(mContext)) {
            insertOutlet(db, outlet);
        }
    }

    private void insertOutlet(SQLiteDatabase db, Outlet outlet) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(OutletContract.OutletEntry._ID, outlet.getId());
            values.put(OutletContract.OutletEntry.COLUMN_NAME_NAME, outlet.getName());

            db.insertOrThrow(OutletContract.OutletEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
