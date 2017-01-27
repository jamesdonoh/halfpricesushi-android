package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletStore;

public class OutletDetailsFragment extends Fragment implements OnMapReadyCallback {
    // See https://developer.android.com/reference/android/content/Intent.html#putExtras%28android.os.Bundle%29
    public final static String ARG_OUTLET_ID = "io.github.jamesdonoh.halfpricesushi.outletId";

    private Outlet mOutlet;

    /**
     * Convenience static constructor for specifying which outlet ID to display.
     */
    static OutletDetailsFragment newInstance(int outletId) {
        OutletDetailsFragment fragment = new OutletDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_OUTLET_ID, outletId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            int outletId = args.getInt(ARG_OUTLET_ID);
            mOutlet = OutletStore.getOutletById(getContext(), outletId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_details, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(mOutlet.getName());

        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.opening_times_grid);

        for (int day = DateTimeConstants.MONDAY; day <= DateTimeConstants.SUNDAY; day++) {
            TextView dayNameView = new TextView(getContext());
            String dayName = new LocalDate().withDayOfWeek(day).dayOfWeek().getAsText();
            dayNameView.setText(dayName);

            TextView openingTimes = new TextView(getContext());
            String timesStr = mOutlet.getOpeningTimesAsString(day);
            if (timesStr == null)
                timesStr = "(closed)";
            openingTimes.setText(timesStr);

            gridLayout.addView(dayNameView);
            gridLayout.addView(openingTimes);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public int getShownOutletId() {
        return mOutlet.getId();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("OutletDetailsFragment", "onMapReady");

        LatLng position = getPosition();
        String title = mOutlet.getName();

        googleMap.addMarker(new MarkerOptions().position(position).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    private LatLng getPosition() {
        return new LatLng(mOutlet.getLatitude(), mOutlet.getLongitude());
    }

    private String getTodayString() {
        DateTime now = new DateTime();

        return now.dayOfWeek().getAsText();
    }
}