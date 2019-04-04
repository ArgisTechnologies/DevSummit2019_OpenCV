package com.logicpd.papapill.fragments.my_medication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.interfaces.OnRotateCompleteListener;


public class CannotRetrieveMedFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "CannotRetrieveMedFragment";
    private OnButtonClickListener mListener;
    private Button btnRetry;
    private Button btnManualRetrieval;
    private User user;
    private Medication medication;
    private int dispenseAmount = 0;
    private boolean isEarlyDispense, isFromSchedule;

    public static CannotRetrieveMedFragment newInstance()
    {
        return new CannotRetrieveMedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cannot_retrieve_med, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            dispenseAmount = bundle.getInt("dispense_amount");

            if (bundle.containsKey("isEarlyDispense")) {
                isEarlyDispense = bundle.getBoolean("isEarlyDispense");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }

            TextView textView = view.findViewById(R.id.textview_username);
            textView.setText(user.getUsername());
        }

        btnRetry = view.findViewById(R.id.button_retry);
        btnRetry.setOnClickListener(this);
        btnManualRetrieval = view.findViewById(R.id.button_dispense_manual_retrieval);
        btnManualRetrieval.setOnClickListener(this);
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
        Bundle bundle = this.getArguments();

        if (v == btnRetry)
        {
            bundle.putString("fragmentName", "DispenseMedsFragment");
            mListener.onButtonClicked(bundle);
        }

        if(v == btnManualRetrieval)
        {
            /*
             * Need fragment and sensor GPIO for this.
             * - wait for sensor (door opens)
             * - wait for sensor (door close)
             */
            TinyGDriver.getInstance().setMoveBinListener(
                    new OnRotateCompleteListener() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "Manual Dispense is finished!");
                            //do any calculations here to update the remaining amount for this medication
                            medication.setMedication_quantity(medication.getMedication_quantity() - dispenseAmount);
                            // update db with new quantity
                            DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
                            db.updateMedication(medication);

                            Bundle bundle = createBundle("TakeMedsFragment");
                            mListener.onButtonClicked(bundle);
                        }
                    });
            TinyGDriver.getInstance().doMoveBin4User(medication.getMedication_location());
        }
    }

    private Bundle createBundle(String fragmentName)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEarlyDispense", isEarlyDispense);
        bundle.putBoolean("isFromSchedule", isFromSchedule);
        bundle.putSerializable("user", user);
        bundle.putSerializable("medication", medication);
        bundle.putString("fragmentName", fragmentName);
        return bundle;
    }
}
