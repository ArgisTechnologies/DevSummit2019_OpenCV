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
import com.logicpd.papapill.utils.TextUtils;

public class NotificationPreferencesFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Contact Prefs";

    private LinearLayout backButton, homeButton;
    private TextView tvContactName;
    private Button btnText, btnVoice, btnEmail, btnNext;
    private boolean isTextSelected, isVoiceSelected, isEmailSelected;
    private OnButtonClickListener mListener;
    private Contact contact;
    private boolean isEditMode = false;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private boolean isFromChangePIN = false;
    private User user;

    public NotificationPreferencesFragment() {
        // Required empty public constructor
    }

    public static NotificationPreferencesFragment newInstance() {
        return new NotificationPreferencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_notification_prefs, container, false);
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
            isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            isFromNotifications = bundle.getBoolean("isFromNotifications");
            isFromChangePIN = bundle.getBoolean("isFromChangePIN");
            contact = (Contact) bundle.getSerializable("contact");

            tvContactName.setText(contact.getName());
            if (contact.isTextNumberSelected()) {
                btnText.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
                isTextSelected = true;
            } else {
                btnText.setBackgroundResource(R.drawable.button_selector);
                isTextSelected = false;
            }
            if (contact.isVoiceNumberSelected()) {
                btnVoice.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
                isVoiceSelected = true;
            } else {
                btnVoice.setBackgroundResource(R.drawable.button_selector);
                isVoiceSelected = false;
            }
            if (contact.isEmailSelected()) {
                btnEmail.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
                isEmailSelected = true;
            } else {
                btnEmail.setBackgroundResource(R.drawable.button_selector);
                isEmailSelected = false;
            }
        }

    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvContactName = view.findViewById(R.id.textview_contact_name);
        btnText = view.findViewById(R.id.button_text_message);
        btnText.setOnClickListener(this);
        btnVoice = view.findViewById(R.id.button_voice_message);
        btnVoice.setOnClickListener(this);
        btnEmail = view.findViewById(R.id.button_email_message);
        btnEmail.setOnClickListener(this);

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
        if (v == btnText) {
            if (!isTextSelected) {
                btnText.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
                isTextSelected = true;
            } else {
                btnText.setBackgroundResource(R.drawable.button_selector);
                isTextSelected = false;
            }

        }
        if (v == btnVoice) {
            if (!isVoiceSelected) {
                btnVoice.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
                isVoiceSelected = true;
            } else {
                btnVoice.setBackgroundResource(R.drawable.button_selector);
                isVoiceSelected = false;

            }

        }
        if (v == btnEmail) {
            if (!isEmailSelected) {
                btnEmail.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
                isEmailSelected = true;
            } else {
                btnEmail.setBackgroundResource(R.drawable.button_selector);
                isEmailSelected = false;
            }
        }
        if (v == btnNext) {
            contact.setTextNumberSelected(isTextSelected);
            contact.setVoiceNumberSelected(isVoiceSelected);
            contact.setEmailSelected(isEmailSelected);
            bundle.putSerializable("contact", contact);
            bundle.putBoolean("isEditMode", isEditMode);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putBoolean("isFromNotifications", isFromNotifications);
            bundle.putBoolean("isFromChangePIN", isFromChangePIN);
            bundle.putSerializable("user", user);
            if (isTextSelected) {
                bundle.putString("fragmentName", "ContactTextNumberFragment");
                mListener.onButtonClicked(bundle);
            } else if (isVoiceSelected) {
                bundle.putString("fragmentName", "ContactVoiceNumberFragment");
                mListener.onButtonClicked(bundle);
            } else if (isEmailSelected) {
                bundle.putString("fragmentName", "ContactEmailAddressFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please select a method first");
            }
        }
    }
}
