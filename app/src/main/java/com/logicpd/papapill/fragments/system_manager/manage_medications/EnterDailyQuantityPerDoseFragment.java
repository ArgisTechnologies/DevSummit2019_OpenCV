package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.DispenseTimesQuantityAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;
import com.logicpd.papapill.utils.TextUtils;

import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class EnterDailyQuantityPerDoseFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "EnterDailyQuantityPerDoseFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private TextView tvTitle, tvEmpty;
    private DispenseTimesQuantityAdapter adapter;
    private List<DispenseTime> dispenseTimeList;
    private DatabaseHelper db;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private Button btnWeek, btnMonth, btnDone;
    private Medication medication;
    private ProgressBar progressBar;
    private boolean isFromSchedule, isEditMode;

    public EnterDailyQuantityPerDoseFragment() {
        // Required empty public constructor
    }

    public static EnterDailyQuantityPerDoseFragment newInstance() {
        return new EnterDailyQuantityPerDoseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_enter_qty_per_dose, container, false);
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
            if (bundle.containsKey("isEditMode")) {
                isEditMode = bundle.getBoolean("isEditMode");
            }
        }

        dispenseTimeList = db.getDispenseTimes(true);
        adapter = new DispenseTimesQuantityAdapter(getActivity(), dispenseTimeList);

        if (isEditMode) {
            // set the saved quantity for each dispense time
            for (int i = 0; i < dispenseTimeList.size(); i++) {
                DispenseTime dispenseTime = dispenseTimeList.get(i);
                int id = dispenseTime.getId();
                ScheduleItem scheduleItem = db.getScheduleItemByDispenseTimeId(id);
                if (scheduleItem != null) {
                    int dispenseAmount = scheduleItem.getDispenseAmount();
                    adapter.setDispenseAmount(i, dispenseAmount);
                }
            }
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        //TODO disabling these until done
        if (isEditMode) {
            TextUtils.disableButton(btnMonth);
            TextUtils.disableButton(btnWeek);
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        btnWeek = view.findViewById(R.id.button_week_view);
        btnWeek.setOnClickListener(this);
        btnMonth = view.findViewById(R.id.button_month_view);
        btnMonth.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recyclerview_dispense_times_list);
        tvEmpty = view.findViewById(R.id.textview_add_dispense_time);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
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
        if (v == btnWeek) {
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "EnterWeeklyQuantityPerDoseFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnMonth) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putString("fragmentName", "EnterMonthlyQuantityPerDoseFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnDone) {
            new dbTask().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class dbTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            //get latest list (with changes) from adapter
            List<DispenseTime> dispenseTimes = adapter.getListFromAdapter();

            //delete all scheduled items related to this user/medication
            if (isEditMode) {
                db.deleteScheduleItems(user, medication);
            }

            for (DispenseTime dispenseTime : dispenseTimes) {
                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setUserId(user.getId());
                scheduleItem.setMedicationId(medication.getId());
                scheduleItem.setDispenseTimeId(dispenseTime.getId());
                scheduleItem.setDispenseAmount(dispenseTime.getDispenseAmount());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());
                scheduleItem.setScheduleType(1);//daily

                //add schedule item
                db.addScheduleItem(scheduleItem);
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

            if (isEditMode) {
                bundle.putBoolean("updateSchedule", true);
                bundle.putString("fragmentName", "Home");
            } else {
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putBoolean("isFromSchedule", isFromSchedule);
                bundle.putBoolean("isEditMode", isEditMode);
                bundle.putString("fragmentName", "MedicationQuantityMessageFragment");
            }
            mListener.onButtonClicked(bundle);
        }
    }
}
