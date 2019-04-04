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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

import java.util.List;

/**
 * Fragment for System Manager...Manage Medications
 *
 * @author alankilloren
 */
public class ManageMedsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Manage Medications";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnNewMed, btnRefill, btnRemoveMed, btnChangeSched, btnEditTimes;

    public ManageMedsFragment() {
        // Required empty public constructor
    }

    public static ManageMedsFragment newInstance() {
        return new ManageMedsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/
        List<Medication> medicationList = db.getMedications();
        if (medicationList.size() == 0) {
            TextUtils.disableButton(btnRemoveMed);
            TextUtils.disableButton(btnRefill);
            TextUtils.disableButton(btnChangeSched);
        }

        //TextUtils.disableButton(btnChangeSched);//TODO disabling until done
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNewMed = view.findViewById(R.id.button_new_med);
        btnNewMed.setOnClickListener(this);
        btnRefill = view.findViewById(R.id.button_refill_bin);
        btnRefill.setOnClickListener(this);
        btnRemoveMed = view.findViewById(R.id.button_remove_med);
        btnRemoveMed.setOnClickListener(this);
        btnChangeSched = view.findViewById(R.id.button_change_med_schedule);
        btnChangeSched.setOnClickListener(this);
        btnEditTimes = view.findViewById(R.id.button_shared_dispense);
        btnEditTimes.setOnClickListener(this);
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
        if (v == btnNewMed) {
            bundle.putString("authFragment", "SelectUserForMedsFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnRefill) {
            bundle.putString("authFragment", "SelectUserForRefillFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnRemoveMed) {
            bundle.putString("authFragment", "SelectUserForRemoveMedFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnChangeSched) {
            bundle.putString("authFragment", "SelectUserForChangeScheduleFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnEditTimes) {
            bundle.putString("authFragment", "SelectUserForEditDispenseTimesFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
