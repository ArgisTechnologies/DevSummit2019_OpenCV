package com.logicpd.papapill.fragments;

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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Fragment for entering or changing system keys
 *
 * @author alankilloren
 */
public class SystemKeyFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "System Key";

    private LinearLayout backButton, homeButton;
    private Button btnOK, btnForgot;
    private EditText etSystemKey;
    private OnButtonClickListener mListener;
    private String currentKey;
    private String authFragment;
    private int authAttempts = 1;

    public SystemKeyFragment() {
        // Required empty public constructor
    }

    public static SystemKeyFragment newInstance() {
        return new SystemKeyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system_key, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
        currentKey = db.getSystemKey();

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
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
        btnForgot = view.findViewById(R.id.button_forgot_key);
        btnForgot.setOnClickListener(this);
        btnOK = view.findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        TextUtils.disableButton(btnOK);
        etSystemKey = view.findViewById(R.id.edittext_system_key);
        etSystemKey.requestFocus();
        etSystemKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(AppConstants.TAG, "in beforeTextChanged()");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.i(AppConstants.TAG, "in onTextChanged() - " + start);
                if (start > 2) {
                    TextUtils.enableButton(btnOK);
                } else {
                    TextUtils.disableButton(btnOK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(AppConstants.TAG, "in afterTextChanged()");
            }
        });
        etSystemKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etSystemKey.getText().length() > 1 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnOK.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        TextUtils.showKeyboard(getActivity(), etSystemKey);

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
        if (v == btnForgot) {
            bundle.putString("fragmentName", "RecoverSystemKeyFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnOK) {
            if (etSystemKey.getText().toString().length() == 4) {
                if (etSystemKey.getText().toString().equals(currentKey)) {
                    bundle.putBoolean("removeFragment", true);
                    bundle.putString("fromFragment", "SystemKeyFragment");
                    bundle.putString("fragmentName", authFragment);
                    mListener.onButtonClicked(bundle);
                } else {
                    bundle.putInt("authAttempts", authAttempts);
                    if (authAttempts < 3) {
                        authAttempts += 1;
                    }
                    bundle.putString("fragmentName", "IncorrectSystemKeyFragment");
                    mListener.onButtonClicked(bundle);
                }
            }
            etSystemKey.setText("");
        }
    }
}
