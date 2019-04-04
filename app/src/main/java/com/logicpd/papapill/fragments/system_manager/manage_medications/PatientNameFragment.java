package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

import java.util.List;

public class PatientNameFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Patient Name";

    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private AutoCompleteTextView etPatientName;
    private OnButtonClickListener mListener;
    private DatabaseHelper db;
    private User user;

    public PatientNameFragment() {
        // Required empty public constructor
    }

    public static PatientNameFragment newInstance() {
        return new PatientNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_patient_name, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (user != null) {
                etPatientName.setText(user.getPatientname());
            }
        }

        //form a list of all patient names in the system for this user
        List<String> patientNames = db.getPatientNames(user);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, patientNames);
        etPatientName.setThreshold(1);
        etPatientName.setAdapter(arrayAdapter);
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etPatientName = view.findViewById(R.id.edittext_patient_name);
        etPatientName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etPatientName.getText().length() > 1 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });
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
            if (etPatientName.getText().toString().length() > 2) {
                bundle.putString("fragmentName", "MedicationNameFragment");
                user.setPatientname(etPatientName.getText().toString());
                Medication medication = new Medication();
                medication.setPatient_name(etPatientName.getText().toString());
                bundle.putSerializable("medication", medication);
                bundle.putSerializable("user", user);
                int returnVal = db.updateUser(user);//TODO should I update the db now, or wait until med is added?
                mListener.onButtonClicked(bundle);
            }
        }
    }
}
