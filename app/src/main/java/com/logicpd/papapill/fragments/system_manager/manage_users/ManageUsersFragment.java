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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

import java.util.List;

public class ManageUsersFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private Button btnAddUser, btnEditUser, btnDeleteUser, btnManageContacts;
    private OnButtonClickListener mListener;

    public ManageUsersFragment() {
        // Required empty public constructor
    }

    public static ManageUsersFragment newInstance() {
        return new ManageUsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());

        List<User> userList = db.getUsers();
        if (userList.size() == 0) {
            TextUtils.disableButton(btnEditUser);
            TextUtils.disableButton(btnDeleteUser);
            TextUtils.disableButton(btnManageContacts);
        }
        if (userList.size() == 2) {
            TextUtils.disableButton(btnAddUser);
        }
    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnAddUser = view.findViewById(R.id.button_add_user);
        btnEditUser = view.findViewById(R.id.button_edit_user);
        btnDeleteUser = view.findViewById(R.id.button_delete_user);
        btnAddUser.setOnClickListener(this);
        btnEditUser.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
        btnManageContacts = view.findViewById(R.id.button_manage_contacts);
        btnManageContacts.setOnClickListener(this);
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
            //go back to home
            bundle.putString("fragmentName", "Back");
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnAddUser) {
            bundle.putString("authFragment", "AddUserFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnEditUser) {
            bundle.putString("authFragment", "SelectEditUserFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnDeleteUser) {
            bundle.putString("authFragment", "SelectDeleteUserFragment");
            bundle.putString("fragmentName", "SystemKeyFragment");
        }
        if (v == btnManageContacts) {
            bundle.putBoolean("isFromAddNewUser", false);
            bundle.putBoolean("isFromNotifications", false);
            bundle.putString("fragmentName", "SystemKeyFragment");
            bundle.putString("authFragment", "ContactListFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
