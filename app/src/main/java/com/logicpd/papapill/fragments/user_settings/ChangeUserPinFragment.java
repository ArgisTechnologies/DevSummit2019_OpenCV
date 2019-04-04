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
public class ChangeUserPinFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Change PIN";

    private LinearLayout backButton, homeButton;
    private Button btnNext, btnForgot;
    private EditText etUserPIN;
    private OnButtonClickListener mListener;
    private User user;
    private DatabaseHelper db;

    public ChangeUserPinFragment() {
        // Required empty public constructor
    }

    public static ChangeUserPinFragment newInstance() {
        return new ChangeUserPinFragment();
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
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
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
        TextUtils.disableButton(btnNext);
        btnForgot = view.findViewById(R.id.button_forgot_pin);
        btnForgot.setVisibility(View.INVISIBLE);
        etUserPIN = view.findViewById(R.id.edittext_user_pin);
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
        if (v == btnNext) {
            if (etUserPIN.getText().toString().length() == 4) {
                user.setPin(etUserPIN.getText().toString());
                // save to db
                int returnVal = db.updateUser(user);
                bundle.putSerializable("user", user);
                //bundle.putString("removeAllFragmentsUpToCurrent", "UserSettingsFragment");
                bundle.putBoolean("isFromChangePIN", true);
                bundle.putString("fragmentName", "SelectContactPinRecoveryFragment");
                mListener.onButtonClicked(bundle);
            }
            etUserPIN.setText("");
        }
    }
}