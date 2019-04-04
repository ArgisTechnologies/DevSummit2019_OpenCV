package com.logicpd.papapill.fragments.my_medication;

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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.MedicationsCheckAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SelectPauseMedicationFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectPauseMedicationFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private TextView tvTitle, tvEmpty;
    private MedicationsCheckAdapter adapter;
    private List<Medication> medicationList;
    private DatabaseHelper db;
    private User user;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private Button btnSelect, btnNext;
    private boolean isAllSelected = false;
    private boolean isNoMeds = false;

    public SelectPauseMedicationFragment() {
        // Required empty public constructor
    }

    public static SelectPauseMedicationFragment newInstance() {
        return new SelectPauseMedicationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_pause_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        tvTitle.setText("SELECT MEDICATIONS TO PAUSE");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }

        medicationList = db.getMedicationsForUser(user);
        adapter = new MedicationsCheckAdapter(getActivity(), medicationList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MedicationsCheckAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                adapter.toggleChecked(position);
            }
        });

        if (medicationList.size() == 0) {
            isNoMeds = true;
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnSelect.setVisibility(View.GONE);
            btnNext.setText("OK");
        } else {
            isNoMeds = false;
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        recyclerView = view.findViewById(R.id.recyclerview_medication_list);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        btnSelect = view.findViewById(R.id.button_select);
        btnSelect.setOnClickListener(this);
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
            bundle.putString("removeAllFragmentsUpToCurrent", "MyMedicationFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnSelect) {
            if (!isAllSelected) {
                adapter.selectAll(true);
                btnSelect.setText("DESELECT ALL");
                isAllSelected = true;
            } else {
                adapter.selectAll(false);
                btnSelect.setText("SELECT ALL");
                isAllSelected = false;
            }
        }
        if (v == btnNext) {
            if (isNoMeds) {
                backButton.performClick();
            } else {
                // update db with current list
                List<Medication> medications = adapter.getListFromAdapter();
                for (Medication medication : medications) {
                    db.updateMedication(medication);
                }
                bundle.putSerializable("user", user);
                bundle.putString("fragmentName", "MedicationsPausedFragment");
                mListener.onButtonClicked(bundle);
            }
        }
    }
}