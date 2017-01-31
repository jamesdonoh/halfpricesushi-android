package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletCache;

public class OutletMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = OutletFinderActivity.class.getSimpleName();

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outlet_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);

        for (Outlet outlet : OutletCache.getAllOutlets(getContext())) {
            LatLng position = new LatLng(outlet.getLatitude(), outlet.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(outlet.getName())
                    .icon(bitmapDescriptor));
        }

        mMap.setOnInfoWindowClickListener(this);

        LatLng oxfordCircus = new LatLng(51.515514, -0.141864);
        float zoom = 12;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oxfordCircus, zoom));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO make this open details activity - but what to do in landscape mode?
        Toast.makeText(getContext(), "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    // TODO handle previously-granted permission being revoked?
    @SuppressWarnings("MissingPermission")
    void onLocationPermissionGranted() {
        Log.d(TAG, "onLocationPermissionGranted: thanks for letting me know activity!");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }
}
