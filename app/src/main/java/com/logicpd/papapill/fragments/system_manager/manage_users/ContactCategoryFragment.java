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
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class ContactCategoryFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "ContactCategoryFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnMyself, btnFamily, btnCaregiver, btnNext;
    private User user;
    private TextView tvContactName;
    private Contact contact;
    private DatabaseHelper db;
    private boolean isFromAddNewUser;

    public ContactCategoryFragment() {
        // Required empty public constructor
    }

    public static ContactCategoryFragment newInstance() {
        return new ContactCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_contact_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            contact = (Contact) bundle.getSerializable("contact");
            switch (contact.getRelationship()) {
                case 1://myself
                    btnMyself.setSelected(true);
                    btnCaregiver.setSelected(false);
                    btnFamily.setSelected(false);
                    break;
                case 2://family member
                    btnMyself.setSelected(false);
                    btnCaregiver.setSelected(false);
                    btnFamily.setSelected(true);
                    break;
                case 3://caregiver
                    btnMyself.setSelected(false);
                    btnCaregiver.setSelected(true);
                    btnFamily.setSelected(false);
                    break;
            }
            tvContactName.setText(contact.getName());
            if (bundle.containsKey("isFromAddNewUser")) {
                if (bundle.getBoolean("isFromAddNewUser")) {
                    isFromAddNewUser = true;
                }
            }
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvContactName = view.findViewById(R.id.textview_contact_name);
        btnMyself = view.findViewById(R.id.button_myself);
        btnMyself.setOnClickListener(this);
        btnFamily = view.findViewById(R.id.button_family);
        btnFamily.setOnClickListener(this);
        btnCaregiver = view.findViewById(R.id.button_caregiver);
        btnCaregiver.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
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
        if (v == btnMyself) {
            contact.setRelationship(1);
            btnMyself.setSelected(true);
            btnCaregiver.setSelected(false);
            btnFamily.setSelected(false);
        }
        if (v == btnFamily) {
            contact.setRelationship(2);
            btnMyself.setSelected(false);
            btnCaregiver.setSelected(false);
            btnFamily.setSelected(true);
        }
        if (v == btnCaregiver) {
            contact.setRelationship(3);
            btnMyself.setSelected(false);
            btnCaregiver.setSelected(true);
            btnFamily.setSelected(false);
        }
        if (v == btnNext) {
            if (contact.getRelationship() > 0) {
                bundle.putSerializable("contact", contact);
                bundle.putSerializable("user", user);
                bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
                bundle.putString("fragmentName", "NotificationSettingsMessageFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please select a category");
            }
        }
    }
}
