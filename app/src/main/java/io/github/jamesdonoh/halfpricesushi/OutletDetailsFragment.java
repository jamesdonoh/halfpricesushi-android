package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OutletDetailsFragment extends Fragment {
    // See https://developer.android.com/reference/android/content/Intent.html#putExtras%28android.os.Bundle%29
    public final static String OUTLET_ID = "io.github.jamesdonoh.halfpricesushi.outletId";

    int mOutletId;
    String mOutletOpeningTimes;

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
            mOutletId = args.getInt(OUTLET_ID);
            mOutletOpeningTimes = "blah";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_details, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(Integer.toString(mOutletId));

        TextView openingTimes = (TextView) view.findViewById(R.id.opening_times);
        openingTimes.setText(mOutletOpeningTimes);

        return view;
    }
}