package com.logicpd.papapill.fragments.system_manager.manage_users;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

public class ContactNameFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "New Contact Name";

    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private EditText etContactName;
    private OnButtonClickListener mListener;
    private User user;
    private Contact contact;
    private boolean isEditMode = false;
    private TextView tvTitle;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private boolean isFromChangePIN = false;

    public ContactNameFragment() {
        // Required empty public constructor
    }

    public static ContactNameFragment newInstance() {
        return new ContactNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_contact_name, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("isEditMode")) {
                if (bundle.getBoolean("isEditMode")) {
                    isEditMode = true;
                }
            }
            contact = (Contact) bundle.getSerializable("contact");
            if (contact != null) {
                etContactName.setText(contact.getName());
            }
            tvTitle.setText("ENTER CONTACT'S FULL NAME");
            isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            isFromNotifications = bundle.getBoolean("isFromNotifications");
            isFromChangePIN = bundle.getBoolean("isFromChangePIN");
            user = (User) bundle.getSerializable("user");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        etContactName = view.findViewById(R.id.edittext_contact_name);
        tvTitle = view.findViewById(R.id.textview_title);
        etContactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etContactName.getText().length() > 2 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });
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
        if (v == btnNext) {
            if (etContactName.getText().toString().length() > 2) {
                bundle.putBoolean("isFromChangePIN", isFromChangePIN);
                bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
                bundle.putBoolean("isFromNotifications", isFromNotifications);
                bundle.putString("fragmentName", "NotificationPreferencesFragment");
                bundle.putSerializable("user", user);
                if (isEditMode) {
                    bundle.putBoolean("isEditMode", isEditMode);
                    contact.setName(etContactName.getText().toString());
                    bundle.putSerializable("contact", contact);
                    mListener.onButtonClicked(bundle);
                } else {
                    //add new
                    Contact contact = new Contact();
                    //contact.setUserid(user.getId());
                    contact.setName(etContactName.getText().toString());
                    bundle.putSerializable("contact", contact);
                    mListener.onButtonClicked(bundle);
                }
            } else {
                TextUtils.showToast(getActivity(), "Please enter a valid name");
            }


        }
    }
}
