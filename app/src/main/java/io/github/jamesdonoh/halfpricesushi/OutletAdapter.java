package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;

class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {

    private final List<Outlet> outletList;

    private final OnOutletClickListener clickListener;

    private int selectedOutletPosition = RecyclerView.NO_POSITION;

    OutletAdapter(List<Outlet> outletList, OnOutletClickListener clickListener) {
        this.outletList = outletList;
        // TODO defend against null clickListener
        this.clickListener = clickListener;

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return outletList.get(position).getId();
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
        Outlet outlet = outletList.get(position);
        outletViewHolder.text1.setText(outlet.getName());

        boolean isSelected = (position == selectedOutletPosition);
        outletViewHolder.itemView.setSelected(isSelected);

        outletViewHolder.bindClickListener(outlet);
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }

    public void setSelectedOutletId(int outletId) {
        int outletPosition = getPositionForOutlet(outletId);
        setSelectedOutletPosition(outletPosition);
    }

    class OutletViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1;

        private OutletViewHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(R.id.text1);
        }

        private void bindClickListener(final Outlet outlet) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //setSelectedOutletPosition(getLayoutPosition());
                    clickListener.onOutletClick(outlet);
                }
            });
        }
    }

    interface OnOutletClickListener {
        void onOutletClick(Outlet outlet);
    }

    private void setSelectedOutletPosition(int newPosition) {
        notifyItemChanged(selectedOutletPosition);
        selectedOutletPosition = newPosition;
        notifyItemChanged(selectedOutletPosition);
    }

    // Inefficient; revisit/delete once sorting has been implemented as it will have to change anyway
    private int getPositionForOutlet(int outletId) {
        ListIterator<Outlet> iterator = outletList.listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Outlet outlet = iterator.next();
            if (outlet.getId() == outletId) {
                return index;
            }
        }

        throw new IllegalArgumentException("Outlet " + outletId + " not found in adapter list");
    }
}