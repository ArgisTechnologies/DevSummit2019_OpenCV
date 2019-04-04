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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * MedicationQuantityFragment
 *
 * @author alankilloren
 */
public class MedicationQuantityFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationQuantityFragment";

    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private OnButtonClickListener mListener;
    private TextView tvMedication;
    private EditText etQuantity;
    private User user;
    private Medication medication;
    private boolean isFromRefill, isFromSchedule;

    public MedicationQuantityFragment() {
        // Required empty public constructor
    }

    public static MedicationQuantityFragment newInstance() {
        return new MedicationQuantityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_enter_med_quantity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (medication != null) {
                tvMedication.setText(medication.getName() + " " + " " + medication.getStrength_measurement());
                etQuantity.setText("" + medication.getMedication_quantity());
            }
            if (bundle.containsKey("isFromRefill")) {
                isFromRefill = bundle.getBoolean("isFromRefill");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMedication = view.findViewById(R.id.textview_medication_dosage);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etQuantity = view.findViewById(R.id.edittext_medication_qty);
        etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etQuantity.getText().length() > 0 && actionId == EditorInfo.IME_ACTION_GO) {
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
            if (etQuantity.getText().toString().length() > 0 && Integer.parseInt(etQuantity.getText().toString()) > 0) {
                medication.setMedication_quantity(Integer.parseInt(etQuantity.getText().toString()));
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                if (isFromRefill) {
                    bundle.putString("fragmentName", "RemoveBinFragment");
                } else {
                    bundle.putString("fragmentName", "SelectUseByDateFragment");
                }
                bundle.putBoolean("isFromRefill", isFromRefill);
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please enter a valid quantity");
            }
        }
    }
}