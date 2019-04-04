package com.logicpd.papapill.fragments.system_manager.manage_users;

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
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

public class AddUserFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Add New User";

    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private EditText etUsername;
    private OnButtonClickListener mListener;
    private User user;

    public AddUserFragment() {
        // Required empty public constructor
    }

    public static AddUserFragment newInstance() {
        return new AddUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_add_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (user != null)
                etUsername.setText(user.getUsername());
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
        etUsername = view.findViewById(R.id.edittext_add_user);
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(AppConstants.TAG, "in beforeTextChanged()");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.i(AppConstants.TAG, "in onTextChanged() - " + start);
                if (start > 0) {
                    TextUtils.enableButton(btnNext);
                } else {
                    TextUtils.disableButton(btnNext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(AppConstants.TAG, "in afterTextChanged()");
            }
        });
        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etUsername.getText().length() > 1 && actionId == EditorInfo.IME_ACTION_GO) {
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
            if (etUsername.getText().toString().length() > 1 && etUsername.getText().toString().length() < 11) {
                if (user == null) {
                    user = new User();
                }
                user.setUsername(etUsername.getText().toString());
                bundle.putSerializable("user", user);
                bundle.putBoolean("isFromAddNewUser", true);
                bundle.putString("fragmentName", "AddUserPinFragment");
                mListener.onButtonClicked(bundle);
            }
            etUsername.setText("");
        }
    }
}
