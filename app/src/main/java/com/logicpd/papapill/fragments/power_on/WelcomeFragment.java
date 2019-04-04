package com.logicpd.papapill.fragments.power_on;

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
 * Blank fragment template
 *
 * @author alankilloren
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Welcome";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnStartTour, btnSetup;
    private OnButtonClickListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_power_on_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/

        //TODO disabling until done
        TextUtils.disableButton(btnStartTour);
    }


    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        btnStartTour = view.findViewById(R.id.button_start_tour);
        btnStartTour.setOnClickListener(this);
        btnSetup = view.findViewById(R.id.button_start_setup);
        btnSetup.setOnClickListener(this);
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
        if (v == btnStartTour) {
            bundle.putString("fragmentName", "TrainingFragment");
        }
        if (v == btnSetup) {
            bundle.putString("fragmentName", "UserAgreementFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
