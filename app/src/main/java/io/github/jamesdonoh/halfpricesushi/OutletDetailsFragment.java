package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletCache;

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
            mOutlet = OutletCache.getOutletById(getContext(), outletId);
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
            dayNameView.setPadding(0, 0, 40, 0);

            TextView openingTimes = new TextView(getContext());
            String timesStr = mOutlet.getOpeningTimesAsString(day);
            if (timesStr == null)
                timesStr = "(closed)";
            openingTimes.setText(timesStr);

            gridLayout.addView(dayNameView);
            gridLayout.addView(openingTimes);
        }

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser && rating != mOutlet.getRating()) {
                    mOutlet.setRating((int) rating); // NB discard any fractional part
                    OutletCache.storeOutletRating(getContext(), mOutlet);
                }
            }
        });
        if (mOutlet.isRated()) {
            ratingBar.setRating((float) mOutlet.getRating());
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

        // TODO DRY this up with OutletMapFragment?
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);

        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .icon(bitmapDescriptor));
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