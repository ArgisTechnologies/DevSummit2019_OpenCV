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
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

public class VerifyUserPinFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private Button btnEdit, btnOK;
    private TextView tvUserInfo;
    private OnButtonClickListener mListener;
    private User user;
    private DatabaseHelper db;
    private boolean isFromAddNewUser, isFromChangePIN;

    public VerifyUserPinFragment() {
        // Required empty public constructor
    }

    public static VerifyUserPinFragment newInstance() {
        return new VerifyUserPinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_verify_pin_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //handle bundle
            user = (User) bundle.getSerializable("user");
            Contact contact = (Contact) bundle.getSerializable("contact");
            String sb = null;
            if (contact != null) {
                sb = "NEW USER PIN: " + user.getPin() +
                        "\nPIN RECOVERY CONTACT: " + contact.getName();
            }
            tvUserInfo.setText(sb);
            if (bundle.containsKey("isFromAddNewUser")) {
                isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            }
            if (bundle.containsKey("isFromChangePIN")) {
                isFromChangePIN = bundle.getBoolean("isFromChangePIN");
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnOK = view.findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        btnEdit = view.findViewById(R.id.button_edit);
        btnEdit.setOnClickListener(this);
        tvUserInfo = view.findViewById(R.id.textview_verify_user_info);
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
        if (v == btnOK) {
            db.updateUser(user);
            bundle.putBoolean("isFromChangePIN", isFromChangePIN);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putString("fragmentName", "UserPinChangedFragment");
            bundle.putSerializable("user", user);
        }
        if (v == btnEdit) {
            bundle.putString("removeAllFragmentsUpToCurrent", "UserPinFragment");
            bundle.putSerializable("user", user);
        }
        mListener.onButtonClicked(bundle);
    }
}
