package com.logicpd.papapill.data.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logicpd.papapill.R;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class BlankAdapter extends RecyclerView.Adapter<BlankAdapter.DataObjectHolder> {

    public BlankAdapter(Context context, List<?> resultList) {
        //TODO set variables to be used
    }


    @NonNull
    @Override
    public BlankAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlankAdapter.DataObjectHolder holder, int position) {
        //TODO update the view holders here
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        DataObjectHolder(View itemView) {
            super(itemView);
            //TODO define views here
        }

        @Override
        public void onClick(View v) {
            //TODO handle clicks
        }
    }
}
