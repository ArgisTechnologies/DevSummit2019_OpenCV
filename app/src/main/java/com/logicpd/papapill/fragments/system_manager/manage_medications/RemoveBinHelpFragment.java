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
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;

/**
 * RemoveBinHelpFragment
 *
 * @author alankilloren
 */
public class RemoveBinHelpFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "RemoveBinHelpFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnDone;

    public RemoveBinHelpFragment() {
        // Required empty public constructor
    }

    public static RemoveBinHelpFragment newInstance() {
        return new RemoveBinHelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_remove_bin_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
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
            backButton.performClick();
        }
    }
}