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
    public void onMapReady(GoogleMap googleMap) {
        for (Outlet outlet : OutletStore.getAllOutlets(getContext())) {
            // sushi = "\ud83c\udf63";
            LatLng position = new LatLng(outlet.getLatitude(), outlet.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(outlet.getName());
//                    .snippet("Lots of sushi"));
        }

        googleMap.setOnInfoWindowClickListener(this);

        LatLng oxfordCircus = new LatLng(51.515514, -0.141864);
        float zoom = 12;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oxfordCircus, zoom));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO make this open details activity - but what to do in landscape mode?
        Toast.makeText(getContext(), "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }
}
