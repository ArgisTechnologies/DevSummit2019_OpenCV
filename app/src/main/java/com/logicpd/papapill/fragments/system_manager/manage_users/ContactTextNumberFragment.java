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

public class ContactTextNumberFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Text Number";

    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private EditText etTextNumber;
    private Button btnNext;
    private Contact contact;
    private boolean isEditMode;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private boolean isFromNotificationSettingsAdapter = false;
    private boolean isFromChangePIN = false;
    private User user;

    public ContactTextNumberFragment() {
        // Required empty public constructor
    }

    public static ContactTextNumberFragment newInstance() {
        return new ContactTextNumberFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_contact_text_number, container, false);
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
            user = (User) bundle.getSerializable("user");
            contact = (Contact) bundle.getSerializable("contact");
            etTextNumber.setText(contact.getTextNumber());
            isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            isFromNotifications = bundle.getBoolean("isFromNotifications");
            isFromNotificationSettingsAdapter = bundle.getBoolean("isFromNotificationSettingsAdapter");
            isFromChangePIN = bundle.getBoolean("isFromChangePIN");
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        etTextNumber = view.findViewById(R.id.edittext_contact_text_number);
        etTextNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etTextNumber.getText().length() > 6 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });
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
        if (v == btnNext) {
            if (etTextNumber.getText().toString().length() > 6) {
                contact.setTextNumber(etTextNumber.getText().toString());
                bundle.putSerializable("contact", contact);
                bundle.putBoolean("isEditMode", isEditMode);
                bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
                bundle.putBoolean("isFromNotifications", isFromNotifications);
                bundle.putBoolean("isFromChangePIN", isFromChangePIN);
                bundle.putSerializable("user", user);
                if (contact.isVoiceNumberSelected()) {
                    bundle.putString("fragmentName", "ContactVoiceNumberFragment");
                } else if (contact.isEmailSelected()) {
                    bundle.putString("fragmentName", "ContactEmailAddressFragment");
                } else {
                    if (isEditMode) {
                        bundle.putString("fragmentName", "ContactInfoFragment");
                    } else {
                        bundle.putString("fragmentName", "VerifyContactInfoFragment");
                    }
                }
                mListener.onButtonClicked(bundle);

            } else {
                TextUtils.showToast(getActivity(), "Please enter a valid number");
            }
        }
    }
}
