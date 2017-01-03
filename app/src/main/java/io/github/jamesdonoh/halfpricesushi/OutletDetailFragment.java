package io.github.jamesdonoh.halfpricesushi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OutletDetailFragment extends Fragment {
    // See https://developer.android.com/reference/android/content/Intent.html#putExtras%28android.os.Bundle%29
    public final static String OUTLET_ID = "io.github.jamesdonoh.halfpricesushi.outletId";

    int mOutletId;

    /**
     * Convenience static constructor for specifying which outlet ID to display.
     */
    static OutletDetailFragment newInstance(int outletId) {
        OutletDetailFragment fragment = new OutletDetailFragment();

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