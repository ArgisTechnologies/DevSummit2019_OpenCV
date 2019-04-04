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

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.HashMap;

/**
 * SelectUseByDateFragment
 *
 * @author alankilloren
 */
public class SelectUseByDateFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectUseByDateFragment";

    private LinearLayout backButton, homeButton;
    private EditText etMonth, etDay, etYear;
    private ImageView btnMonthUp, btnMonthDown, btnDayUp, btnDayDown, btnYearUp, btnYearDown;
    private Button btnNext;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private Calendar calendar;
    private int currentMonth, currentDay, currentYear;
    private HashMap<Integer, String> months;
    private boolean isFromSchedule;

    public SelectUseByDateFragment() {
        // Required empty public constructor
    }

    public static SelectUseByDateFragment newInstance() {
        return new SelectUseByDateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_select_use_by_date, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }
        setDatePicker();
    }

    private void setDatePicker() {
        calendar = Calendar.getInstance();
        months = new HashMap<>();
        months.put(1, "JAN");
        months.put(2, "FEB");
        months.put(3, "MAR");
        months.put(4, "APR");
        months.put(5, "MAY");
        months.put(6, "JUN");
        months.put(7, "JUL");
        months.put(8, "AUG");
        months.put(9, "SEP");
        months.put(10, "OCT");
        months.put(11, "NOV");
        months.put(12, "DEC");
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        etMonth.setText(months.get(currentMonth));
        etDay.setText("" + currentDay);
        etYear.setText("" + currentYear);
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnMonthUp = view.findViewById(R.id.button_month_up);
        btnMonthUp.setOnClickListener(this);
        btnMonthDown = view.findViewById(R.id.button_month_down);
        btnMonthDown.setOnClickListener(this);

        btnDayUp = view.findViewById(R.id.button_day_up);
        btnDayUp.setOnClickListener(this);
        btnDayDown = view.findViewById(R.id.button_day_down);
        btnDayDown.setOnClickListener(this);

        btnYearUp = view.findViewById(R.id.button_year_up);
        btnYearUp.setOnClickListener(this);
        btnYearDown = view.findViewById(R.id.button_year_down);
        btnYearDown.setOnClickListener(this);

        etMonth = view.findViewById(R.id.edittext_month);
        etMonth.setEnabled(false);
        etDay = view.findViewById(R.id.edittext_day);
        etDay.setEnabled(false);
        etYear = view.findViewById(R.id.edittext_year);
        etYear.setEnabled(false);

        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
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
            //formulate a date based on input
            String sDate = months.get(currentMonth) + " " + currentDay + " " + currentYear;
            medication.setUse_by_date(sDate);
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putString("fragmentName", "ConfirmMedInfoFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnYearUp) {
            currentYear += 1;
            etYear.setText("" + currentYear);
        }
        if (v == btnYearDown) {
            currentYear -= 1;
            etYear.setText("" + currentYear);
        }
        if (v == btnMonthUp) {
            if (currentMonth < 12) {
                currentMonth += 1;
                int max = getDaysInMonth(currentYear, currentMonth);
                if (Integer.parseInt(etDay.getText().toString()) > max) {
                    currentDay = max;
                    etDay.setText("" + currentDay);
                }
            }
            etMonth.setText(months.get(currentMonth));
        }
        if (v == btnMonthDown) {
            if (currentMonth > 1) {
                currentMonth -= 1;
                int max = getDaysInMonth(currentYear, currentMonth);
                if (Integer.parseInt(etDay.getText().toString()) > max) {
                    currentDay = max;
                    etDay.setText("" + currentDay);
                }
            }
            etMonth.setText(months.get(currentMonth));
        }
        if (v == btnDayUp) {
            int max = getDaysInMonth(currentYear, currentMonth);
            if (Integer.parseInt(etDay.getText().toString()) > max) {
                currentDay = max;
                etDay.setText("" + currentDay);
            }
            if (currentDay < max) {
                currentDay += 1;
            }
            etDay.setText("" + currentDay);
        }
        if (v == btnDayDown) {
            if (currentDay > 1) {
                currentDay -= 1;
            }
            etDay.setText("" + currentDay);
        }
    }
}
