package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.utils.TextUtils;

/**
 * MedicationDosageFragment
 *
 * @author alankilloren
 */
public class MedicationDosageFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationDosageFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private Button btnNext;
    private EditText etDosage;

    public MedicationDosageFragment() {
        // Required empty public constructor
    }

    public static MedicationDosageFragment newInstance() {
        return new MedicationDosageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_medication_dosage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (medication != null) {
                etDosage.setText(medication.getDosage_instructions());
            }
        }

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etDosage = view.findViewById(R.id.edittext_medication_dosage);
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
        if (v == btnNext) {
            if (etDosage.getText().toString().length() > 1) {
                medication.setDosage_instructions(etDosage.getText().toString());
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "MedicationQuantityMessageFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please enter dosage instructions");
            }
        }
    }
}