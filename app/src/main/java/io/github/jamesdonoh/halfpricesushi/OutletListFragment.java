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
import io.github.jamesdonoh.halfpricesushi.model.OutletFileLoader;

public class OutletListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_list, container, false);

        // Should this happen in onActivityCreated instead?
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        List<Outlet> outletList = OutletFileLoader.getOutlets(getContext());
        OutletAdapter outletAdapter = new OutletAdapter(outletList, new OutletAdapter.OnOutletClickListener() {
            @Override public void onOutletClick(Outlet outlet) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), OutletDetailActivity.class);

                // Pass outlet ID argument to detail activity via intent extra
                intent.putExtra(OutletDetailFragment.OUTLET_ID, outlet.getId());

                startActivity(intent);
            }
        });
        recyclerView.setAdapter(outletAdapter);

        return view;
    }
}
