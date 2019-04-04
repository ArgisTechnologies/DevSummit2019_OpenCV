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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class EditDispenseTimesFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "EditDispenseTimesFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnAdd, btnEdit, btnDelete;
    private User user;
    private DatabaseHelper db;
    private boolean isFromSchedule;
    private int MAX_DISPENSE_TIMES = 24;//TODO change or move this as need be

    public EditDispenseTimesFragment() {
        // Required empty public constructor
    }

    public static EditDispenseTimesFragment newInstance() {
        return new EditDispenseTimesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_edit_dispense_times, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }
        if (db.getDispenseTimes(false).size() == 0) {
            TextUtils.disableButton(btnEdit);
            TextUtils.disableButton(btnDelete);
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnAdd = view.findViewById(R.id.button_add_dispense_time);
        btnAdd.setOnClickListener(this);
        btnEdit = view.findViewById(R.id.button_edit_dispense_time);
        btnEdit.setOnClickListener(this);
        btnDelete = view.findViewById(R.id.button_delete_dispense_time);
        btnDelete.setOnClickListener(this);
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
        if (v == btnAdd) {
            if (db.getDispenseTimes(false).size() == MAX_DISPENSE_TIMES) {
                TextUtils.showToast(getActivity(), "You are allowed a maximum of 24 dispense time entries. Please edit or delete an existing one first.");
            } else {
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                bundle.putString("fragmentName", "DispenseTimeNameFragment");
                bundle.putSerializable("user", user);
                mListener.onButtonClicked(bundle);
            }
        }
        if (v == btnEdit) {
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putString("fragmentName", "SelectEditDispenseTimeFragment");
            bundle.putSerializable("user", user);
            mListener.onButtonClicked(bundle);
        }
        if (v == btnDelete) {
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putString("fragmentName", "SelectDeleteDispenseTimeFragment");
            bundle.putSerializable("user", user);
            mListener.onButtonClicked(bundle);
        }
    }
}