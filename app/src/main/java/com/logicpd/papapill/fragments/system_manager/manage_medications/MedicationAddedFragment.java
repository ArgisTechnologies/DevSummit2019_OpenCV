package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * MedicationAddedFragment
 *
 * @author alankilloren
 */
public class MedicationAddedFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationAddedFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnAddMed, btnDone;
    private User user;
    private Medication medication;
    private TextView tvMedication;
    private boolean isFromSchedule;

    public MedicationAddedFragment() {
        // Required empty public constructor
    }

    public static MedicationAddedFragment newInstance() {
        return new MedicationAddedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_medication_added, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            tvMedication.setText(medication.getName() + " " + medication.getStrength_measurement());
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnAddMed = view.findViewById(R.id.button_add_med);
        btnAddMed.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        tvMedication = view.findViewById(R.id.textview_medication_name);
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
        if (v == btnDone) {
            if (isFromSchedule) {
                // send something back to MainActivity, telling it we entered a new medication and to update the schedule
                bundle.putBoolean("updateSchedule", true);
            }
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnAddMed) {
            bundle.putString("removeAllFragmentsUpToCurrent", "SelectUserForMedsFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}