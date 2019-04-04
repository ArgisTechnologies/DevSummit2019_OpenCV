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
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.MedicationsAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SelectChangeMedScheduleFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectChangeMedScheduleFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnNext;
    private TextView tvTitle, tvEmpty;
    private MedicationsAdapter adapter;
    private List<Medication> medicationList;
    private DatabaseHelper db;
    private User user;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    public SelectChangeMedScheduleFragment() {
        // Required empty public constructor
    }

    public static SelectChangeMedScheduleFragment newInstance() {
        return new SelectChangeMedScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_medication_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }
        tvTitle.setText("SELECT A MED SCHEDULE TO CHANGE");

        medicationList = db.getMedicationsForUser(user, true);

        // sort by bin #
        Collections.sort(medicationList, new Comparator<Medication>() {

            @Override
            public int compare(Medication m1, Medication m2) {
                try {
                    return m1.getMedication_location() - m2.getMedication_location();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        adapter = new MedicationsAdapter(getActivity(), medicationList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MedicationsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Medication medication = medicationList.get(position);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEditMode", true);
                List<ScheduleItem> scheduleItems = db.getScheduleItemsByMedication(medication);
                if (scheduleItems.size() == 1) {//this is a daily schedule
                    //TODO
                    bundle.putString("fragmentName", "SelectDispensingTimesFragment");

                }
                if (scheduleItems.size() > 1) {//this is a weekly or monthly schedule
                    //TODO
                    bundle.putString("fragmentName", "");

                }

                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                mListener.onButtonClicked(bundle);
            }
        });

        if (medicationList.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setText("NO SCHEDULED MEDICATIONS FOUND");
            tvEmpty.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText("OK");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        btnNext.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recyclerview_medication_list);
        tvTitle = view.findViewById(R.id.textview_title);
        tvEmpty = view.findViewById(R.id.textview_empty);
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
            bundle.putString("removeAllFragmentsUpToCurrent", "ManageMedsFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
