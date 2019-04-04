package com.logicpd.papapill.fragments.system_manager.system_settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.adapters.WifiListAdapter;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;
import com.logicpd.papapill.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fragment for System Manager...System Settings...Connect to Wireless Network...WiFi
 *
 * @author alankilloren
 */
public class SelectWifiNetworkFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private WifiManager wifiManager;
    private WifiListAdapter adapter;
    private RecyclerView recyclerView;
    private Button btnCancel;
    private List<String> scanList;
    private RecyclerView.LayoutManager mLayoutManager;
    private PreferenceUtils prefs;
    private String selectedSSID;
    private boolean isAuthenticated;
    private SupplicantState previousState;
    private ProgressBar progressBar;
    private boolean isFromSetup;

    public SelectWifiNetworkFragment() {
        // Required empty public constructor
    }

    public static SelectWifiNetworkFragment newInstance() {
        return new SelectWifiNetworkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_settings_select_wifi, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PreferenceUtils(getActivity());
        scanList = new ArrayList<>();
        setupViews(view);
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        getActivity().registerReceiver(WifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();

        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("isFromSetup")) {
                isFromSetup = bundle.getBoolean("isFromSetup");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(WifiScanReceiver);
    }

    private final BroadcastReceiver WifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                progressBar.setVisibility(View.GONE);
                List<ScanResult> mScanResults = wifiManager.getScanResults();

                scanList.clear();

                for (ScanResult result : mScanResults) {
                    if (!scanList.contains(result.SSID)) {
                        scanList.add(result.SSID);
                    }
                }
                cleanEntries();
                adapter.notifyDataSetChanged();
                Log.i(AppConstants.TAG, "Wifi scan results received");
            }
        }
    };

    private void cleanEntries() {
        //strip out entries that don't contain a SSID
        for (Iterator<String> iter = scanList.listIterator(); iter.hasNext(); ) {
            String ssid = iter.next();
            if (ssid.equals("")) {
                iter.remove();
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView = view.findViewById(R.id.recyclerview_wifi);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        adapter = new WifiListAdapter(scanList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new WifiListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                Log.i(AppConstants.TAG, "Wifi ID " + scanList.get(position) + " tapped on");
                selectedSSID = scanList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("ssid", selectedSSID);
                bundle.putString("fragmentName", "WifiPasswordFragment");
                mListener.onButtonClicked(bundle);
            }
        });

        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);

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
    }
}