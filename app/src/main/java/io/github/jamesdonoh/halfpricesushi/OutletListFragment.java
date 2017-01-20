package io.github.jamesdonoh.halfpricesushi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletStore;

public class OutletListFragment extends Fragment implements OutletAdapter.OnOutletClickListener {
    private final static String SELECTED_OUTLET_ID = "selectedOutlet";

    private List<Outlet> mOutletList;

    private boolean mDualPane;

    private int mSelectedOutletId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOutletList = OutletStore.getAllOutlets(getContext());

        if (savedInstanceState != null) {
            mSelectedOutletId = savedInstanceState.getInt(SELECTED_OUTLET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        OutletAdapter mOutletAdapter = new OutletAdapter(mOutletList, this);
        recyclerView.setAdapter(mOutletAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane) {
            showOutletDetails(mSelectedOutletId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_OUTLET_ID, mSelectedOutletId);
    }

    @Override
    public void onOutletClick(Outlet outlet) {
        showOutletDetails(outlet.getId());
    }

    private void showOutletDetails(int outletId) {
        mSelectedOutletId = outletId;

        if (mDualPane) {
            OutletDetailsFragment details = (OutletDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);

            if (details == null || details.getShownOutletId() != outletId) {
                details = OutletDetailsFragment.newInstance(outletId);
                getFragmentManager().beginTransaction()
                        .replace(R.id.details, details)
                        .commit();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), OutletDetailsActivity.class);

            // Pass outlet ID argument to detail activity via intent extra
            intent.putExtra(OutletDetailsFragment.OUTLET_ID, outletId);

            startActivity(intent);
        }
    }
}
