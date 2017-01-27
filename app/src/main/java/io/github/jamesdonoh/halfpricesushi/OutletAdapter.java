package io.github.jamesdonoh.halfpricesushi;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;

class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {
    private static final String TAG = OutletAdapter.class.getSimpleName();

    private final List<Outlet> sortedOutlets;

    private final Comparator<Outlet> NAME_ORDER = new Comparator<Outlet>() {
        @Override
        public int compare(Outlet o1, Outlet o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private final Comparator<Outlet> DISTANCE_ORDER = new Comparator<Outlet>() {
        @Override
        public int compare(Outlet o1, Outlet o2) {
            double o1m = getMetresTo(o1);
            double o2m = getMetresTo(o2);

            return o1m > o2m ? 1 : o1m < o2m ? -1 : 0;
        }
    };

    // TODO: move the sorting stuff somewhere else?
    private Comparator<Outlet> mSortOrder = NAME_ORDER;

    private final OnOutletClickListener clickListener;

    private int selectedOutletPosition = RecyclerView.NO_POSITION;

    // FIXME Do we need to store this state here as well as in OutletFinderActivity?
    // FIXME default should not be Oxford Circus!
    private LatLng mCurrentLatLng = new LatLng(51.515514, -0.141864);

    // TODO get rid of this
    private int mNumUpdates;

    OutletAdapter(List<Outlet> outletList, OnOutletClickListener clickListener) {
        this.sortedOutlets = outletList;
        sortOutlets();

        // TODO defend against null clickListener
        this.clickListener = clickListener;

        // Optimisation: each item has a unique key (see #getItemId)
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return sortedOutlets.get(position).getId();
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewGroup) {
        // Reuse itemview for efficiency?
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.outlet_list_item, parent, false);

        return new OutletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OutletViewHolder outletViewHolder, int position) {
        Outlet outlet = sortedOutlets.get(position);
        outletViewHolder.text1.setText(outlet.getName());
        outletViewHolder.text2.setText(getFormattedDistanceToOutlet(outlet));

        /* Now get a 'highlight' effect for free from the android:background on the item layout
           so is it still worth having this?

        boolean isSelected = (position == selectedOutletPosition);
        outletViewHolder.itemView.setSelected(isSelected);
        */
    }

    @Override
    public int getItemCount() {
        return sortedOutlets.size();
    }

    void setSelectedOutletId(int outletId) {
        int outletPosition = getPositionForOutlet(outletId);
        setSelectedOutletPosition(outletPosition);
    }

    void toggleSortOrder() {
        if (mSortOrder == DISTANCE_ORDER) {
            mSortOrder = NAME_ORDER;
        } else {
            mSortOrder = DISTANCE_ORDER;
        }

        sortOutlets();
    }

    void onLocationChanged(Location location) {
        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        Log.d(TAG, "lat/lng updated to " + mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude
                + " (" + ++mNumUpdates + " updates so far)");

        // TODO make this more efficient rather than invalidating everything?
        // TODO only do this if we're actually visible?
        sortOutlets();
    }

    private void sortOutlets() {
        // TODO optimise? also diff changes to allow for more efficient UI updates?
        Collections.sort(sortedOutlets, mSortOrder);
        notifyDataSetChanged();
    }

    class OutletViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView text1;

        private final TextView text2;

        private OutletViewHolder(View itemView) {
            super(itemView);

            text1 = (TextView) itemView.findViewById(R.id.text1);
            text2 = (TextView) itemView.findViewById(R.id.text2);

            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // Ensure item has not been removed
                Outlet outlet = sortedOutlets.get(position);
                clickListener.onOutletClick(outlet.getId());
            }
        }
    }

    interface OnOutletClickListener {
        void onOutletClick(int outletId);
    }

    private void setSelectedOutletPosition(int newPosition) {
        notifyItemChanged(selectedOutletPosition);
        selectedOutletPosition = newPosition;
        notifyItemChanged(selectedOutletPosition);
    }

    // Inefficient; revisit/delete once sorting has been implemented as it will have to change anyway
    private int getPositionForOutlet(int outletId) {
        ListIterator<Outlet> iterator = sortedOutlets.listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Outlet outlet = iterator.next();
            if (outlet.getId() == outletId) {
                return index;
            }
        }

        throw new IllegalArgumentException("Outlet " + outletId + " not found in adapter list");
    }

    private String getFormattedDistanceToOutlet(Outlet outlet) {
        double metres = getMetresTo(outlet);
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String kmStr = df.format(metres / 1000);

        return kmStr + "km";
    }

    private double getMetresTo(Outlet outlet) {
        LatLng to = new LatLng(outlet.getLatitude(), outlet.getLongitude());

        return SphericalUtil.computeDistanceBetween(mCurrentLatLng, to);
    }
}