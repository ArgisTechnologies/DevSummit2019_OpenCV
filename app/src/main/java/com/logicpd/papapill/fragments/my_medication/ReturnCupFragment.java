package com.logicpd.papapill.fragments.my_medication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class ReturnCupFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "ReturnCupFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Button btnHelp, btnOk;
    private Medication medication;
    private boolean isEarlyDispense, isFromSchedule;

    public ReturnCupFragment() {
        // Required empty public constructor
    }

    public static ReturnCupFragment newInstance() {
        return new ReturnCupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_return_cup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (bundle.containsKey("isEarlyDispense")) {
                isEarlyDispense = bundle.getBoolean("isEarlyDispense");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnHelp = view.findViewById(R.id.button_help);
        btnHelp.setOnClickListener(this);
        btnOk = view.findViewById(R.id.button_ok);
        btnOk.setOnClickListener(this);
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
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnOk) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putBoolean("isEarlyDispense", isEarlyDispense);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            if (isEarlyDispense) {
                bundle.putString("fragmentName", "MedicationTakenEarlyFragment");
            } else if (isFromSchedule) {
                bundle.putString("fragmentName", "ScheduledMedicationTakenFragment");
            } else {
                bundle.putString("fragmentName", "MedicationTakenFragment");
            }
        }
        mListener.onButtonClicked(bundle);
    }
}