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
import com.logicpd.papapill.data.Medication;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class MedicationsCheckAdapter extends RecyclerView.Adapter<MedicationsCheckAdapter.DataObjectHolder> {
    private MedicationsCheckAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private List<Medication> medications;

    public MedicationsCheckAdapter(Context context, List<Medication> resultList) {
        this.medications = resultList;
    }


    @NonNull
    @Override
    public MedicationsCheckAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medication_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationsCheckAdapter.DataObjectHolder holder, int position) {
        //Medication medication = dispenseTimes.get(position);

        holder.tvMedicationName.setText(medications.get(position).getName() + " "
                + medications.get(position).getStrength_value()
                + " " + medications.get(position).getStrength_measurement());

        if (medications.get(position).isPaused()) {
            holder.checkSelected.setChecked(true);
        } else {
            holder.checkSelected.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMedicationName;
        CheckBox checkSelected;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvMedicationName = itemView.findViewById(R.id.textview_medication_name);
            checkSelected = itemView.findViewById(R.id.checkbox_medication_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public void selectAll(boolean b) {
        for (Medication medication : medications) {
            if (b) {
                medication.setPaused(true);
            } else {
                medication.setPaused(false);
            }
        }
        notifyDataSetChanged();
    }

    public void toggleChecked(int position) {
        if (medications.get(position).isPaused()) {
            medications.get(position).setPaused(false);
        } else {
            medications.get(position).setPaused(true);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MedicationsCheckAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public List<Medication> getListFromAdapter() {
        return medications;
    }
}
