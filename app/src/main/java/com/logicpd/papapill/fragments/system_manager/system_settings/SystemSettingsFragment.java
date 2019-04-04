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

import com.logicpd.papapill.R;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Fragment for System Manager...System Settings
 *
 * @author alankilloren
 */
public class SystemSettingsFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private Button btnWireless, btnKey, btnBluetooth, btnDateTime, btnCloud, btnAV;
    private OnButtonClickListener mListener;

    public SystemSettingsFragment() {
        // Required empty public constructor
    }

    public static SystemSettingsFragment newInstance() {
        return new SystemSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        //TODO - disabling these for now until I can get them working
        TextUtils.disableButton(btnDateTime);
        TextUtils.disableButton(btnBluetooth);
        TextUtils.disableButton(btnCloud);
        TextUtils.disableButton(btnAV);
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnWireless = view.findViewById(R.id.button_wireless);
        btnWireless.setOnClickListener(this);
        btnKey = view.findViewById(R.id.button_system_key);
        btnKey.setOnClickListener(this);
        btnBluetooth = view.findViewById(R.id.button_bluetooth);
        btnBluetooth.setOnClickListener(this);
        btnDateTime = view.findViewById(R.id.button_datetime);
        btnDateTime.setOnClickListener(this);
        btnCloud = view.findViewById(R.id.button_cloud);
        btnCloud.setOnClickListener(this);
        btnAV = view.findViewById(R.id.button_av);
        btnAV.setOnClickListener(this);
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
        if (v == btnWireless) {
            bundle.putString("fragmentName", "SystemKeyFragment");
            bundle.putString("authFragment", "SelectWifiNetworkFragment");
        }
        if (v == btnBluetooth) {
            bundle.putString("fragmentName", "SystemKeyFragment");
            bundle.putString("authFragment", "BluetoothFragment");
        }
        if (v == btnKey) {
            bundle.putString("fragmentName", "SystemKeyFragment");
            bundle.putString("authFragment", "ChangeSystemKeyFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
