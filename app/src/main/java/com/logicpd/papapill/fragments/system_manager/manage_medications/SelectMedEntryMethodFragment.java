package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SelectMedEntryMethodFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectMedEntryMethodFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private User user;
    private Button btnBarcode, btnManual;

    public SelectMedEntryMethodFragment() {
        // Required empty public constructor
    }

    public static SelectMedEntryMethodFragment newInstance() {
        return new SelectMedEntryMethodFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selectmedentrymethod, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("user")) {
                user = (User) bundle.getSerializable("user");
            }
        }

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnBarcode = view.findViewById(R.id.button_barcode);
        btnBarcode.setOnClickListener(this);
        btnManual = view.findViewById(R.id.button_manual);
        btnManual.setOnClickListener(this);
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

        if (v == btnBarcode) {
            bundle.putString("fragmentName", "ScanBarCodeFragment");
            bundle.putSerializable("user", user);
        }

        if (v == btnManual) {
            bundle.putString("fragmentName", "ImportantMessageFragment");
            bundle.putSerializable("user", user);
        }
        mListener.onButtonClicked(bundle);
    }
}