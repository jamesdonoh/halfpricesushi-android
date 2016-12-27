package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.jamesdonoh.halfpricesushi.model.Outlet;

public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {

    private List<Outlet> outletList;

    public class OutletViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public OutletViewHolder(View view) {
            super(view);
            text1 = (TextView) view.findViewById(android.R.id.text1);
            text2 = (TextView) view.findViewById(android.R.id.text2);
        }
    }

    public OutletAdapter(List<Outlet> outletList) {
        this.outletList = outletList;
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewGroup) {
        // Inefficient?
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);

        return new OutletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OutletViewHolder outletViewHolder, int position) {
        Outlet outlet = outletList.get(position);
        outletViewHolder.text1.setText(outlet.getName());
        outletViewHolder.text2.setText(outlet.getAddress());
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }
}