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
import com.logicpd.papapill.data.adapters.FillStatusAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class CheckFillStatusFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "CheckFillStatusFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnDone;
    private User user;
    private FillStatusAdapter adapter;
    private List<Medication> medicationList;
    private DatabaseHelper db;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private TextView tvEmpty;

    public CheckFillStatusFragment() {
        // Required empty public constructor
    }

    public static CheckFillStatusFragment newInstance() {
        return new CheckFillStatusFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_fill_status, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }

        medicationList = db.getMedicationsForUser(user);
        adapter = new FillStatusAdapter(getActivity(), medicationList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        /*adapter.setOnItemClickListener(new FillStatusAdapter.MyClickListener() {
            @Override
            public void onItemClick ( int position, View v){
                *//*Medication medication = medicationList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "ConfirmRefillMedicationFragment");
                mListener.onButtonClicked(bundle);*//*
            }
        });*/

        if (medicationList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recyclerview_medication_list);
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
}