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
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class MedicationTakenEarlyFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationTakenEarlyFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private Button btnDone;
    private TextView tvTitle, tvMedInfo;
//    boolean isEarlyDispense;

    public MedicationTakenEarlyFragment() {
        // Required empty public constructor
    }

    public static MedicationTakenEarlyFragment newInstance() {
        return new MedicationTakenEarlyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_medication_taken_early, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            /*if (bundle.containsKey("isEarlyDispense")) {
                isEarlyDispense = bundle.getBoolean("isEarlyDispense");
            }*/
        }
        /*if (isEarlyDispense) {
            tvTitle.setText("EARLY DISPENSING COMPLETE");
            tvMedInfo.setText("INSERT EXTRA MEDICATION INFO HERE");
            btnGetMedication.setVisibility(View.GONE);
            btnDone.setText("DONE");
        }*/
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        tvMedInfo = view.findViewById(R.id.textview_med_info);
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
        if (v == btnDone) {
            //go to MyMedicationFragment
            bundle.putSerializable("user", user);
            bundle.putString("removeAllFragmentsUpToCurrent", "MyMedicationFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}