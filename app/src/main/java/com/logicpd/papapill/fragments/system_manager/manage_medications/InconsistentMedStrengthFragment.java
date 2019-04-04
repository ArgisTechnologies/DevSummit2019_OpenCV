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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class InconsistentMedStrengthFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "InconsistentMedStrengthFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private Button btnCancel, btnRemove;

    public InconsistentMedStrengthFragment() {
        // Required empty public constructor
    }

    public static InconsistentMedStrengthFragment newInstance() {
        return new InconsistentMedStrengthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_inconsistent_med_strength, container, false);
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
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);
        btnRemove = view.findViewById(R.id.button_remove_med);
        btnRemove.setOnClickListener(this);
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
        if (v == btnCancel) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("removeAllFragmentsUpToCurrent", "ManageMedsFragment");
        }
        if (v == btnRemove) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "ConfirmRemoveMedicationFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}