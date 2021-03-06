package io.github.jamesdonoh.halfpricesushi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.github.jamesdonoh.halfpricesushi.model.OutletCache;

public class OutletListFragment extends Fragment implements OutletAdapter.OnOutletClickListener {
    private static final String TAG = OutletListFragment.class.getSimpleName();

    private final static String SELECTED_OUTLET_ID = "selectedOutlet";

    private final static int NONE = -1;

    private final static int UPDATE_INTERVAL = 1000;

    private OutletAdapter mOutletAdapter;

    private boolean mDualPane;

    private int mSelectedOutletId = NONE;

    private Handler mUpdateHandler;

    private Runnable mUpdateChecker = new Runnable() {
        @Override
        public void run() {
            try {
                mOutletAdapter.filterOutlets();
            } finally {
                mUpdateHandler.postDelayed(mUpdateChecker, UPDATE_INTERVAL);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(" + (savedInstanceState != null ? "Bundle" : "") + ")");

        // This fragment would like to participate in populating the options menu
        setHasOptionsMenu(true);

        mOutletAdapter = new OutletAdapter(OutletCache.getAllOutlets(getContext()), this);

        if (savedInstanceState != null) {
            mSelectedOutletId = savedInstanceState.getInt(SELECTED_OUTLET_ID);
            Log.d(TAG, "onCreate: restored selected ID = " + mSelectedOutletId);
        }

        mUpdateHandler = new Handler();
    }

    /** Fragment is visible to the user */
    @Override
    public void onStart() {
        super.onStart();

        mUpdateChecker.run();
    }

    /** Fragment is no longer started (i.e. visible to the user) */
    @Override
    public void onStop() {
        super.onStop();

        mUpdateHandler.removeCallbacks(mUpdateChecker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(..., " + (savedInstanceState != null ? "Bundle" : "") + ")");
        View view = inflater.inflate(R.layout.fragment_outlet_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Optimisation: RecyclerView size does not depend on adapter contents
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mOutletAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated(" + (savedInstanceState != null ? "Bundle" : "") + ")");

        // As parent activity has been created it is now safe to determine if in dual-pane mode
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        Log.d(TAG, "mDualPane = " + mDualPane);

        if (mDualPane && mSelectedOutletId != NONE) {
            showOutletDetails(mSelectedOutletId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: persisting selected ID = " + mSelectedOutletId);
        outState.putInt(SELECTED_OUTLET_ID, mSelectedOutletId);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_outlet_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_sort:
                // User chose the toggle sort button
                Toast.makeText(getContext(), R.string.sort_toggle, Toast.LENGTH_SHORT).show();
                mOutletAdapter.toggleSortOrder();
                return true;

            default:
                // Action not recognized so pass to superclass
                return super.onOptionsItemSelected(item);
        }
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
            Log.d(TAG, "showOutletDetails: details = " + details);

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
            intent.putExtra(OutletDetailsFragment.ARG_OUTLET_ID, outletId);

            startActivity(intent);
        }
    }

    void onLocationChanged(Location location) {
        mOutletAdapter.onLocationChanged(location);
    }
}
