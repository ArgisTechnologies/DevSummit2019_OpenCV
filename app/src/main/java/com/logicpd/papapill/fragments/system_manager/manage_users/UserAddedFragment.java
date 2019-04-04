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
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

public class UserAddedFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private TextView tvWelcome;
    private Button btnSkip, btnNotifications;
    private OnButtonClickListener mListener;
    private User user;

    public UserAddedFragment() {
        // Required empty public constructor
    }

    public static UserAddedFragment newInstance() {
        return new UserAddedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_user_added, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        backButton.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            tvWelcome.setText("WELCOME, " + user.getUsername() + "!");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        tvWelcome = view.findViewById(R.id.textview_welcome);
        btnSkip = view.findViewById(R.id.button_skip);
        btnSkip.setOnClickListener(this);

        btnNotifications = view.findViewById(R.id.button_notification_settings);
        btnNotifications.setOnClickListener(this);
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
        if (v == btnSkip) {
            bundle.putString("removeAllFragmentsUpToCurrent", "ManageUsersFragment");
        }
        if (v == btnNotifications) {
            bundle.putBoolean("isFromAddNewUser", true);
            bundle.putSerializable("user", user);
            bundle.putString("fragmentName", "NotificationContactsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}