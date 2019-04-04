package com.logicpd.papapill.fragments.user_settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Fragment for User Settings...Change User Pin
 *
 * @author alankilloren
 */
public class UserPinFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "User PIN";

    private LinearLayout backButton, homeButton;
    private Button btnNext, btnForgot;
    private EditText etUserPIN;
    private TextView tvTitle;
    private OnButtonClickListener mListener;
    private String username;
    private User user;
    private DatabaseHelper db;
    private String authFragment;
    private int authAttempts = 1;

    public UserPinFragment() {
        // Required empty public constructor
    }

    public static UserPinFragment newInstance() {
        return new UserPinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings_user_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        db = DatabaseHelper.getInstance(getActivity());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = db.getUserByName(bundle.getString("username"));
            username = user.getUsername();
            tvTitle.setText("ENTER " + username + "'S USER PIN");

            if (bundle.containsKey("authFragment")) {
                authFragment = bundle.getString("authFragment");
            }
        }
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        TextUtils.disableButton(btnNext);
        btnForgot = view.findViewById(R.id.button_forgot_pin);
        btnForgot.setOnClickListener(this);

        etUserPIN = view.findViewById(R.id.edittext_user_pin);
        tvTitle = view.findViewById(R.id.textview_title);
        etUserPIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start > 2) {
                    TextUtils.enableButton(btnNext);
                } else {
                    TextUtils.disableButton(btnNext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etUserPIN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etUserPIN.getText().length() == 4 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        etUserPIN.setFocusableInTouchMode(true);
        etUserPIN.requestFocus();
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
            bundle.putString("removeAllFragmentsUpToCurrent", "UserSettingsFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnForgot) {
            bundle.putSerializable("user", user);
            bundle.putString("fragmentName", "RecoverUserPinFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnNext) {
            if (etUserPIN.getText().toString().length() == 4 && etUserPIN.getText().toString().equals(user.getPin())) {
                user.setPin(etUserPIN.getText().toString());
                bundle.putSerializable("user", user);
                bundle.putString("fragmentName", authFragment);
                bundle.putBoolean("removeFragment", true);
                mListener.onButtonClicked(bundle);
            } else {
                bundle.putInt("authAttempts", authAttempts);
                if (authAttempts < 3) {
                    authAttempts += 1;
                }
                bundle.putString("fragmentName", "IncorrectUserPinFragment");
                mListener.onButtonClicked(bundle);
            }
            etUserPIN.setText("");
        }
    }
}