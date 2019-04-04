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
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Fragment for User Settings
 *
 * @author alankilloren
 */
public class UserSettingsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "User Settings";

    private LinearLayout backButton, homeButton;
    private Button btnFont, btnVoice, btnColor, btnPin;
    private OnButtonClickListener mListener;
    private User user;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    public static UserSettingsFragment newInstance() {
        return new UserSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }
    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);


        btnFont = view.findViewById(R.id.button_font_size);
        btnFont.setOnClickListener(this);
        btnVoice = view.findViewById(R.id.button_select_voice);
        btnVoice.setOnClickListener(this);
        btnColor = view.findViewById(R.id.button_color_theme);
        btnColor.setOnClickListener(this);
        btnPin = view.findViewById(R.id.button_user_pin);
        btnPin.setOnClickListener(this);
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
        if (v == btnColor) {
            bundle.putString("authFragment", "ThemeSettingFragment");
            bundle.putString("fragmentName", "SelectUserSettingsFragment");
            bundle.putSerializable("user", user);
        }
        if (v == btnFont) {
            bundle.putString("authFragment", "FontSettingFragment");
            bundle.putString("fragmentName", "SelectUserSettingsFragment");
            bundle.putSerializable("user", user);
        }
        if (v == btnVoice) {
            bundle.putString("authFragment", "VoiceSettingFragment");
            bundle.putString("fragmentName", "SelectUserSettingsFragment");
            bundle.putSerializable("user", user);
        }
        if (v == btnPin) {
            bundle.putString("authFragment", "ChangeUserPinFragment");
            bundle.putString("fragmentName", "SelectUserSettingsFragment");
            bundle.putSerializable("user", user);
        }
        mListener.onButtonClicked(bundle);

    }
}