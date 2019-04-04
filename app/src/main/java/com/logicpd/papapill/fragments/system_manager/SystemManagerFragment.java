package com.logicpd.papapill.fragments.system_manager;

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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Fragment for System Manager
 *
 * @author alankilloren
 */
public class SystemManagerFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private Button btnManageUsers, btnManageMedications, btnSystemSettings;
    private OnButtonClickListener mListener;

    public SystemManagerFragment() {
        // Required empty public constructor
    }

    public static SystemManagerFragment newInstance() {
        return new SystemManagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);

        if (db.getUsers().size() == 0) {
            TextUtils.disableButton(btnManageMedications);
        }
    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnManageUsers = view.findViewById(R.id.button_manage_users);
        btnManageUsers.setOnClickListener(this);
        btnManageMedications = view.findViewById(R.id.button_manage_medications);
        btnSystemSettings = view.findViewById(R.id.button_system_settings);
        btnManageMedications.setOnClickListener(this);
        btnSystemSettings.setOnClickListener(this);
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
        if (v == btnManageUsers) {
            bundle.putString("fragmentName", "ManageUsersFragment");
        }
        if (v == btnSystemSettings) {
            bundle.putString("fragmentName", "SystemSettingsFragment");
        }
        if (v == btnManageMedications) {
            bundle.putString("fragmentName", "ManageMedsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}