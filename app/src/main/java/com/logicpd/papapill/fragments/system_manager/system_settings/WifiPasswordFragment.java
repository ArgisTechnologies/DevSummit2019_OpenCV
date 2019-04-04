package com.logicpd.papapill.fragments.system_manager.system_settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logicpd.papapill.R;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.utils.PreferenceUtils;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class WifiPasswordFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "WifiPasswordFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnNext;
    private EditText etPassword;
    private String selectedSSID;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private boolean isAuthenticated;
    private SupplicantState previousState;
    private PreferenceUtils prefs;
    private boolean isFromSetup;
    private ProgressBar progressBar;

    public WifiPasswordFragment() {
        // Required empty public constructor
    }

    public static WifiPasswordFragment newInstance() {
        return new WifiPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_settings_wifi_password, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PreferenceUtils(getActivity());

        setupViews(view);
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedSSID = bundle.getString("ssid");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(WifiConnectReceiver, filter);

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        btnNext.setVisibility(View.GONE);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        etPassword = view.findViewById(R.id.edittext_wifi_password);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(AppConstants.TAG, "in beforeTextChanged()");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.i(AppConstants.TAG, "in onTextChanged() - " + start);
                if (start > 0) {
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(AppConstants.TAG, "in afterTextChanged()");
            }
        });
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etPassword.getText().length() > 1 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        TextUtils.showKeyboard(getActivity(), etPassword);

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

        if (v == btnNext) {
            if (etPassword.getText().toString().length() > 0) {
                String checkPassword = etPassword.getText().toString();
                connectWifi(checkPassword, selectedSSID);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                TextUtils.showToast(getActivity(), "Please enter a passkey first");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(WifiConnectReceiver);
    }

    private void connectWifi(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private final BroadcastReceiver WifiConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(AppConstants.TAG, "WifiConnectReceiver onReceive - " + action);
            if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {

                SupplicantState state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                wifiInfo = wifiManager.getConnectionInfo();

                Log.i(AppConstants.TAG, "WiFi State: " + state);
                switch (state) {
                    case DISCONNECTED:
                        if (previousState == SupplicantState.FOUR_WAY_HANDSHAKE || previousState == SupplicantState.GROUP_HANDSHAKE) {
                            //invalid key entered
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Invalid key entered!", Toast.LENGTH_SHORT).show();
                            isAuthenticated = false;
                            Log.d(AppConstants.TAG, "Invalid key entered!");
                        }
                        break;
                    case AUTHENTICATING:
                        isAuthenticated = false;
                        break;
                    case COMPLETED:
                        if (previousState == SupplicantState.FOUR_WAY_HANDSHAKE || previousState == SupplicantState.GROUP_HANDSHAKE) {
                            progressBar.setVisibility(View.GONE);
                            isAuthenticated = true;
//                            Toast.makeText(getActivity(), "Connected to " + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
                            String s = wifiInfo.getSSID().replaceAll("^\"|\"$", "");
                            prefs.setSSID(s);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isFromSetup", isFromSetup);
                            bundle.putString("fragmentName", "WifiConnectedFragment");
                            mListener.onButtonClicked(bundle);
                        }
                        break;
                }
                previousState = state;
            }
        }
    };
}