package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class MedicationTimeBetweenDosesFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationTimeBetweenDosesFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Button btnNext;
    private Medication medication;
    private ImageView btnIncrease, btnDecrease;
    private EditText etTimeBetweenDoses;
    private int currentValue = 0, minvalue = 0, maxValue = 24;//TODO do we need to change this?


    public MedicationTimeBetweenDosesFragment() {
        // Required empty public constructor
    }

    public static MedicationTimeBetweenDosesFragment newInstance() {
        return new MedicationTimeBetweenDosesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_time_between_doses, container, false);
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
        etTimeBetweenDoses.setText(String.valueOf(minvalue));
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        etTimeBetweenDoses = view.findViewById(R.id.edittext_time_between_doses);
        btnDecrease = view.findViewById(R.id.button_decrease);
        btnDecrease.setOnClickListener(this);
        btnIncrease = view.findViewById(R.id.button_increase);
        btnIncrease.setOnClickListener(this);
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
        if (v == btnIncrease) {
            if (Integer.parseInt(etTimeBetweenDoses.getText().toString()) >= 0) {
                currentValue = Integer.parseInt(etTimeBetweenDoses.getText().toString());
                currentValue += 1;
                etTimeBetweenDoses.setText(String.valueOf(currentValue));
            }
        }
        if (v == btnDecrease) {
            if (Integer.parseInt(etTimeBetweenDoses.getText().toString()) > 0) {
                currentValue = Integer.parseInt(etTimeBetweenDoses.getText().toString());
                currentValue -= 1;
                etTimeBetweenDoses.setText(String.valueOf(currentValue));
            }
        }
        if (v == btnNext) {
            if (etTimeBetweenDoses.getText().toString().length() > 0 && Integer.parseInt(etTimeBetweenDoses.getText().toString()) > 0) {
                medication.setTime_between_doses(Integer.parseInt(etTimeBetweenDoses.getText().toString()));
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "MaximumUnitsFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please enter a time between doses");
            }
        }
    }
}
