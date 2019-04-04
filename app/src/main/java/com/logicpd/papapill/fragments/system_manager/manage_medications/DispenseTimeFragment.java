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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class DispenseTimeFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "DispenseTimeFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private EditText etHour, etMinute, etAMPM;
    private ImageView btnHourUp, btnHourDown, btnMinuteUp, btnMinuteDown, btnAMPMUp, btnAMPMDown;
    private Button btnNext;
    private TextView tvTitle;
    private OnButtonClickListener mListener;
    private User user;
    private DispenseTime dispenseTime;
    private Calendar calendar;
    private int currentHour, currentMinute, currentAMPM;
    private boolean isEditMode, isFromSchedule;

    public DispenseTimeFragment() {
        // Required empty public constructor
    }

    public static DispenseTimeFragment newInstance() {
        return new DispenseTimeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_set_dispense_time, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            dispenseTime = (DispenseTime) bundle.getSerializable("dispensetime");
            tvTitle.setText("ENTER \"" + dispenseTime.getDispenseName() + "\" TIME");

            if (bundle.containsKey("isEditMode")) {
                isEditMode = bundle.getBoolean("isEditMode");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
            if (isEditMode) {
                if (dispenseTime != null && dispenseTime.getDispenseTime() != null) {
                    //set picker from bundled dispense time
                    //break dispense time into chunks
                    String sTime = dispenseTime.getDispenseTime();
                    String[] sTimeArray = sTime.split(":");
                    etHour.setText(sTimeArray[0]);
                    currentHour = Integer.parseInt(etHour.getText().toString());
                    String[] sTimeArray2 = sTimeArray[1].split(" ");
                    etMinute.setText(sTimeArray2[0]);
                    currentMinute = Integer.parseInt(etMinute.getText().toString());
                    etAMPM.setText(sTimeArray2[1]);
                    String ampm = etAMPM.getText().toString();
                    if (ampm.equals("AM")) {
                        currentAMPM = 0;
                    } else {
                        currentAMPM = 1;
                    }
                }
            } else {
                setTimePicker();
            }
        }
    }

    private void setTimePicker() {
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
        currentHour = calendar.get(Calendar.HOUR);
        currentMinute = calendar.get(Calendar.MINUTE);
        currentAMPM = calendar.get(Calendar.AM_PM);
        etHour.setText("" + currentHour);
        if (String.valueOf(currentMinute).length() == 1) {
            etMinute.setText("0" + currentMinute);
        } else {
            etMinute.setText("" + currentMinute);
        }
        if (currentAMPM == 0) {
            etAMPM.setText("AM");
        } else {
            etAMPM.setText("PM");
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnHourUp = view.findViewById(R.id.button_hour_up);
        btnHourUp.setOnClickListener(this);
        btnHourDown = view.findViewById(R.id.button_hour_down);
        btnHourDown.setOnClickListener(this);

        btnMinuteUp = view.findViewById(R.id.button_minute_up);
        btnMinuteUp.setOnClickListener(this);
        btnMinuteDown = view.findViewById(R.id.button_minute_down);
        btnMinuteDown.setOnClickListener(this);

        btnAMPMUp = view.findViewById(R.id.button_ampm_up);
        btnAMPMUp.setOnClickListener(this);
        btnAMPMDown = view.findViewById(R.id.button_ampm_down);
        btnAMPMDown.setOnClickListener(this);

        etHour = view.findViewById(R.id.edittext_hour);
        etHour.setEnabled(false);
        etMinute = view.findViewById(R.id.edittext_minute);
        etMinute.setEnabled(false);
        etAMPM = view.findViewById(R.id.edittext_ampm);
        etAMPM.setEnabled(false);

        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);

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

    /**
     * Returns the maximum number of days in a given month/year
     *
     * @param year  Passed in year 4-digits
     * @param month Passed in month
     * @return Max # of days in month
     */
    private int getDaysInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.lengthOfMonth();
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
            //formulate a time string based on input
            String sTime = etHour.getText().toString() + ":" + etMinute.getText().toString() + " " + etAMPM.getText().toString();
            dispenseTime.setDispenseTime(sTime);
            bundle.putSerializable("user", user);
            bundle.putBoolean("isEditMode", isEditMode);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putSerializable("dispensetime", dispenseTime);
            bundle.putString("fragmentName", "DispenseTimeSummaryFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnAMPMUp) {
            etAMPM.setText("PM");
        }
        if (v == btnAMPMDown) {
            etAMPM.setText("AM");
        }
        if (v == btnHourUp) {
            if (currentHour < 12) {
                currentHour += 1;
                etHour.setText("" + currentHour);
            }
        }
        if (v == btnHourDown) {
            if (currentHour > 1) {
                currentHour -= 1;
                etHour.setText("" + currentHour);
            }
        }
        if (v == btnMinuteUp) {
            if (currentMinute < 59) {
                currentMinute += 1;
                if (String.valueOf(currentMinute).length() == 1) {
                    etMinute.setText("0" + currentMinute);
                } else {
                    etMinute.setText("" + currentMinute);
                }
            }
        }
        if (v == btnMinuteDown) {
            if (currentMinute > 0) {
                currentMinute -= 1;
                if (String.valueOf(currentMinute).length() == 1) {
                    etMinute.setText("0" + currentMinute);
                } else {
                    etMinute.setText("" + currentMinute);
                }
            }
        }
    }
}