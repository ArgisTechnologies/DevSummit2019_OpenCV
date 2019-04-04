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

import com.google.gson.Gson;
import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

import org.json.JSONObject;

public class ConfirmMedInfoFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private Button btnEdit, btnOK;
    private TextView tvMedInfo, tvTitle;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private boolean isFromAddNewUser;

    public ConfirmMedInfoFragment() {
        // Required empty public constructor
    }

    public static ConfirmMedInfoFragment newInstance() {
        return new ConfirmMedInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_verify_med_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //handle bundle
            user = (User) bundle.getSerializable("user");
            if (user != null) {
                tvTitle.setText("NEW MED FOR " + user.getPatientname());
            }
            medication = (Medication) bundle.getSerializable("medication");
            String sb = null;
            if (medication != null) {
                sb = "MED NAME: " + medication.getName() + " " + medication.getStrength_measurement() + "\n"
                        + "MED NICKNAME: " + medication.getNickname() + "\n"
                        + "QUANTITY: " + medication.getMedication_quantity() + "\n"
                        + "DIRECTIONS FOR USE: " + medication.getDosage_instructions() + "\n"
                        + "USE-BY: " + medication.getUse_by_date();
            }
            tvMedInfo.setText(sb);
            if (bundle.containsKey("isFromAddNewUser")) {
                isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnOK = view.findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        btnEdit = view.findViewById(R.id.button_edit);
        btnEdit.setOnClickListener(this);
        tvMedInfo = view.findViewById(R.id.textview_verify_med_info);
        tvTitle = view.findViewById(R.id.textview_title);
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
        if (v == btnOK) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "SelectMedScheduleFragment");
        }
        if (v == btnEdit) {
            //TODO since popbackstack doesn't retain the bundle, we may have to temp save medication/user
            Gson gson = new Gson();
            String medJSON = gson.toJson(medication);
            String userJSON = gson.toJson(user);

            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("removeAllFragmentsUpToCurrent", "PatientNameFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}