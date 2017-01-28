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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;

class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {
    private static final String TAG = OutletAdapter.class.getSimpleName();

    private final ArrayList<Outlet> sortedOutlets;

    private final Comparator<Outlet> CLOSING_TIME_ORDER = new Comparator<Outlet>() {
        @Override
        public int compare(Outlet o1, Outlet o2) {
            // Integer.compare not available pre-1.7 but Double.compareTo has same effect
            return Double.compare(o1.getMinsToClosingTime(), o2.getMinsToClosingTime());
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
    private Comparator<Outlet> mSortOrder = CLOSING_TIME_ORDER;

    private final OnOutletClickListener clickListener;

    private int selectedOutletPosition = RecyclerView.NO_POSITION;

    // FIXME Do we need to store this state here as well as in OutletFinderActivity?
    // FIXME default should not be Oxford Circus!
    private LatLng mCurrentLatLng = new LatLng(51.515514, -0.141864);

    // TODO get rid of this
    private int mNumUpdates;

    OutletAdapter(List<Outlet> outletList, OnOutletClickListener clickListener) {
        this.sortedOutlets = new ArrayList<Outlet>(outletList);
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

        int threshold = 31;
        String closingInfo = outlet.getClosingTime();
        int minsToClosingTime = outlet.getMinsToClosingTime();
        if (minsToClosingTime < threshold)
            closingInfo = minsToClosingTime + "min";
        outletViewHolder.text3.setText(closingInfo);

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
            mSortOrder = CLOSING_TIME_ORDER;
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

    void filterOutlets() {
        //Log.d(TAG, "filterOutlets called");
        // TODO make this non-destructive; what happens at midnight?
        // TODO optimise using notifyItemChanged/notifyItemRangeRemoved (not all change!)
        for (int i = sortedOutlets.size() - 1; i >= 0; i--) {
            Outlet outlet = sortedOutlets.get(i);
            if (outlet.getMinsToClosingTime() <= 0)
                sortedOutlets.remove(i);
        }

        notifyDataSetChanged();
    }

    private void sortOutlets() {
        filterOutlets();

        // TODO optimise? also diff changes to allow for more efficient UI updates?
        Collections.sort(sortedOutlets, mSortOrder);
        notifyDataSetChanged();
    }

    class OutletViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView text1;
        private final TextView text2;
        private final TextView text3;

        private OutletViewHolder(View itemView) {
            super(itemView);

            text1 = (TextView) itemView.findViewById(R.id.text1);
            text2 = (TextView) itemView.findViewById(R.id.text2);
            text3 = (TextView) itemView.findViewById(R.id.text3);

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