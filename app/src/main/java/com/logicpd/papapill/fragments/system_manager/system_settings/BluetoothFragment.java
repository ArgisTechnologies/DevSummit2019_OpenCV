package com.logicpd.papapill.fragments.system_manager.system_settings;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.logicpd.papapill.R;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

import java.lang.reflect.Field;

import static com.logicpd.papapill.misc.AppConstants.TAG;

/**
 * Fragment for System Manager...System Settings...Pair Bluetooth Keyboard
 *
 * @author alankilloren
 */
public class BluetoothFragment extends Fragment implements View.OnClickListener {

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnCancel, btnSearch;
    private Switch bluetoothSwitch;
    private OnButtonClickListener mListener;
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothFragment() {
        // Required empty public constructor
    }

    public static BluetoothFragment newInstance() {
        return new BluetoothFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().registerReceiver(mAdapterStateChangeReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        return inflater.inflate(R.layout.fragment_system_settings_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "No Bluetooth device present!");
            TextUtils.showToast(getActivity(), "This device doesn't support Bluetooth");
        } else {
            //is Bluetooth ON or OFF?
            if (bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth is already enabled");
                bluetoothSwitch.setChecked(true);
            } else {
                Log.d(TAG, "Bluetooth is currently disabled - enabling...");
                bluetoothSwitch.setChecked(false);
                bluetoothAdapter.enable();
                //Log.d(TAG, "Bluetooth adapter enabled: " + bluetoothAdapter.isEnabled());
            }
        }
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mAdapterStateChangeReceiver);
    }


    private String getBluetoothMAC() {
        try {
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);
            Object btManagerService = mServiceField.get(bluetoothAdapter);
            if (btManagerService != null) {
                return (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);
        btnSearch = view.findViewById(R.id.button_search);
        btnSearch.setOnClickListener(this);
        bluetoothSwitch = view.findViewById(R.id.switch_bluetooth);
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 0);*/
                    bluetoothAdapter.enable();
                    Log.d(TAG, "Bluetooth enabled");

                } else {
                    bluetoothAdapter.disable();
                    Log.d(TAG, "Bluetooth disabled");
                }
            }
        });
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
        if (v == btnCancel) {
            backButton.performClick();
        }
        if (v == btnSearch) {
            bundle.putString("fragmentName", "BluetoothPairFragment");
            mListener.onButtonClicked(bundle);
        }
    }

    public BroadcastReceiver mAdapterStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Bluetooth Adapter enabled:  " + bluetoothAdapter.isEnabled());
            if (bluetoothAdapter.isEnabled()) {
                bluetoothSwitch.setChecked(true);
            }
        }
    };
}
