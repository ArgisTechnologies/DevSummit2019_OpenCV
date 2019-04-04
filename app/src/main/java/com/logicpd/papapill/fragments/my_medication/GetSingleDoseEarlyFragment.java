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
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class GetSingleDoseEarlyFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "GetSingleDoseEarlyFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnCancel, btnDispense;
    private User user;
    private TextView tvText1, tvText2;
    private Medication medication;
    private ScheduleItem scheduleItem;
    private int dispenseAmount = 0;

    public GetSingleDoseEarlyFragment() {
        // Required empty public constructor
    }

    public static GetSingleDoseEarlyFragment newInstance() {
        return new GetSingleDoseEarlyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_get_single_dose_early, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");

            scheduleItem = (ScheduleItem) bundle.getSerializable("scheduleItem");
            if (scheduleItem != null) {
                DispenseTime dispenseTime = DatabaseHelper.getInstance(getActivity()).getDispenseTime(scheduleItem.getDispenseTimeId());
                tvText1.setText("YOU HAVE CHOSEN TO DISPENSE YOUR " + dispenseTime.getDispenseTime()
                        + " " + dispenseTime.getDispenseName()
                        + " DOSE EARLY.");
            }
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
        btnDispense = view.findViewById(R.id.button_dispense_now);
        btnDispense.setOnClickListener(this);
        tvText1 = view.findViewById(R.id.textview_dose_text);
        tvText2 = view.findViewById(R.id.textview_dose_text2);
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

        if (v == btnDispense) {
            // send user, medication and dispense amount to DispenseMedsFragment
            medication = DatabaseHelper.getInstance(getActivity()).getMedication(scheduleItem.getMedicationId());
            dispenseAmount = scheduleItem.getDispenseAmount();
            bundle.putSerializable("user", user);
            bundle.putBoolean("isEarlyDispense", true);
            bundle.putSerializable("medication", medication);
            bundle.putInt("dispense_amount", dispenseAmount);
            bundle.putString("fragmentName", "DispenseMedsFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}