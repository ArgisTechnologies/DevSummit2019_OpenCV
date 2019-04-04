package com.logicpd.papapill.data.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.ScheduleItem;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class DispenseTimesQuantityAdapter extends RecyclerView.Adapter<DispenseTimesQuantityAdapter.DataObjectHolder> {
    private DispenseTimesQuantityAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private List<DispenseTime> dispenseTimes;
    private List<ScheduleItem> scheduleItems;

    public DispenseTimesQuantityAdapter(Context context, List<DispenseTime> resultList) {
        this.dispenseTimes = resultList;
    }


    @NonNull
    @Override
    public DispenseTimesQuantityAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dispense_time_quantity_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DispenseTimesQuantityAdapter.DataObjectHolder holder, int position) {

        holder.tvDispenseTimeName.setText(dispenseTimes.get(position).getDispenseName() + "\n"
                + dispenseTimes.get(position).getDispenseTime());
        holder.etQuantity.setText("" + dispenseTimes.get(position).getDispenseAmount());
        holder.currentQty = dispenseTimes.get(position).getDispenseAmount();
        holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.currentQty > 0) {
                    holder.currentQty -= 1;
                    holder.etQuantity.setText("" + holder.currentQty);
                    dispenseTimes.get(holder.getAdapterPosition()).setDispenseAmount(holder.currentQty);
                }
            }
        });
        holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.currentQty += 1;
                holder.etQuantity.setText("" + holder.currentQty);
                dispenseTimes.get(holder.getAdapterPosition()).setDispenseAmount(holder.currentQty);
            }
        });

    }

    public void setDispenseAmount(int index, int amount) {
        dispenseTimes.get(index).setDispenseAmount(amount);

    }

    @Override
    public int getItemCount() {
        return dispenseTimes.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDispenseTimeName;
        EditText etQuantity;
        Button btnDecrease, btnIncrease;
        int currentQty = 0;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvDispenseTimeName = itemView.findViewById(R.id.textview_dispense_time_name);
            btnDecrease = itemView.findViewById(R.id.button_decrease);
            btnIncrease = itemView.findViewById(R.id.button_increase);
            etQuantity = itemView.findViewById(R.id.edittext_quantity);
            etQuantity.setEnabled(false);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();*/
            /*if (v == btnDecrease) {
                if (currentQty > 0) {
                    currentQty -= 1;
                    etQuantity.setText("" + currentQty);
                }
            }
            if (v == btnIncrease) {
                currentQty += 1;
                etQuantity.setText("" + currentQty);
            }*/
        }
    }

    public List<DispenseTime> getListFromAdapter() {
        return dispenseTimes;
    }

    public void setOnItemClickListener(DispenseTimesQuantityAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
