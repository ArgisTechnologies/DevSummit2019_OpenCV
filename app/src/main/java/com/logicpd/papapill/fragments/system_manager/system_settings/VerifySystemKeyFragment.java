package com.logicpd.papapill.fragments.system_manager.system_settings;

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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.PreferenceUtils;

/**
 * Fragment for System Manager...System Settings...Change System Key...Verify
 *
 * @author alankilloren
 */
public class VerifySystemKeyFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnOK, btnEdit;
    private TextView tvSystemKey;
    private String systemKey;
    private DatabaseHelper db;
    private boolean isFromSetup;
    private PreferenceUtils prefs;

    public VerifySystemKeyFragment() {
        // Required empty public constructor
    }

    public static VerifySystemKeyFragment newInstance() {
        return new VerifySystemKeyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_settings_verify_key, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        prefs = new PreferenceUtils(getActivity());
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            systemKey = bundle.getString("system_key");
            tvSystemKey.setText(systemKey);
            if (bundle.containsKey("isFromSetup")) {
                isFromSetup = bundle.getBoolean("isFromSetup");
            }
        }
        if (isFromSetup) {
            homeButton.setVisibility(View.GONE);
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvSystemKey = view.findViewById(R.id.textview_verify_key);
        btnOK = view.findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        btnEdit = view.findViewById(R.id.button_edit_system_key);
        btnEdit.setOnClickListener(this);
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
        if (v == btnEdit) {
            backButton.performClick();
        }
        if (v == btnOK) {
            int returnVal = db.updateSystemKey(1, systemKey);
            if (isFromSetup) {
                //TODO is this a good place to send registration data to the cloud?

                // set preferences to isFirstTimeRun false
                prefs.setFirstTimeRun(false);
                bundle.putString("fragmentName", "Home");//TODO
            } else {
                bundle.putString("removeAllFragmentsUpToCurrent", "SystemSettingsFragment");
            }
            mListener.onButtonClicked(bundle);
        }
    }
}