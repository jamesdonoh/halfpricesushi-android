package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;

class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {
    private final List<Outlet> sortedOutlets;

    // TODO: move the sorting stuff somewhere else?
    private Comparator<Outlet> mSortOrder = NAME_ORDER;

    private final static Comparator<Outlet> NAME_ORDER = new Comparator<Outlet>() {
        @Override
        public int compare(Outlet o1, Outlet o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private final static Comparator<Outlet> DISTANCE_ORDER = new Comparator<Outlet>() {
        @Override
        public int compare(Outlet o1, Outlet o2) {
            int o1m = getMetresTo(o1);
            int o2m = getMetresTo(o2);

            return o1m > o2m ? 1 : o1m < o2m ? -1 : 0;
        }
    };

    private final OnOutletClickListener clickListener;

    private int selectedOutletPosition = RecyclerView.NO_POSITION;

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

    private static String getFormattedDistanceToOutlet(Outlet outlet) {
        float metres = (float)getMetresTo(outlet);
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String kmStr = df.format(metres / 1000);

        return kmStr + "km";
    }

    private static int getMetresTo(Outlet outlet) {
        return outlet.getName().length() * 139;
    }
}