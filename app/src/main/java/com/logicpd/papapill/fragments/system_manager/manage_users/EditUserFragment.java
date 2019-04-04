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

public class EditUserFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private TextView tvTitle;
    private Button btnChgName, btnNotifications;
    private User user;

    public EditUserFragment() {
        // Required empty public constructor
    }

    public static EditUserFragment newInstance() {
        return new EditUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_edit_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            tvTitle.setText("EDIT USER - " + user.getUsername());
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnChgName = view.findViewById(R.id.button_change_nickname);
        btnChgName.setOnClickListener(this);
        btnNotifications = view.findViewById(R.id.button_notifications);
        btnNotifications.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
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
        if (v == btnChgName) {
            bundle.putSerializable("user", user);
            bundle.putString("fragmentName", "EditUserNicknameFragment");
        }
        if (v == btnNotifications) {
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromAddNewUser", false);
            bundle.putString("fragmentName", "NotificationContactsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
