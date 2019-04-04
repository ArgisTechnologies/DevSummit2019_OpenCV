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
public class DispenseTimeSummaryFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Verify Contact Info";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private TextView tvSummary, tvTitle;
    private Button btnEdit, btnNext;
    private DatabaseHelper db;
    private boolean isEditMode, isFromSchedule;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private User user;
    private DispenseTime dispenseTime;

    public DispenseTimeSummaryFragment() {
        // Required empty public constructor
    }

    public static DispenseTimeSummaryFragment newInstance() {
        return new DispenseTimeSummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_dispense_time_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("isEditMode")) {
                isEditMode = bundle.getBoolean("isEditMode");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
            isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            isFromNotifications = bundle.getBoolean("isFromNotifications");
            user = (User) bundle.getSerializable("user");
            dispenseTime = (DispenseTime) bundle.getSerializable("dispensetime");
            StringBuilder sb = new StringBuilder();
            sb.append(dispenseTime.getDispenseName() + "\n" + dispenseTime.getDispenseTime());
            tvSummary.setText(sb.toString());
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnEdit = view.findViewById(R.id.button_edit_dispense_time);
        btnEdit.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        tvSummary = view.findViewById(R.id.textview_dispense_time_summary);
        tvTitle = view.findViewById(R.id.textview_title);
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
            // add/update db
            if (isEditMode) {
                //edit
                int returnVal = db.updateDispenseTime(dispenseTime);
                if (returnVal > 0) {
                    bundle.putBoolean("isEditMode", isEditMode);
                    bundle.putBoolean("isFromSchedule", isFromSchedule);
                    bundle.putSerializable("user", user);
                    bundle.putBoolean("updateSchedule", true);//update schedule to reflect any time changes, etc.
                    bundle.putString("removeAllFragmentsUpToCurrent", "SelectEditDispenseTimeFragment");
                    mListener.onButtonClicked(bundle);
                } else {
                    TextUtils.showToast(getActivity(), "Problem saving info");
                }
            } else {
                //add
                int returnVal = db.addDispenseTime(dispenseTime);
                if (returnVal > 0) {
                    bundle.putBoolean("isEditMode", isEditMode);
                    bundle.putBoolean("isFromSchedule", isFromSchedule);
                    bundle.putSerializable("user", user);
                    if (isFromSchedule) {
                        bundle.putString("removeAllFragmentsUpToCurrent", "SelectDispensingTimesFragment");
                    } else {
                        bundle.putString("removeAllFragmentsUpToCurrent", "EditDispenseTimesFragment");
                    }
                    mListener.onButtonClicked(bundle);
                } else {
                    TextUtils.showToast(getActivity(), "Problem saving info");
                }
            }
        }
        if (v == btnEdit) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("dispensetime", dispenseTime);
            bundle.putBoolean("isEditMode", isEditMode);
            bundle.putString("fragmentName", "DispenseTimeNameFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
