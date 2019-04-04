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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * SelectMedScheduleFragment
 *
 * @author alankilloren
 */
public class SelectMedScheduleFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Select Schedule";

    private LinearLayout backButton, homeButton;
    private Button btnScheduled, btnAsNeeded, btnBoth;
    private OnButtonClickListener mListener;
    private DatabaseHelper db;
    private User user;
    private Medication medication;

    public SelectMedScheduleFragment() {
        // Required empty public constructor
    }

    public static SelectMedScheduleFragment newInstance() {
        return new SelectMedScheduleFragment();
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
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnAsNeeded = view.findViewById(R.id.button_as_needed);
        btnAsNeeded.setOnClickListener(this);
        btnBoth = view.findViewById(R.id.button_both);
        btnBoth.setOnClickListener(this);
        btnScheduled = view.findViewById(R.id.button_as_scheduled);
        btnScheduled.setOnClickListener(this);

        //TODO disabling these until next PR
        TextUtils.disableButton(btnScheduled);
        TextUtils.disableButton(btnBoth);
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
        if (v == btnAsNeeded) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "MaximumUnitsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
