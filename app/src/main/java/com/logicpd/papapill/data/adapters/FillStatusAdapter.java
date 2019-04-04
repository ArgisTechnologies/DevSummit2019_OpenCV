package com.logicpd.papapill.data.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class FillStatusAdapter extends RecyclerView.Adapter<FillStatusAdapter.DataObjectHolder> {
    private FillStatusAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private List<Medication> medications;

    public FillStatusAdapter(Context context, List<Medication> resultList) {
        this.medications = resultList;
    }


    @NonNull
    @Override
    public FillStatusAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fill_status_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FillStatusAdapter.DataObjectHolder holder, int position) {
        Medication medication = medications.get(position);

        holder.tvLine1.setText("BIN " + medication.getMedication_location() + ": " + medication.getName() + " "
                + medication.getStrength_value()
                + " " + medication.getStrength_measurement());
        int days = medication.getMedication_quantity() / medication.getMax_units_per_day();//TODO
        holder.tvLine2.setText(medication.getMedication_quantity() + " PILLS (" + days + " DAYS) REMAINING ");
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvLine1, tvLine2;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvLine1 = itemView.findViewById(R.id.textview_line1);
            tvLine2 = itemView.findViewById(R.id.textview_line2);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();*/
        }
    }

    public void setOnItemClickListener(FillStatusAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
