package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DaySchedule;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.DispenseTimesQuantityAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class EnterWeeklyQuantityPerDoseFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "EnterWeeklyQuantityPerDoseFragment";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    OnButtonClickListener mListener;
    User user;
    TextView tvTitle, tvEmpty, tvDayofWeek;
    DispenseTimesQuantityAdapter adapter;
    List<DispenseTime> dispenseTimeList;
    List<DaySchedule> dayScheduleList;
    DatabaseHelper db;
    RecyclerView recyclerView;
    Button btnDone;
    ImageView btnPrevious, btnNext;
    Medication medication;
    ProgressBar progressBar;
    DayOfWeek dayOfWeek;
    boolean isFromSchedule;

    public EnterWeeklyQuantityPerDoseFragment() {
        // Required empty public constructor
    }

    public static EnterWeeklyQuantityPerDoseFragment newInstance() {
        return new EnterWeeklyQuantityPerDoseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_enter_weekly_qty_per_dose, container, false);
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
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }

        dispenseTimeList = db.getDispenseTimes(true);
        adapter = new DispenseTimesQuantityAdapter(getActivity(), dispenseTimeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        /*adapter.setOnItemClickListener(new DispenseTimesQuantityAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
               *//* DispenseTime dispenseTime = dispenseTimeList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putSerializable("dispensetime", dispenseTime);
                bundle.putString("fragmentName", "");
                mListener.onButtonClicked(bundle);*//*
            }
        });*/
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
        dayOfWeek = DayOfWeek.of(day);
        tvDayofWeek.setText(dayOfWeek.name());

        dayScheduleList = new ArrayList<>();
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        recyclerView = view.findViewById(R.id.recyclerview_dispense_times_list);
        tvEmpty = view.findViewById(R.id.textview_add_dispense_time);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        btnPrevious = view.findViewById(R.id.button_previous_day);
        btnPrevious.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next_day);
        btnNext.setOnClickListener(this);
        tvDayofWeek = view.findViewById(R.id.textview_day_of_week);
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
            if (dayScheduleList.size() > 0) {
                // show a warning dialog when going from weekly back to daily
                showWarningDialog("Back");
            } else {
                bundle.putString("fragmentName", "Back");
                mListener.onButtonClicked(bundle);
            }
        }
        if (v == homeButton) {
            if (dayScheduleList.size() > 0) {
                // show a warning dialog when going from weekly back to home
                showWarningDialog("Home");
            } else {
                bundle.putString("fragmentName", "Home");
                mListener.onButtonClicked(bundle);
            }
        }

        if (v == btnDone) {
            Log.d(AppConstants.TAG, "Schedule list size: " + dayScheduleList.size());

            upsert();//update the current day schedule in the list

            // update db with dayschedulelist
            new dbTask().execute();

        }
        if (v == btnPrevious) {
            upsert();//update the current day schedule in the list

            //go back a day
            dayOfWeek = dayOfWeek.minus(1);
            tvDayofWeek.setText(dayOfWeek.name());

            // get dispense amounts for selected day and populate dispenseTimeList
            getDispenseAmounts();
            adapter.notifyDataSetChanged();
        }
        if (v == btnNext) {
            upsert();//update the current day schedule in the list

            //advance a day
            dayOfWeek = dayOfWeek.plus(1);
            tvDayofWeek.setText(dayOfWeek.name());

            // get dispense amounts for selected day and populate dispenseTimeList
            getDispenseAmounts();
            adapter.notifyDataSetChanged();
        }
    }

    private void showWarningDialog(final String fragmentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage("Do you want to discard your changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putString("fragmentName", fragmentName);
                        mListener.onButtonClicked(bundle);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Populates the dispense times list with saved values if found for the current day selected
     */
    private void getDispenseAmounts() {
        boolean isFound = false;
        int index = -1;
        for (int i = 0; i < dayScheduleList.size(); i++) {
            DaySchedule daySchedule1 = dayScheduleList.get(i);
            if (daySchedule1.getDay().equals(dayOfWeek.name())) {
                //found, update
                isFound = true;
                index = i;
                break;
            }
        }
        if (isFound) {
            //get schedule items
            List<ScheduleItem> scheduleItems = dayScheduleList.get(index).getScheduleItemList();

            //loop thru schedule items and populate adapter with saved values
            int i = 0;
            for (ScheduleItem scheduleItem : scheduleItems) {
                adapter.setDispenseAmount(i, scheduleItem.getDispenseAmount());
                i += 1;
            }
        } else {
            //not found, so set values to 0
            for (int i = 0; i < dispenseTimeList.size(); i++) {
                adapter.setDispenseAmount(i, 0);
            }
        }
    }

    /**
     * Insert or update the current day in the list
     */
    private void upsert() {

        // form a schedule item based on current day/selections
        DaySchedule daySchedule = new DaySchedule();
        List<DispenseTime> dispenseTimes = adapter.getListFromAdapter();
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        for (DispenseTime dispenseTime : dispenseTimes) {
            ScheduleItem scheduleItem = new ScheduleItem();
            scheduleItem.setUserId(user.getId());
            scheduleItem.setMedicationId(medication.getId());
            scheduleItem.setDispenseTimeId(dispenseTime.getId());
            scheduleItem.setDispenseAmount(dispenseTime.getDispenseAmount());
            scheduleItem.setScheduleType(2);//weekly
            scheduleItem.setScheduleDay(dayOfWeek.name());
            scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
            scheduleItem.setDispenseName(dispenseTime.getDispenseName());

            scheduleItems.add(scheduleItem);
            daySchedule.setDay(dayOfWeek.name());
            daySchedule.setScheduleItemList(scheduleItems);
        }

        // check list for current entry
        boolean isFound = false;
        int index = -1;
        for (int i = 0; i < dayScheduleList.size(); i++) {
            DaySchedule daySchedule1 = dayScheduleList.get(i);
            if (daySchedule1.getDay().equals(dayOfWeek.name())) {
                //found, update
                isFound = true;
                index = i;
                break;
            }
        }
        if (isFound) {
            dayScheduleList.set(index, daySchedule);
        } else {
            dayScheduleList.add(daySchedule);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class dbTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            // go through dayScheduleList and save schedule items to db
            for (DaySchedule daySchedule : dayScheduleList) {
                List<ScheduleItem> scheduleItems = daySchedule.getScheduleItemList();
                for (ScheduleItem scheduleItem : scheduleItems) {
                    db.addScheduleItem(scheduleItem);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Bundle bundle = new Bundle();
            // advance to enter medication quantity
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "MedicationQuantityMessageFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}