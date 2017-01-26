package io.github.jamesdonoh.halfpricesushi;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

// TODO improve obvious copy-paste from https://developers.google.com/maps/documentation/android-api/current-places-tutorial

public class OutletFinderActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = OutletFinderActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    private OutletFragmentPagerAdapter mPagerAdapter;

    private boolean mLocationPermissionGranted = false;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    // A request object to store parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_outlet_finder);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new OutletFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Removing location updates");
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
    }

    /**
     * Gets the device's current location when the Google Play services client is
     * successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Google Play services connected!");

        getDeviceLocation();
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location.getLatitude() + "," + location.getLongitude());
        mCurrentLocation = location;
    }

    /**
     * Builds a GoogleApiClient.
     * Uses the addApi() method to request the Google Places API and the Fused Location Provider.
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        /*
         * Sets the desired interval for active location updates. This interval is
         * inexact. You may not receive updates at all if no location sources are available, or
         * you may receive them slower than requested. You may also receive updates faster than
         * requested if other applications are requesting location at a faster interval.
         */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        /*
         * Sets the fastest rate for active location updates. This interval is exact, and your
         * application will never receive updates faster than this value.
         */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Gets the current location of the device and starts the location update notifications.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location serviced granted immediately");
            setLocationPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (isLocationPermissionGranted()) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mCurrentLocation != null) {
                Log.d(TAG, "Last location: " + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
            } else {
                Log.d(TAG, "getLastLocation was null!");
            }

            Log.d(TAG, "Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location services granted after requesting");
            setLocationPermissionGranted();
        } else {
            Log.d(TAG, "Location services not granted even after requesting");
        }
    }

    private void setLocationPermissionGranted() {
        mLocationPermissionGranted = true;

        OutletMapFragment mapFragment = (OutletMapFragment) mPagerAdapter.getRegisteredFragment(1);
        mapFragment.onLocationPermissionGranted();
    }

    private boolean isLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }
}
