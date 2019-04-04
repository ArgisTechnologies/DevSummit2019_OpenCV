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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class VerifyContactInfoFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Veryify Contact Info";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private TextView tvSummary;
    private Button btnEdit, btnNext, btnDelete;
    private Contact contact;
    private DatabaseHelper db;
    private boolean isEditMode = false;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private boolean isFromChangePIN = false;
    private User user;

    public VerifyContactInfoFragment() {
        // Required empty public constructor
    }

    public static VerifyContactInfoFragment newInstance() {
        return new VerifyContactInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_verify_contact_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("isEditMode")) {
                if (bundle.getBoolean("isEditMode")) {
                    isEditMode = true;
                }
            }
            user = (User) bundle.getSerializable("user");
            isFromNotifications = bundle.getBoolean("isFromNotifications");
            isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            isFromChangePIN = bundle.getBoolean("isFromChangePIN");
            contact = (Contact) bundle.getSerializable("contact");
            StringBuilder sb = new StringBuilder();
            sb.append("NAME: " + contact.getName());
            if (contact.getTextNumber() != null && !contact.getTextNumber().isEmpty()) {
                sb.append("\nTEXT MESSAGE: " + contact.getTextNumber());
            }
            if (contact.getVoiceNumber() != null && !contact.getVoiceNumber().isEmpty()) {
                sb.append("\nVOICE MESSAGE: " + contact.getVoiceNumber());
            }
            if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
                sb.append("\nEMAIL: " + contact.getEmail());
            }
            tvSummary.setText(sb.toString());
            if (isEditMode) {
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnEdit = view.findViewById(R.id.button_edit_contact_info);
        btnEdit.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        tvSummary = view.findViewById(R.id.textview_verify_contact_info);
        btnDelete = view.findViewById(R.id.button_delete_contact);
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
        if (v == btnNext) {
            // add to db
            int returnVal = db.addContact(contact);
            bundle.putSerializable("contact", contact);
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromNotifications", isFromNotifications);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putBoolean("isFromChangePIN", isFromChangePIN);
            bundle.putString("fragmentName", "ContactAddedFragment");
        }
        if (v == btnEdit) {
            bundle.putBoolean("isFromNotifications", isFromNotifications);
            bundle.putBoolean("isFromChangePIN", isFromChangePIN);
            bundle.putSerializable("contact", contact);
            bundle.putSerializable("user", user);
            bundle.putBoolean("removeFragment", true);
            bundle.putString("fragmentName", "ContactNameFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
