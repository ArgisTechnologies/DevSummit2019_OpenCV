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

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class MedicationScheduleFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationScheduleFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Button btnAsScheduled, btnAsNeeded;
    private Medication medication;

    public MedicationScheduleFragment() {
        // Required empty public constructor
    }

    public static MedicationScheduleFragment newInstance() {
        return new MedicationScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_select_med_sched, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
        }

        //TextUtils.disableButton(btnAsScheduled);//TODO disabling until section is done
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnAsScheduled = view.findViewById(R.id.button_as_scheduled);
        btnAsScheduled.setOnClickListener(this);
        btnAsNeeded = view.findViewById(R.id.button_as_needed);
        btnAsNeeded.setOnClickListener(this);
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
        if (v == btnAsScheduled) {
            medication.setMedication_schedule_type(2);
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromSchedule", true);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "SelectDispensingTimesFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnAsNeeded) {
            medication.setMedication_schedule_type(1);
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "MaximumNumberPerDoseFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
