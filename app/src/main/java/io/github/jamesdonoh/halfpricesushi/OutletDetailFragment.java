package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OutletDetailFragment extends Fragment {
    private static final String ARG_OUTLET_ID = "outlet_id";

    private static final int DEFAULT_OUTLET_ID = 0;

    int mOutletId = DEFAULT_OUTLET_ID;

    /**
     * Convenience static constructor for specifying which outlet ID to display.
     */
    static OutletDetailFragment newInstance(int outletId) {
        OutletDetailFragment fragment = new OutletDetailFragment();

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
            mOutletId = args.getInt(ARG_OUTLET_ID, DEFAULT_OUTLET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_detail, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(Integer.toString(mOutletId));

        return view;
    }
}