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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * MedicationStrengthFragment
 *
 * @author alankilloren
 */
public class MedicationStrengthFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationStrengthFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Button btnNext;
    private Medication medication;
    private EditText etStrength;
    private TextView tvMedicationName;
    private DatabaseHelper db;

    public MedicationStrengthFragment() {
        // Required empty public constructor
    }

    public static MedicationStrengthFragment newInstance() {
        return new MedicationStrengthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_enter_strength, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        db = DatabaseHelper.getInstance(getActivity());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (medication != null) {
                tvMedicationName.setText(medication.getName());
                etStrength.setText(medication.getStrength_measurement());
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMedicationName = view.findViewById(R.id.textview_medication_name);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etStrength = view.findViewById(R.id.edittext_medication_strength);
        etStrength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etStrength.getText().length() > 1 && actionId == EditorInfo.IME_ACTION_GO) {
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
            if (etStrength.getText().toString().length() > 1) {
                medication.setStrength_measurement(etStrength.getText().toString());
                if (db.isDuplicateMedicationFound(medication)) {
                    // found duplicate medication, show exception screen
                    bundle.putSerializable("user", user);
                    bundle.putSerializable("medication", medication);
                    bundle.putString("fragmentName", "DuplicateMedicationFragment");
                    mListener.onButtonClicked(bundle);
                } else {
                    bundle.putSerializable("user", user);
                    bundle.putSerializable("medication", medication);
                    bundle.putString("fragmentName", "MedicationDosageFragment");
                    mListener.onButtonClicked(bundle);
                }
            } else {
                TextUtils.showToast(getActivity(), "Please enter a medication strength");
            }
        }
    }
}