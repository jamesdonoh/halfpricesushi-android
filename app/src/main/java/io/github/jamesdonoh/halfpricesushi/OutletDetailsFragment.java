package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletStore;

public class OutletDetailsFragment extends Fragment {
    // See https://developer.android.com/reference/android/content/Intent.html#putExtras%28android.os.Bundle%29
    public final static String OUTLET_ID = "io.github.jamesdonoh.halfpricesushi.outletId";

    private Outlet mOutlet;

    /**
     * Convenience static constructor for specifying which outlet ID to display.
     */
    static OutletDetailsFragment newInstance(int outletId) {
        OutletDetailsFragment fragment = new OutletDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(OUTLET_ID, outletId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            int outletId = args.getInt(OUTLET_ID);
            mOutlet = OutletStore.getOutletById(getContext(), outletId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_details, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(mOutlet.getName());

        /*
        TextView openingTimes = (TextView) view.findViewById(R.id.opening_times);
        openingTimes.setText(mOutlet.getOpeningTimes());

        TextView latitude = (TextView) view.findViewById(R.id.latitude);
        latitude.setText(Double.toString(mOutlet.getLatitude()));

        TextView longitude = (TextView) view.findViewById(R.id.longitude);
        longitude.setText(Double.toString(mOutlet.getLongitude()));
        */

        return view;
    }

    public int getShownOutletId() {
        return mOutlet.getId();
    }
}