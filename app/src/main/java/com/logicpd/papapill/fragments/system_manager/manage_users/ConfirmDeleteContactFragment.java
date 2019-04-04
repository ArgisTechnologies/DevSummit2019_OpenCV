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
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

public class ConfirmDeleteContactFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private Button btnCancel, btnDelete;
    private OnButtonClickListener mListener;
    private DatabaseHelper db;
    private Contact contact;

    public ConfirmDeleteContactFragment() {
        // Required empty public constructor
    }

    public static ConfirmDeleteContactFragment newInstance() {
        return new ConfirmDeleteContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_confirm_delete_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        db = DatabaseHelper.getInstance(getActivity());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            contact = (Contact) bundle.getSerializable("contact");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(this);
        btnDelete = view.findViewById(R.id.button_delete_contact);
        btnDelete.setOnClickListener(this);
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
        if (v == btnCancel) {
            backButton.performClick();
        }
        if (v == btnDelete) {
            // delete contact & notification settings
            int returnVal = db.deleteContact(contact);
            if (returnVal == 1) {
                db.deleteNotificationSettingsForContact(contact);
                bundle.putString("fragmentName", "ContactDeletedFragment");
                mListener.onButtonClicked(bundle);
            } else {
                // problem deleting contact
                TextUtils.showToast(getActivity(), "Problem deleting contact");
            }
        }
    }
}
