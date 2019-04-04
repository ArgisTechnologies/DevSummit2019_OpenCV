package com.logicpd.papapill.data.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class DispenseTimesAdapter extends RecyclerView.Adapter<DispenseTimesAdapter.DataObjectHolder> {
    private DispenseTimesAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private List<DispenseTime> dispenseTimes;
    private boolean showCheckBox;

    public DispenseTimesAdapter(Context context, List<DispenseTime> resultList, boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
        this.dispenseTimes = resultList;
    }


    @NonNull
    @Override
    public DispenseTimesAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dispense_time_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DispenseTimesAdapter.DataObjectHolder holder, int position) {
        holder.tvDispenseTimeName.setText(dispenseTimes.get(position).getDispenseName() + " "
                + dispenseTimes.get(position).getDispenseTime());
        if (!showCheckBox) {
            holder.checkSelected.setVisibility(View.GONE);
        } else {
            holder.checkSelected.setVisibility(View.VISIBLE);
        }
        if (dispenseTimes.get(position).isActive()) {
            holder.checkSelected.setChecked(true);
        } else {
            holder.checkSelected.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return dispenseTimes.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDispenseTimeName;
        CheckBox checkSelected;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvDispenseTimeName = itemView.findViewById(R.id.textview_dispense_time_name);
            checkSelected = itemView.findViewById(R.id.checkbox_dispense_time_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public void toggleChecked(int position) {
        if (dispenseTimes.get(position).isActive()) {
            dispenseTimes.get(position).setActive(false);
        } else {
            dispenseTimes.get(position).setActive(true);
        }
        notifyDataSetChanged();
    }

    public List<DispenseTime> getListFromAdapter() {
        return dispenseTimes;
    }

    public void setOnItemClickListener(DispenseTimesAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
