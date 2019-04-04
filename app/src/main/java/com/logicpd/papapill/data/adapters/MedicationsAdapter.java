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
public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.DataObjectHolder> {
    private MedicationsAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private List<Medication> medications;

    public MedicationsAdapter(Context context, List<Medication> resultList) {
        this.medications = resultList;
    }


    @NonNull
    @Override
    public MedicationsAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medication_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationsAdapter.DataObjectHolder holder, int position) {
        holder.tvMedicationName.setText("BIN " + medications.get(position).getMedication_location() + " - " + medications.get(position).getName() + " "
                + medications.get(position).getStrength_value()
                + " " + medications.get(position).getStrength_measurement());
        holder.checkSelected.setVisibility(View.GONE);
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

    public void setOnItemClickListener(MedicationsAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
