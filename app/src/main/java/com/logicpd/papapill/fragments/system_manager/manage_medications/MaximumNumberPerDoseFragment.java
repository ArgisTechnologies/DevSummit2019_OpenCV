package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class MaximumNumberPerDoseFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MaximumNumberPerDoseFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private TextView tvMedication;
    private Button btnNext;
    private ImageView btnIncrease, btnDecrease;
    private User user;
    private Medication medication;
    private EditText etMaxNumberPerDose;
    private int currentValue = 0, minvalue = 0, maxValue = 24;//TODO do we need to change this?

    public MaximumNumberPerDoseFragment() {
        // Required empty public constructor
    }

    public static MaximumNumberPerDoseFragment newInstance() {
        return new MaximumNumberPerDoseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_max_number_per_dose, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            tvMedication.setText(medication.getName() + " " + medication.getStrength_value() + " " + medication.getStrength_measurement());
        }
        etMaxNumberPerDose.setText(String.valueOf(minvalue));

    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMedication = view.findViewById(R.id.textview_medication_dosage);
        etMaxNumberPerDose = view.findViewById(R.id.edittext_max_number_per_dose);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        btnDecrease = view.findViewById(R.id.button_decrease);
        btnDecrease.setOnClickListener(this);
        btnIncrease = view.findViewById(R.id.button_increase);
        btnIncrease.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = null;

        if (context instanceof Activity) {
            a = (Activity) context;
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnButtonClickListener) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (v == backButton) {
            bundle.putString("fragmentName", "Back");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnIncrease) {
            if (Integer.parseInt(etMaxNumberPerDose.getText().toString()) >= 0) {
                currentValue = Integer.parseInt(etMaxNumberPerDose.getText().toString());
                currentValue += 1;
                etMaxNumberPerDose.setText(String.valueOf(currentValue));
            }
        }
        if (v == btnDecrease) {
            if (Integer.parseInt(etMaxNumberPerDose.getText().toString()) > 0) {
                currentValue = Integer.parseInt(etMaxNumberPerDose.getText().toString());
                currentValue -= 1;
                etMaxNumberPerDose.setText(String.valueOf(currentValue));
            }
        }
        if (v == btnNext) {
            if (Integer.parseInt(etMaxNumberPerDose.getText().toString()) > 0) {
                medication.setMax_number_per_dose(Integer.parseInt(etMaxNumberPerDose.getText().toString()));
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "MedicationQuantityMessageFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please enter max number per dose");
            }
        }
    }
}
