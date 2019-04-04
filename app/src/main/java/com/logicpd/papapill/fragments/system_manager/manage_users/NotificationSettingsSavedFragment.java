package com.logicpd.papapill.fragments.system_manager.manage_users;

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
 * Blank fragment template
 *
 * @author alankilloren
 */
public class NotificationSettingsSavedFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "NotificationSettingsSavedFragment";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnDone, btnAnother;
    private User user;
    private boolean isFromAddNewUser = false;

    public NotificationSettingsSavedFragment() {
        // Required empty public constructor
    }

    public static NotificationSettingsSavedFragment newInstance() {
        return new NotificationSettingsSavedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_notification_settings_saved, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (bundle.containsKey("isFromAddNewUser")) {
                isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            }
        }
    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        btnAnother = view.findViewById(R.id.button_another);
        btnAnother.setOnClickListener(this);
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
        if (v == btnDone) {
            if (isFromAddNewUser) {
                bundle.putSerializable("user", user);
                bundle.putString("fragmentName", "NewUserSetupCompleteFragment");
            } else {
                bundle.putString("fragmentName", "Home");
            }
        }
        if (v == btnAnother) {
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putString("removeAllFragmentsUpToCurrent", "NotificationContactsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
