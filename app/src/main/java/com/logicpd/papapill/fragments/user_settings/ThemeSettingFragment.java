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
 * Fragment for User Settings...Color Theme
 *
 * @author alankilloren
 */
public class ThemeSettingFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Theme Settings";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnTheme1, btnTheme2, btnTheme3, btnTheme4, btnDone;
    private int currentSetting = 0;//default
    private User user;
    private DatabaseHelper db;

    public ThemeSettingFragment() {
        // Required empty public constructor
    }

    public static ThemeSettingFragment newInstance() {
        return new ThemeSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings_color_theme, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            currentSetting = user.getTheme();
            switch (currentSetting) {
                case 0:
                    btnTheme1.setSelected(true);
                    break;
                case 1:
                    btnTheme2.setSelected(true);
                    break;
                case 2:
                    btnTheme3.setSelected(true);
                    break;
                case 3:
                    btnTheme4.setSelected(true);
                    break;
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnTheme1 = view.findViewById(R.id.button_sunrise);
        btnTheme1.setOnClickListener(this);
        btnTheme2 = view.findViewById(R.id.button_forrest);
        btnTheme2.setOnClickListener(this);
        btnTheme3 = view.findViewById(R.id.button_ocean);
        btnTheme3.setOnClickListener(this);
        btnTheme4 = view.findViewById(R.id.button_frost);
        btnTheme4.setOnClickListener(this);
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
        if (v == btnTheme1) {
            btnTheme1.setSelected(true);
            btnTheme2.setSelected(false);
            btnTheme3.setSelected(false);
            btnTheme4.setSelected(false);
            currentSetting = 0;

        }
        if (v == btnTheme2) {
            btnTheme1.setSelected(false);
            btnTheme2.setSelected(true);
            btnTheme3.setSelected(false);
            btnTheme4.setSelected(false);
            currentSetting = 1;
        }
        if (v == btnTheme3) {
            btnTheme1.setSelected(false);
            btnTheme2.setSelected(false);
            btnTheme3.setSelected(true);
            btnTheme4.setSelected(false);
            currentSetting = 2;
        }
        if (v == btnTheme4) {
            btnTheme1.setSelected(false);
            btnTheme2.setSelected(false);
            btnTheme3.setSelected(false);
            btnTheme4.setSelected(true);
            currentSetting = 3;
        }
        if (v == btnDone) {
            user.setTheme(currentSetting);
            int returnVal = db.updateUser(user);
            bundle.putSerializable("user", user);
            bundle.putString("removeAllFragmentsUpToCurrent", "UserSettingsFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}