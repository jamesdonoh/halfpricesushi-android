package io.github.jamesdonoh.halfpricesushi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;
import io.github.jamesdonoh.halfpricesushi.model.OutletStore;

public class OutletListFragment extends Fragment implements OutletAdapter.OnOutletClickListener {
    private final static String SELECTED_OUTLET_ID = "selectedOutlet";

    private final static int NONE = -1;

    private OutletAdapter mOutletAdapter;

    private boolean mDualPane;

    private int mSelectedOutletId = NONE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OutletListFragment", "onCreate(" + (savedInstanceState != null ? "Bundle" : "") + ")");

        if (savedInstanceState != null) {
            mSelectedOutletId = savedInstanceState.getInt(SELECTED_OUTLET_ID);
            Log.d("OutletListFragment", "onCreate: restored selected ID = " + mSelectedOutletId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OutletListFragment", "onCreateView(..., " + (savedInstanceState != null ? "Bundle" : "") + ")");
        View view = inflater.inflate(R.layout.fragment_outlet_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Optimisation: RecyclerView size does not depend on adapter contents
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mOutletAdapter = new OutletAdapter(OutletStore.getAllOutlets(getContext()), this);
        recyclerView.setAdapter(mOutletAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("OutletListFragment", "onActivityCreated(" + (savedInstanceState != null ? "Bundle" : "") + ")");

        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane && mSelectedOutletId != NONE) {
            showOutletDetails(mSelectedOutletId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("OutletListFragment", "onSaveInstanceState: persisting selected ID = " + mSelectedOutletId);
        outState.putInt(SELECTED_OUTLET_ID, mSelectedOutletId);
    }

    @Override
    public void onOutletClick(int outletId) {
        showOutletDetails(outletId);
    }

    private void showOutletDetails(int outletId) {
        mSelectedOutletId = outletId;

        if (mDualPane) {
            OutletDetailsFragment details = (OutletDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            Log.d("OutletListFragment", "showOutletDetails: details = " + details);

            if (details == null || details.getShownOutletId() != outletId) {
                if (details != null) {
                    Log.d("OutletListFragment", "showOutletDetails: shownOutletId = " + details.getShownOutletId());
                }
                details = OutletDetailsFragment.newInstance(outletId);
                getFragmentManager().beginTransaction()
                        .replace(R.id.details, details)
                        .commit();
            }
            mOutletAdapter.setSelectedOutletId(mSelectedOutletId);
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), OutletDetailsActivity.class);

            // Pass outlet ID argument to detail activity via intent extra
            intent.putExtra(OutletDetailsFragment.OUTLET_ID, outletId);

            startActivity(intent);
        }
    }
}
