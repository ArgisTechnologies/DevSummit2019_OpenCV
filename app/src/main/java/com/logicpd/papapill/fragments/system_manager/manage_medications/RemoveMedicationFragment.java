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
 * Blank fragment template
 *
 * @author alankilloren
 */
public class RemoveMedicationFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "RemoveMedicationFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnHelp, btnDeveloper;
    private User user;
    private Medication medication;
    private DatabaseHelper db;

    public RemoveMedicationFragment() {
        // Required empty public constructor
    }

    public static RemoveMedicationFragment newInstance() {
        return new RemoveMedicationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_remove_medication, container, false);
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

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnHelp = view.findViewById(R.id.button_help);
        btnHelp.setOnClickListener(this);
        btnDeveloper = view.findViewById(R.id.button_developer);
        btnDeveloper.setOnClickListener(this);
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
        if (v == btnHelp) {
            //TODO display help for remove bin
        }

        //TODO this fragment needs to be able to detect the door close so it can advance to next fragment
        if (v == btnDeveloper) {
            int returnVal = db.removeMedication(medication);
            if (returnVal > 0) {
                // remove medication from schedule
                returnVal = db.removeMedicationFromSchedule(user, medication);
                if (returnVal > 0) {
                    bundle.putSerializable("user", user);
                    bundle.putSerializable("medication", medication);
                    bundle.putString("fragmentName", "MedicationRemovedFragment");
                    bundle.putBoolean("updateSchedule", true);
                    mListener.onButtonClicked(bundle);
                } else {
                    TextUtils.showToast(getActivity(), "Problem removing medication from schedule");
                }
            } else {
                TextUtils.showToast(getActivity(), "Problem removing medication");
            }
        }
    }
}