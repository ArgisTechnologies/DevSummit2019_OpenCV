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
import com.logicpd.papapill.data.DaySchedule;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class GetScheduledMedEarlyFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "GetScheduledMedEarlyFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnSingleDose, btnVacationSupply;
    private User user;
    private Medication medication;

    public GetScheduledMedEarlyFragment() {
        // Required empty public constructor
    }

    public static GetScheduledMedEarlyFragment newInstance() {
        return new GetScheduledMedEarlyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_get_med_early, container, false);
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
        btnSingleDose = view.findViewById(R.id.button_single_dose);
        btnSingleDose.setOnClickListener(this);
        btnVacationSupply = view.findViewById(R.id.button_vacation_supply);
        btnVacationSupply.setOnClickListener(this);

        //TODO disabling until done
        TextUtils.disableButton(btnVacationSupply);
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

        if (v == btnSingleDose) {
            ScheduleItem scheduleItem = getScheduleData();
            //TODO - need to handle getting next dose if the current schedule is done for the day (see HomeFragment)

            bundle.putSerializable("scheduleItem", scheduleItem);
            bundle.putSerializable("user", user);
            bundle.putString("fragmentName", "GetSingleDoseEarlyFragment");
        }

        if (v == btnVacationSupply) {
            //TODO
        }
        mListener.onButtonClicked(bundle);
    }

    private ScheduleItem getScheduleData() {
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());

        DaySchedule daySchedule = db.getDaySchedule(user);
        if (daySchedule.getScheduleItemList().size() > 0) {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/dd/yyyy h:mm a", Locale.getDefault());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/dd/yyyy h:mm:ss a", Locale.getDefault());

            for (final ScheduleItem scheduleItem : daySchedule.getScheduleItemList()) {
                DispenseTime dispenseTime = db.getDispenseTime(scheduleItem.getDispenseTimeId());
                //get current date
                SimpleDateFormat monthDateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
                Date now = new Date();
                String today = monthDateFormat.format(now);
                String schedtimestamp = today + " " + dispenseTime.getDispenseTime();
                String nowtimestamp = outputDateFormat.format(now);
                try {
                    Date schedDate = inputDateFormat.parse(schedtimestamp);
                    Date nowDate = outputDateFormat.parse(nowtimestamp);
                    if (schedDate.compareTo(nowDate) < 0) {
                        //event has passed, skip
                    } else {
                        //next scheduled item
                        return scheduleItem;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //TODO look at tomorrow's schedule and display that??
        }
        return null;
    }
}