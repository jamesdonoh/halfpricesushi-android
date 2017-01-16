package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;

class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {

    private final List<Outlet> outletList;

    private final OnOutletClickListener clickListener;

    OutletAdapter(List<Outlet> outletList, OnOutletClickListener clickListener) {
        this.outletList = outletList;
        // TODO defend against null clickListener
        this.clickListener = clickListener;
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewGroup) {
        // Reuse itemview for efficiency?
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);

        return new OutletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OutletViewHolder outletViewHolder, int position) {
        Outlet outlet = outletList.get(position);
        outletViewHolder.text1.setText(outlet.getName());
        outletViewHolder.text2.setText(Integer.toString(outlet.getId()));

        outletViewHolder.bindClickListener(outlet, clickListener);
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }

    static class OutletViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1, text2;

        OutletViewHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            text2 = (TextView) itemView.findViewById(android.R.id.text2);
        }

        private void bindClickListener(final Outlet outlet,
                                       final OnOutletClickListener clickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    clickListener.onOutletClick(outlet);
                }
            });
        }
    }

    interface OnOutletClickListener {
        void onOutletClick(Outlet outlet);
    }
}