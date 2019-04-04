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
 * MaximumUnitsFragment
 *
 * @author alankilloren
 */
public class MaximumUnitsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MaximumUnitsFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Button btnNext;
    private Medication medication;
    private ImageView btnIncreaseUnitsPerDose, btnDecreaseUnitsPerDose,
            btnIncreaseUnitsPerDay, btnDecreaseUnitsPerDay,
            btnIncreaseHrsBetweenDoses, btnDecreaseHrsBetweenDoses;
    private EditText etMaxUnitsPerDose, etMaxUnitsPerDay, etHrsBetweenDoses;

    private int currentUnitsPerDose = 0, currentUnitsPerDay = 0, currentHrsBetweenDoses = 0;

    public MaximumUnitsFragment() {
        // Required empty public constructor
    }

    public static MaximumUnitsFragment newInstance() {
        return new MaximumUnitsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_max_units, container, false);
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

        etMaxUnitsPerDose.setText("0");
        etMaxUnitsPerDay.setText("0");
        etHrsBetweenDoses.setText("0");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etMaxUnitsPerDose = view.findViewById(R.id.edittext_max_units_per_dose);
        etMaxUnitsPerDay = view.findViewById(R.id.edittext_max_units_per_day);
        etHrsBetweenDoses = view.findViewById(R.id.edittext_min_hours_between_doses);
        btnIncreaseUnitsPerDose = view.findViewById(R.id.button_increase_units_per_dose);
        btnIncreaseUnitsPerDose.setOnClickListener(this);
        btnDecreaseUnitsPerDose = view.findViewById(R.id.button_decrease_units_per_dose);
        btnDecreaseUnitsPerDose.setOnClickListener(this);

        btnIncreaseUnitsPerDay = view.findViewById(R.id.button_increase_units_per_day);
        btnIncreaseUnitsPerDay.setOnClickListener(this);
        btnDecreaseUnitsPerDay = view.findViewById(R.id.button_decrease_units_per_day);
        btnDecreaseUnitsPerDay.setOnClickListener(this);

        btnIncreaseHrsBetweenDoses = view.findViewById(R.id.button_increase_hours_between_doses);
        btnIncreaseHrsBetweenDoses.setOnClickListener(this);
        btnDecreaseHrsBetweenDoses = view.findViewById(R.id.button_decrease_hours_between_doses);
        btnDecreaseHrsBetweenDoses.setOnClickListener(this);
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
        if (v == btnIncreaseUnitsPerDose) {
            if (Integer.parseInt(etMaxUnitsPerDose.getText().toString()) >= 0) {
                currentUnitsPerDose = Integer.parseInt(etMaxUnitsPerDose.getText().toString());
                currentUnitsPerDose += 1;
                etMaxUnitsPerDose.setText(String.valueOf(currentUnitsPerDose));
            }
        }
        if (v == btnDecreaseUnitsPerDose) {
            if (Integer.parseInt(etMaxUnitsPerDose.getText().toString()) > 0) {
                currentUnitsPerDose = Integer.parseInt(etMaxUnitsPerDose.getText().toString());
                currentUnitsPerDose -= 1;
                etMaxUnitsPerDose.setText(String.valueOf(currentUnitsPerDose));
            }
        }
        if (v == btnIncreaseUnitsPerDay) {
            if (Integer.parseInt(etMaxUnitsPerDay.getText().toString()) >= 0) {
                currentUnitsPerDay = Integer.parseInt(etMaxUnitsPerDay.getText().toString());
                currentUnitsPerDay += 1;
                etMaxUnitsPerDay.setText(String.valueOf(currentUnitsPerDay));
            }
        }
        if (v == btnDecreaseUnitsPerDay) {
            if (Integer.parseInt(etMaxUnitsPerDay.getText().toString()) > 0) {
                currentUnitsPerDay = Integer.parseInt(etMaxUnitsPerDay.getText().toString());
                currentUnitsPerDay -= 1;
                etMaxUnitsPerDay.setText(String.valueOf(currentUnitsPerDay));
            }
        }
        if (v == btnIncreaseHrsBetweenDoses) {
            if (Integer.parseInt(etHrsBetweenDoses.getText().toString()) >= 0) {
                currentHrsBetweenDoses = Integer.parseInt(etHrsBetweenDoses.getText().toString());
                currentHrsBetweenDoses += 1;
                etHrsBetweenDoses.setText(String.valueOf(currentHrsBetweenDoses));
            }
        }
        if (v == btnDecreaseHrsBetweenDoses) {
            if (Integer.parseInt(etHrsBetweenDoses.getText().toString()) > 0) {
                currentHrsBetweenDoses = Integer.parseInt(etHrsBetweenDoses.getText().toString());
                currentHrsBetweenDoses -= 1;
                etHrsBetweenDoses.setText(String.valueOf(currentHrsBetweenDoses));
            }
        }
        if (v == btnNext) {
            if (Integer.parseInt(etMaxUnitsPerDose.getText().toString()) > 0
                    && Integer.parseInt(etMaxUnitsPerDay.getText().toString()) > 0
                    && Integer.parseInt(etHrsBetweenDoses.getText().toString()) > 0) {
                medication.setMax_units_per_day(Integer.parseInt(etMaxUnitsPerDay.getText().toString()));
                medication.setMax_number_per_dose(Integer.parseInt(etMaxUnitsPerDose.getText().toString()));
                medication.setTime_between_doses(Integer.parseInt(etHrsBetweenDoses.getText().toString()));
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "SelectMedLocationFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please enter valid values for all items");
            }
        }
    }
}