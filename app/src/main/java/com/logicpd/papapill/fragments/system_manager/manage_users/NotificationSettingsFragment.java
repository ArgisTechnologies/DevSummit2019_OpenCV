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
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class NotificationSettingsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "NotificationSettingsFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnPerson, btnTrigger;
    private OnButtonClickListener mListener;

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    public static NotificationSettingsFragment newInstance() {
        return new NotificationSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_notification_settings, container, false);
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

        btnPerson = view.findViewById(R.id.button_notification_person);
        btnPerson.setOnClickListener(this);
        btnTrigger = view.findViewById(R.id.button_notification_event);
        btnTrigger.setOnClickListener(this);
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
        if (v == btnPerson) {
            bundle.putString("fragmentName", "SelectContactEditNotificationFragment");
            //TODO Do I need to pass something in here to indicate this is based on a contact?
        }
        if (v == btnTrigger) {
            bundle.putString("fragmentName", "EditNotificationSettingsFragment");
            //TODO Do I need to pass something in here to indicate this is based on a trigger event?
        }

        mListener.onButtonClicked(bundle);
    }
}
