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
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

public class DispenseTimeNameFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Medication Name";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private EditText etDispenseName;
    private OnButtonClickListener mListener;
    private User user;
    private DispenseTime dispenseTime;
    private boolean isEditMode = false;
    private boolean isFromSchedule;

    public DispenseTimeNameFragment() {
        // Required empty public constructor
    }

    public static DispenseTimeNameFragment newInstance() {
        return new DispenseTimeNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_dispense_time_name, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            dispenseTime = (DispenseTime) bundle.getSerializable("dispensetime");
            if (dispenseTime != null && dispenseTime.getDispenseName() != null) {
                etDispenseName.setText(dispenseTime.getDispenseName());
            }
            if (bundle.containsKey("isEditMode")) {
                isEditMode = bundle.getBoolean("isEditMode");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etDispenseName = view.findViewById(R.id.edittext_dispense_time_name);
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
            if (etDispenseName.getText().toString().length() > 3) {
                if (isEditMode) {
                    //edit existing record
                    dispenseTime.setDispenseName(etDispenseName.getText().toString());
                    bundle.putSerializable("dispensetime", dispenseTime);
                    bundle.putSerializable("user", user);
                    bundle.putBoolean("isEditMode", isEditMode);
                    bundle.putString("fragmentName", "DispenseTimeFragment");
                    mListener.onButtonClicked(bundle);
                } else {
                    //adding a new record
                    DispenseTime dispenseTime = new DispenseTime();
                    dispenseTime.setUserId(user.getId());
                    dispenseTime.setDispenseName(etDispenseName.getText().toString());
                    bundle.putSerializable("dispensetime", dispenseTime);
                    bundle.putBoolean("isEditMode", isEditMode);
                    bundle.putBoolean("isFromSchedule", isFromSchedule);
                    bundle.putSerializable("user", user);
                    bundle.putString("fragmentName", "DispenseTimeFragment");
                    mListener.onButtonClicked(bundle);
                }

            } else {
                TextUtils.showToast(getActivity(), "Please enter a valid dispense time name");
            }
        }
    }
}
