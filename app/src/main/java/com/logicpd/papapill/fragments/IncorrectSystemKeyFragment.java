package com.logicpd.papapill.fragments;

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
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;

/**
 * IncorrectSystemKeyFragment
 *
 * @author alankilloren
 */
public class IncorrectSystemKeyFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "IncorrectSystemKeyFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnRetry, btnReset;
    private int authAttempts;

    public IncorrectSystemKeyFragment() {
        // Required empty public constructor
    }

    public static IncorrectSystemKeyFragment newInstance() {
        return new IncorrectSystemKeyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_key_incorrect, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("authAttempts")) {
                authAttempts = bundle.getInt("authAttempts");
            }
        }
        setupViews(view);

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        TextView tvContent = view.findViewById(R.id.textview_content);
        btnReset = view.findViewById(R.id.button_reset);
        btnReset.setOnClickListener(this);
        btnRetry = view.findViewById(R.id.button_try_again);
        btnRetry.setOnClickListener(this);

        if (authAttempts == 3) {
            btnRetry.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = btnReset.getLayoutParams();
            params.width = 250;
            btnReset.setLayoutParams(params);
            tvContent.setText("YOU HAVE ENTERED THE SYSTEM KEY INCORRECTLY 3 TIMES.\n\nYOU MUST RESET THE SYSTEM KEY NOW.");
        }
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
        if (v == btnRetry) {
            backButton.performClick();
        }
        if (v == btnReset) {
            bundle.putString("fragmentName", "RecoverSystemKeyFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}