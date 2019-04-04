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

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.utils.TextUtils;

/**
 * MedicationFitInstructionsFragment
 *
 * @author alankilloren
 */
public class MedicationFitInstructionsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationFitInstructionsFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private boolean isFromRefill, isFromSchedule;
    private Button btnNext;
    private DatabaseHelper db;

    public MedicationFitInstructionsFragment() {
        // Required empty public constructor
    }

    public static MedicationFitInstructionsFragment newInstance() {
        return new MedicationFitInstructionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_medication_fit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (bundle.containsKey("isFromRefill")) {
                isFromRefill = bundle.getBoolean("isFromRefill");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
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
            int returnVal;
            if (isFromRefill || isFromSchedule) {
                returnVal = db.updateMedication(medication);
            } else {
                returnVal = db.addMedication(medication);
            }

            if (returnVal > 0) {
                if (isFromRefill) {
                    bundle.putBoolean("isFromRefill", isFromRefill);
                    bundle.putBoolean("isFromSchedule", isFromSchedule);
                    bundle.putSerializable("user", user);
                    bundle.putSerializable("medication", medication);
                    bundle.putString("fragmentName", "MedicationRefilledFragment");
                    mListener.onButtonClicked(bundle);
                } else {
                    bundle.putSerializable("user", user);
                    bundle.putSerializable("medication", medication);
                    bundle.putBoolean("isFromSchedule", isFromSchedule);
                    bundle.putString("fragmentName", "MedicationAddedFragment");
                    mListener.onButtonClicked(bundle);
                }

            } else {
                //TODO do we need an actual screen for this instead in case something goes wrong?
                TextUtils.showToast(getActivity(), "Problem saving medication");
            }
        }
    }
}