package com.logicpd.papapill.fragments.my_medication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Fragment for My Medication
 *
 * @author alankilloren
 */
public class MyMedicationFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "My Medication";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnScheduled, btnReview, btnAsNeeded, btnFillStatus, btnPause, btnTracker;
    private User user;
    private DatabaseHelper db;

    public MyMedicationFragment() {
        // Required empty public constructor
    }

    public static MyMedicationFragment newInstance() {
        return new MyMedicationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds, container, false);
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
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnAsNeeded = view.findViewById(R.id.button_get_as_needed_med);
        btnAsNeeded.setOnClickListener(this);
        btnPause = view.findViewById(R.id.button_pause_med);
        btnPause.setOnClickListener(this);
        btnTracker = view.findViewById(R.id.button_med_tracker);
        btnTracker.setOnClickListener(this);
        btnFillStatus = view.findViewById(R.id.button_check_fill_status);
        btnFillStatus.setOnClickListener(this);
        btnScheduled = view.findViewById(R.id.button_get_scheduled_med);
        btnScheduled.setOnClickListener(this);
        btnReview = view.findViewById(R.id.button_review_med_sched);
        btnReview.setOnClickListener(this);
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
        if (v == btnScheduled) {
            bundle.putSerializable("user", user);
//            bundle.putString("fragmentName", "GetScheduledMedEarlyFragment");
            bundle.putString("authFragment", "GetScheduledMedEarlyFragment");
            bundle.putString("fragmentName", "SelectUserForGetMedsFragment");
        }
        if (v == btnAsNeeded) {
            bundle.putSerializable("user", user);
            bundle.putString("authFragment", "GetAsNeededMedicationFragment");
            bundle.putString("fragmentName", "SelectUserForGetMedsFragment");
        }
        if (v == btnPause) {
            bundle.putSerializable("user", user);
            bundle.putString("authFragment", "SelectPauseMedicationFragment");
            bundle.putString("fragmentName", "SelectUserForGetMedsFragment");
        }
        if (v == btnTracker) {
            bundle.putSerializable("user", user);
            bundle.putString("authFragment", "MedicationTrackerFragment");
            bundle.putString("fragmentName", "SelectUserForGetMedsFragment");
        }
        if (v == btnFillStatus) {
            bundle.putSerializable("user", user);
            bundle.putString("authFragment", "CheckFillStatusFragment");
            bundle.putString("fragmentName", "SelectUserForGetMedsFragment");
        }
        if (v == btnReview) {
            bundle.putSerializable("user", user);
            bundle.putString("authFragment", "SelectReviewMedSchedFragment");
            bundle.putString("fragmentName", "SelectUserForGetMedsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
