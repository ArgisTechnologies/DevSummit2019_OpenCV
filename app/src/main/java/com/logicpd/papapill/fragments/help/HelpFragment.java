package com.logicpd.papapill.fragments.help;

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
 * Fragment for main Help menu
 *
 * @author alankilloren
 */
public class HelpFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Help";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnTraining, btnLegal, btnQuick, btnWarranty, btnInstruct, btnUpdate;
    private OnButtonClickListener mListener;
    private TextView tvTitle;
    private int taps = 0;

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/

    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnTraining = view.findViewById(R.id.button_training_videos);
        btnTraining.setOnClickListener(this);
        btnLegal = view.findViewById(R.id.button_legal_notices);
        btnLegal.setOnClickListener(this);
        btnQuick = view.findViewById(R.id.button_quick_start);
        btnQuick.setOnClickListener(this);
        btnWarranty = view.findViewById(R.id.button_warranty);
        btnWarranty.setOnClickListener(this);
        btnInstruct = view.findViewById(R.id.button_instructions);
        btnInstruct.setOnClickListener(this);
        btnUpdate = view.findViewById(R.id.button_updates);
        btnUpdate.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);

        //TODO FOR DEVS ONLY - remove this in actual release
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taps == 2) {
                    taps = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("fragmentName", "DeveloperTestFragment");
                    mListener.onButtonClicked(bundle);
                } else {
                    taps += 1;
                    Log.i(AppConstants.TAG, "Tap: " + taps);
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
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnTraining) {
            bundle.putString("fragmentName", "TrainingVideosFragment");
        }
        if (v == btnQuick) {
            bundle.putString("fragmentName", "QuickStartFragment");
        }
        if (v == btnInstruct) {
            bundle.putString("fragmentName", "InstructionsFragment");
        }
        if (v == btnLegal) {
            bundle.putString("fragmentName", "LegalNoticesFragment");
        }
        if (v == btnWarranty) {
            bundle.putString("fragmentName", "WarrantyFragment");
        }
        if (v == btnUpdate) {
            bundle.putString("fragmentName", "CheckUpdatesFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
