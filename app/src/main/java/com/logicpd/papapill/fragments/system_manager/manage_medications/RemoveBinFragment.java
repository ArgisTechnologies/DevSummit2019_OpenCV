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
 * RemoveBinFragment
 *
 * @author alankilloren
 */
public class RemoveBinFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "RemoveBinFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnHelp, btnDeveloper, btnCancel;
    private User user;
    private Medication medication;
    private boolean isRemoveMedication, isFromRefill, isFromSchedule;
    private TextView tvRemoveBin;

    public RemoveBinFragment() {
        // Required empty public constructor
    }

    public static RemoveBinFragment newInstance() {
        return new RemoveBinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_remove_bin, container, false);
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
                tvRemoveBin.setText("1. SLIDE DEVICE DOOR OPEN AND REMOVE BIN #" + medication.getMedication_location());
            }
            if (bundle.containsKey("isRemoveMedication")) {
                isRemoveMedication = bundle.getBoolean("isRemoveMedication");
            }
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
        btnHelp = view.findViewById(R.id.button_help);
        btnHelp.setOnClickListener(this);
        btnDeveloper = view.findViewById(R.id.button_developer);
        btnDeveloper.setOnClickListener(this);
        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);
        tvRemoveBin = view.findViewById(R.id.textview_remove_bin);
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
        if (v == btnHelp) {
            // display help/video for remove bin
            bundle.putString("fragmentName", "RemoveBinHelpFragment");
            mListener.onButtonClicked(bundle);
        }

        //TODO this fragment needs to be able to detect the door open so it can advance to next fragment
        if (v == btnDeveloper) {
            if (isFromRefill) {
                bundle.putBoolean("isFromRefill", isFromRefill);
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "LoadMedicationFragment");
                mListener.onButtonClicked(bundle);
            } else if (isRemoveMedication) {
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                bundle.putString("fragmentName", "RemoveMedicationFragment");
                mListener.onButtonClicked(bundle);
            } else {
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                bundle.putString("fragmentName", "LoadMedicationFragment");
                mListener.onButtonClicked(bundle);
            }
        }
    }
}