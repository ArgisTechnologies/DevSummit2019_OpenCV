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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class ConfirmRemoveMedicationFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "ConfirmRemoveMedicationFragmente";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private TextView tvMedication, tvFromSched;
    private Button btnCancel, btnRemove;

    public ConfirmRemoveMedicationFragment() {
        // Required empty public constructor
    }

    public static ConfirmRemoveMedicationFragment newInstance() {
        return new ConfirmRemoveMedicationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_confirm_remove_med, container, false);
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
            tvFromSched.setText("FROM " + user.getUsername() + "'S SCHEDULE AND THE DEVICE?");
        }

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMedication = view.findViewById(R.id.textview_medication_name);
        tvFromSched = view.findViewById(R.id.textview_from_device_schedule);
        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);
        btnRemove = view.findViewById(R.id.button_remove);
        btnRemove.setOnClickListener(this);
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
        if (v == btnCancel) {
            backButton.performClick();
        }
        if (v == btnRemove) {
            bundle.putBoolean("isRemoveMedication", true);
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "RemoveBinFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}