package com.logicpd.papapill.fragments.my_medication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.logicpd.papapill.misc.AppConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class MedicationScheduleSummaryFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "MedicationScheduleSummaryFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private TextView tvSummary, tvTitle;
    private Button btnDone;
    private DatabaseHelper db;
    private User user;
    private Medication medication;

    public MedicationScheduleSummaryFragment() {
        // Required empty public constructor
    }

    public static MedicationScheduleSummaryFragment newInstance() {
        return new MedicationScheduleSummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_med_schedule_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (medication != null) {
                tvTitle.setText(medication.getName() + " "
                        + medication.getStrength_value()
                        + " " + medication.getStrength_measurement()
                        + " SCHEDULE");
            }
        }

        getScheduleData();

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        tvSummary = view.findViewById(R.id.textview_med_sched_summary);
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
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnDone) {
            bundle.putSerializable("user", user);
            bundle.putString("removeAllFragmentsUpToCurrent", "MyMedicationFragment");
        }
        mListener.onButtonClicked(bundle);
    }

    private void getScheduleData() {
        StringBuilder sb = new StringBuilder();
        //sb.append("DOSAGE TIME / INFO\n");

        List<ScheduleItem> scheduleItems = db.getAllScheduledItemsForUser(user, medication);
        //sort list by time
        Collections.sort(scheduleItems, new Comparator<ScheduleItem>() {
            DateFormat f = new SimpleDateFormat("h:mm a", Locale.getDefault());

            @Override
            public int compare(ScheduleItem o1, ScheduleItem o2) {
                try {
                    return f.parse(o1.getDispenseTime()).compareTo(f.parse(o2.getDispenseTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        if (scheduleItems.size() > 0) {
            // loop through and form a summary of the scheduled med
            for (ScheduleItem scheduleItem : scheduleItems) {
                DispenseTime dispenseTime = db.getDispenseTime(scheduleItem.getDispenseTimeId());

                //display sections for different schedule types (1=daily, 2=weekly, 3=monthly)
                if (scheduleItem.getScheduleType() == 1) {//daily
                    sb.append(dispenseTime.getDispenseName())
                            .append(" DAILY @ ")
                            .append(dispenseTime.getDispenseTime())
                            .append("\n");
                }
                if (scheduleItem.getScheduleType() == 2) {//weekly
                    sb.append(dispenseTime.getDispenseName())
                            .append(" ")
                            .append(scheduleItem.getScheduleDay())
                            .append(" @ ")
                            .append(dispenseTime.getDispenseTime())
                            .append("\n");
                }
                if (scheduleItem.getScheduleType() == 3) {//monthly
                    sb.append(dispenseTime.getDispenseName())
                            .append(" ")
                            .append(scheduleItem.getScheduleDate())
                            .append(" @ ")
                            .append(dispenseTime.getDispenseTime())
                            .append("\n");
                }
            }
        } else {
            sb.append("THIS IS AN AS-NEEDED MEDICATION.");
        }
        tvSummary.setText(sb.toString());
    }
}