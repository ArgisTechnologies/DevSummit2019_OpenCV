package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class PlaceMedOtherFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "PlaceMedOtherFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Medication medication;
    private Button btnDone, btnHelp;
    private TextView tvTitle, tvPlaceMed;
    private DatabaseHelper db;

    public PlaceMedOtherFragment() {
        // Required empty public constructor
    }

    public static PlaceMedOtherFragment newInstance() {
        return new PlaceMedOtherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_place_med_other, container, false);
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
            tvTitle.setText("PLACE MEDICATION IN " + medication.getMedication_location_name());
            tvPlaceMed.setText("PLACE YOUR MEDICATION IN " + medication.getMedication_location_name() + " NOW");
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        tvPlaceMed = view.findViewById(R.id.textview_place_other_location);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        btnHelp = view.findViewById(R.id.button_help);
        btnHelp.setOnClickListener(this);
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
        if (v == btnDone) {
            //add medication to db, etc.
            int returnVal = db.updateMedication(medication);
            if (returnVal > 0) {
                bundle.putSerializable("user", user);
                bundle.putSerializable("medication", medication);
                bundle.putString("fragmentName", "MedicationAddedFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Problem saving medication");
            }
        }
        if (v == btnHelp) {
            //TODO
        }
    }
}