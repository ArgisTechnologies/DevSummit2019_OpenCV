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
import android.widget.SeekBar;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Fragment for User Settings...Screen Brightness
 *
 * @author alankilloren
 */
public class ScreenSettingFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Screen Settings";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnDone;
    private SeekBar seekBar;
    private User user;
    private DatabaseHelper db;

    public ScreenSettingFragment() {
        // Required empty public constructor
    }

    public static ScreenSettingFragment newInstance() {
        return new ScreenSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings_screen_brightness, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (user.getScreenBrightness() == 0) {
                seekBar.setProgress(128);//default
            } else {
                seekBar.setProgress(user.getScreenBrightness());
            }
        }

    }


    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        seekBar = view.findViewById(R.id.seekbar_brightness);
        seekBar.setMax(255);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                //TODO The above line does not work on Android Things as there is currently no support for controlling displays from the system level
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnDone) {
            // save value to db
            user.setScreenBrightness(seekBar.getProgress());
            int returnVal = db.updateUser(user);
            bundle.putSerializable("user", user);
            backButton.performClick();
        }
    }
}
