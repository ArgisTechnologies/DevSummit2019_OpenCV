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
 * Fragment for User Settings...Font Size
 *
 * @author alankilloren
 */
public class FontSettingFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Font Settings";

    private LinearLayout backButton, homeButton;
    private Button btnSmall, btnMedium, btnLarge, btnDone;
    private OnButtonClickListener mListener;
    private DatabaseHelper db;
    private User user;
    private int currentSetting = 0;//default;


    public FontSettingFragment() {
        // Required empty public constructor
    }

    public static FontSettingFragment newInstance() {
        return new FontSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings_font_size, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            currentSetting = user.getFontSize();
            switch (currentSetting) {
                case 0:
                    btnSmall.setSelected(true);
                    break;
                case 1:
                    btnMedium.setSelected(true);
                    break;
                case 2:
                    btnLarge.setSelected(true);
                    break;
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnSmall = view.findViewById(R.id.button_small);
        btnSmall.setOnClickListener(this);
        btnMedium = view.findViewById(R.id.button_medium);
        btnMedium.setOnClickListener(this);
        btnLarge = view.findViewById(R.id.button_large);
        btnLarge.setOnClickListener(this);
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
        if (v == btnSmall) {
            btnMedium.setSelected(false);
            btnLarge.setSelected(false);
            btnSmall.setSelected(true);
            currentSetting = 0;
        }
        if (v == btnMedium) {
            btnSmall.setSelected(false);
            btnLarge.setSelected(false);
            btnMedium.setSelected(true);
            currentSetting = 1;
        }
        if (v == btnLarge) {
            btnLarge.setSelected(true);
            btnMedium.setSelected(false);
            btnSmall.setSelected(false);
            currentSetting = 2;
        }
        if (v == btnDone) {
            user.setFontSize(currentSetting);
            int returnVal = db.updateUser(user);
            bundle.putSerializable("user", user);
            bundle.putString("removeAllFragmentsUpToCurrent", "UserSettingsFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
