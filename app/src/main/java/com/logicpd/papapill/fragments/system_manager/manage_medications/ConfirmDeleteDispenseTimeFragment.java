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

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class ConfirmDeleteDispenseTimeFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "ConfirmDeleteDispenseTimeFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private DispenseTime dispenseTime;
    private TextView tvDeleteText;
    private Button btnCancel, btnRemove;
    private DatabaseHelper db;
    private boolean isFromSchedule;

    public ConfirmDeleteDispenseTimeFragment() {
        // Required empty public constructor
    }

    public static ConfirmDeleteDispenseTimeFragment newInstance() {
        return new ConfirmDeleteDispenseTimeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_confirm_delete_dispense_time, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            dispenseTime = (DispenseTime) bundle.getSerializable("dispensetime");
            tvDeleteText.setText("DELETE " + dispenseTime.getDispenseName() + " " + dispenseTime.getDispenseTime() + " DISPENSING TIME FROM SYSTEM?");
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
        tvDeleteText = view.findViewById(R.id.textview_delete_dispense_time_text);
        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);
        btnRemove = view.findViewById(R.id.button_delete_dispense_time);
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
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnCancel) {
            backButton.performClick();
        }
        if (v == btnRemove) {
            // remove dispense time from db and proceed to DispenseTimeDeletedFragment
            int returnVal = db.deleteDispenseTime(dispenseTime);
            if (returnVal > 0) {
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                bundle.putString("fragmentName", "DispenseTimeDeletedFragment");
                bundle.putSerializable("user", user);
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Problem deleting dispense time");
            }
        }
    }
}