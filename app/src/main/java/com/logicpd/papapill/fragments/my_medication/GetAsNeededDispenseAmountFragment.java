package com.logicpd.papapill.fragments.my_medication;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
public class GetAsNeededDispenseAmountFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "GetAsNeededDispenseAmountFragment";

    private RelativeLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private TextView tvMedication;
    private ImageView btnIncrease, btnDecrease;
    private Button btnNext;
    private EditText etDispenseAmount;

    int currentValue = 0, minvalue = 0, maxValue = 24;//TODO do we need to change this?

    public GetAsNeededDispenseAmountFragment() {
        // Required empty public constructor
    }

    public static GetAsNeededDispenseAmountFragment newInstance() {
        return new GetAsNeededDispenseAmountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_medication_amount, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            tvMedication.setText(medication.getName() + " " + medication.getStrength_value() + " " + medication.getStrength_measurement());
        }
        etDispenseAmount.setText(String.valueOf(minvalue));
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMedication = view.findViewById(R.id.textview_medication_name);
        btnDecrease = view.findViewById(R.id.button_decrease);
        btnDecrease.setOnClickListener(this);
        btnIncrease = view.findViewById(R.id.button_increase);
        btnIncrease.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etDispenseAmount = view.findViewById(R.id.edittext_dispense_amount);
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
            //TODO should this max out to be what's currently on-hand for this medication?
            if (Integer.parseInt(etDispenseAmount.getText().toString()) >= 0
                    && Integer.parseInt(etDispenseAmount.getText().toString()) < medication.getMedication_quantity()) {
                currentValue = Integer.parseInt(etDispenseAmount.getText().toString());
                currentValue += 1;
                etDispenseAmount.setText(String.valueOf(currentValue));
            }
        }
        if (v == btnDecrease) {
            if (Integer.parseInt(etDispenseAmount.getText().toString()) > 0) {
                currentValue = Integer.parseInt(etDispenseAmount.getText().toString());
                currentValue -= 1;
                etDispenseAmount.setText(String.valueOf(currentValue));
            }
        }

        if (v == btnNext) {
            if (Integer.parseInt(etDispenseAmount.getText().toString()) > 0) {
                //if (Integer.parseInt(etDispenseAmount.getText().toString()) > medication.getMax_number_per_dose()) {
                //TextUtils.showToast(getActivity(), "This number exceeds the max number per dose");
                //} else {
                bundle.putInt("dispense_amount", Integer.parseInt(etDispenseAmount.getText().toString()));
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "DispenseMedsFragment");
                mListener.onButtonClicked(bundle);
                //}
            } else {
                TextUtils.showToast(getActivity(), "Please enter a number");
            }
        }
    }
}