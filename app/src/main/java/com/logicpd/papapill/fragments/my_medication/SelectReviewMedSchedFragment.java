package com.logicpd.papapill.fragments.my_medication;

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
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SelectReviewMedSchedFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectReviewMedSchedFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnMedication, btnWeek, btnDay, btnMonth;
    private User user;

    public SelectReviewMedSchedFragment() {
        // Required empty public constructor
    }

    public static SelectReviewMedSchedFragment newInstance() {
        return new SelectReviewMedSchedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_select_review_med_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnMedication = view.findViewById(R.id.button_by_medication);
        btnMedication.setOnClickListener(this);
        btnWeek = view.findViewById(R.id.button_by_week);
        btnWeek.setOnClickListener(this);
        btnDay = view.findViewById(R.id.button_by_day);
        btnDay.setOnClickListener(this);
        btnMonth = view.findViewById(R.id.button_by_month);
        btnMonth.setOnClickListener(this);

        //TODO disabling these until done
        TextUtils.disableButton(btnWeek);
        TextUtils.disableButton(btnDay);
        TextUtils.disableButton(btnMonth);

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
            bundle.putString("removeAllFragmentsUpToCurrent", "MyMedicationFragment");
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnMedication) {
            bundle.putSerializable("user", user);
            bundle.putString("fragmentName", "SelectMedScheduleListFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}