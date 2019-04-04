package com.logicpd.papapill.fragments.my_medication;

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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

public class MyMedsUserPinFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "PIN";

    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private EditText etUserPIN;
    private OnButtonClickListener mListener;
    private User user;
    private String authFragment;
    private TextView tvTitle;

    public MyMedsUserPinFragment() {
        // Required empty public constructor
    }

    public static MyMedsUserPinFragment newInstance() {
        return new MyMedsUserPinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_user_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            tvTitle.setText("ENTER " + user.getUsername() + "'S USER PIN");
            if (bundle.containsKey("authFragment")) {
                authFragment = bundle.getString("authFragment");
            }
        }
    }

    private void setupViews(View view) {
        tvTitle = view.findViewById(R.id.textview_title);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        btnNext.setVisibility(View.GONE);
        etUserPIN = view.findViewById(R.id.edittext_enter_user_pin);
        etUserPIN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etUserPIN.getText().length() > 3 && actionId == EditorInfo.IME_ACTION_GO) {
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
            bundle.putString("removeAllFragmentsUpToCurrent", "MyMedicationFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnNext) {
            if (etUserPIN.getText().toString().length() == 4) {
                if (etUserPIN.getText().toString().equals(user.getPin())) {
                    bundle.putSerializable("user", user);
                    bundle.putBoolean("removeFragment", true);
                    bundle.putString("fragmentName", authFragment);
                    mListener.onButtonClicked(bundle);
                } else {
                    TextUtils.showToast(getActivity(), "Invalid PIN entered");
                }

            } else {
                TextUtils.showToast(getActivity(), "PIN must be 4 numbers");
            }
        }
    }
}
