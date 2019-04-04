package com.logicpd.papapill.data.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logicpd.papapill.R;

import java.util.List;

/**
 * RecycleView list adapter for WiFi access point data
 *
 * @author alankilloren
 */
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.DataObjectHolder> {
    private List<String> resultList;
    private MyClickListener myClickListener;

    public WifiListAdapter(List<String> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public WifiListAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiListAdapter.DataObjectHolder holder, int position) {

        holder.tvWifiId.setText(resultList.get(position));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvWifiId;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvWifiId = itemView.findViewById(R.id.textview_wifi_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
