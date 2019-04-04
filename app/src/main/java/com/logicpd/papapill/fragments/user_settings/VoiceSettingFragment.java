package com.logicpd.papapill.fragments.user_settings;

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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Fragment for User Settings...Select Voice
 *
 * @author alankilloren
 */
public class VoiceSettingFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Voice Settings";

    private LinearLayout backButton, homeButton;
    private Button btnFemale1, btnFemale2, btnMale1, btnMale2, btnDone;
    private OnButtonClickListener mListener;
    private int currentSetting = 0;//default
    private User user;
    private DatabaseHelper db;

    public VoiceSettingFragment() {
        // Required empty public constructor
    }

    public static VoiceSettingFragment newInstance() {
        return new VoiceSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings_select_voice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            currentSetting = user.getVoice();
            switch (currentSetting) {
                case 0:
                    btnFemale1.setSelected(true);
                    break;
                case 1:
                    btnFemale2.setSelected(true);
                    break;
                case 2:
                    btnMale1.setSelected(true);
                    break;
                case 3:
                    btnMale2.setSelected(true);
                    break;
            }
        }

    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnFemale1 = view.findViewById(R.id.button_female1);
        btnFemale1.setOnClickListener(this);
        btnFemale2 = view.findViewById(R.id.button_female2);
        btnFemale2.setOnClickListener(this);
        btnMale1 = view.findViewById(R.id.button_male1);
        btnMale1.setOnClickListener(this);
        btnMale2 = view.findViewById(R.id.button_male2);
        btnMale2.setOnClickListener(this);
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
            bundle.putString("removeAllFragmentsUpToCurrent", "UserSettingsFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnFemale1) {
            btnFemale2.setSelected(false);
            btnMale1.setSelected(false);
            btnMale2.setSelected(false);
            btnFemale1.setSelected(true);
            currentSetting = 0;
        }
        if (v == btnFemale2) {
            btnFemale2.setSelected(true);
            btnMale1.setSelected(false);
            btnMale2.setSelected(false);
            btnFemale1.setSelected(false);
            currentSetting = 1;
        }
        if (v == btnMale1) {
            btnFemale2.setSelected(false);
            btnMale1.setSelected(true);
            btnMale2.setSelected(false);
            btnFemale1.setSelected(false);
            currentSetting = 2;
        }
        if (v == btnMale2) {
            btnFemale2.setSelected(false);
            btnMale1.setSelected(false);
            btnMale2.setSelected(true);
            btnFemale1.setSelected(false);
            currentSetting = 3;
        }
        if (v == btnDone) {
            user.setVoice(currentSetting);
            int returnVal = db.updateUser(user);
            bundle.putString("removeAllFragmentsUpToCurrent", "UserSettingsFragment");
            bundle.putSerializable("user", user);
            mListener.onButtonClicked(bundle);
        }
    }
}
