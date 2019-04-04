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
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

public class ContactUpdatedFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private TextView tvContactName;
    private Button btnDone;
    private OnButtonClickListener mListener;
    private Contact contact;
    private boolean isEditMode;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private boolean isFromChangePIN = false;
    private User user;

    public ContactUpdatedFragment() {
        // Required empty public constructor
    }

    public static ContactUpdatedFragment newInstance() {
        return new ContactUpdatedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_contact_updated, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        backButton.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("isEditMode")) {
                if (bundle.getBoolean("isEditMode")) {
                    isEditMode = true;
                }
            }
            if (bundle.containsKey("isFromAddNewUser")) {
                if (bundle.getBoolean("isFromAddNewUser")) {
                    isFromAddNewUser = true;
                }
            }
            isFromNotifications = bundle.getBoolean("isFromNotifications");
            isFromChangePIN = bundle.getBoolean("isFromChangePIN");
            user = (User) bundle.getSerializable("user");
            contact = (Contact) bundle.getSerializable("contact");
            tvContactName.setText(contact.getName());
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        tvContactName = view.findViewById(R.id.textview_contact_name);
        btnDone = view.findViewById(R.id.button_ok);
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
            bundle.putString("fragmentName", "Back");
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnDone) {
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);

            if (isFromAddNewUser && !isFromNotifications || isFromChangePIN) {
                bundle.putString("removeAllFragmentsUpToCurrent", "SelectContactPinRecoveryFragment");
            } else if (isFromNotifications) {
                bundle.putString("removeAllFragmentsUpToCurrent", "NotificationContactsFragment");
            } else {
                bundle.putString("removeAllFragmentsUpToCurrent", "ContactListFragment");
            }
        }
        mListener.onButtonClicked(bundle);
    }
}
