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
    private List<Outlet> outletList;

    private boolean mDualPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        outletList = OutletStore.getAllOutlets(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        OutletAdapter outletAdapter = new OutletAdapter(outletList, this);
        recyclerView.setAdapter(outletAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onOutletClick(Outlet outlet) {
        if (mDualPane) {
            OutletDetailsFragment details = (OutletDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);

            if (details == null || details.getShownOutletId() != outlet.getId()) {
                details = OutletDetailsFragment.newInstance(outlet.getId());
                getFragmentManager().beginTransaction()
                        .replace(R.id.details, details)
                        .commit();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), OutletDetailsActivity.class);

            // Pass outlet ID argument to detail activity via intent extra
            intent.putExtra(OutletDetailsFragment.OUTLET_ID, outlet.getId());

            startActivity(intent);
        }
    }
}
