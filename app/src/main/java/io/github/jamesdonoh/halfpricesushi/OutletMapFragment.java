package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletStore;

public class OutletMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
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

        for (Outlet outlet : OutletStore.getAllOutlets(getContext())) {
            // sushi = "\ud83c\udf63";
            LatLng position = new LatLng(outlet.getLatitude(), outlet.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(outlet.getName()));
        }

        // TODO handle possibility of no location services
        mMap.setMyLocationEnabled(true);

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

    /*
    @Override
    public boolean onMyLocationButtonClick() {
        // TODO this is just a a copy-paste - maybe remove if we don't need custom behaviour
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    */
}
