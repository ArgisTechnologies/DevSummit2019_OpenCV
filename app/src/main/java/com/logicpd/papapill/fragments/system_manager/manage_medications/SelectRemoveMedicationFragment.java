package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.MedicationsAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SelectRemoveMedicationFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectRemoveMedicationFragment";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    OnButtonClickListener mListener;
    User user;
    TextView tvTitle, tvEmpty;
    Button btnNext;
    MedicationsAdapter adapter;
    List<Medication> medicationList;
    DatabaseHelper db;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;

    public SelectRemoveMedicationFragment() {
        // Required empty public constructor
    }

    public static SelectRemoveMedicationFragment newInstance() {
        return new SelectRemoveMedicationFragment();
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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }
        tvTitle.setText("SELECT A MEDICATION TO REMOVE");

        medicationList = db.getMedicationsForUser(user);
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
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "ConfirmRemoveMedicationFragment");
                mListener.onButtonClicked(bundle);
            }
        });

        if (medicationList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText("DONE");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        tvEmpty = view.findViewById(R.id.textview_empty);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        btnNext.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recyclerview_medication_list);
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
        if (v == btnNext) {
            bundle.putString("removeAllFragmentsUpToCurrent", "ManageMedsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
