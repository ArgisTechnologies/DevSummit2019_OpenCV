package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.logicpd.papapill.data.adapters.DispenseTimesAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

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
public class SelectDispensingTimesFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectDispensingTimesFragment";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    OnButtonClickListener mListener;
    User user;
    TextView tvTitle, tvEmpty;
    Button btnAdd, btnNext;
    DispenseTimesAdapter adapter;
    List<DispenseTime> dispenseTimeList;
    DatabaseHelper db;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;
    Medication medication;
    boolean isFromSchedule, isEditMode;

    public SelectDispensingTimesFragment() {
        // Required empty public constructor
    }

    public static SelectDispensingTimesFragment newInstance() {
        return new SelectDispensingTimesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_select_dispense_times, container, false);
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

        dispenseTimeList = db.getDispenseTimes(false);

        // go through dispenseTimeList and check only the one(s) associated with that scheduleitem
        for (DispenseTime dispenseTime : dispenseTimeList) {
            dispenseTime.setActive(false);
            ScheduleItem scheduleItem = db.getScheduleItem(user, medication, dispenseTime.getId());
            if (scheduleItem != null) {
                dispenseTime.setActive(true);
            }
        }

        //sort list by time
        Collections.sort(dispenseTimeList, new Comparator<DispenseTime>() {
            DateFormat f = new SimpleDateFormat("h:mm a", Locale.getDefault());

            @Override
            public int compare(DispenseTime o1, DispenseTime o2) {
                try {
                    return f.parse(o1.getDispenseTime()).compareTo(f.parse(o2.getDispenseTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        adapter = new DispenseTimesAdapter(getActivity(), dispenseTimeList, true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DispenseTimesAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
               /* DispenseTime dispenseTime = dispenseTimeList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putSerializable("dispensetime", dispenseTime);
                bundle.putString("fragmentName", "");
                mListener.onButtonClicked(bundle);*/
                adapter.toggleChecked(position);
            }
        });

        if (dispenseTimeList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recyclerview_dispense_times_list);
        btnAdd = view.findViewById(R.id.button_add_dispense_time);
        btnAdd.setOnClickListener(this);
        tvEmpty = view.findViewById(R.id.textview_add_dispense_time);
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
        if (v == btnAdd) {
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putString("fragmentName", "EditDispenseTimesFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnNext) {
            // update db with current list
            List<DispenseTime> dispenseTimes = adapter.getListFromAdapter();
            for (DispenseTime dispenseTime : dispenseTimes) {
                db.updateDispenseTime(dispenseTime);
            }
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putBoolean("isEditMode", isEditMode);
            bundle.putBoolean("isFromSchedule", isFromSchedule);
            bundle.putString("fragmentName", "DirectionsQuantityPerDoseFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
