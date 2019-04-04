package com.logicpd.papapill.fragments.power_on;

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
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SetSystemKeyFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SetSystemKeyFragment";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    OnButtonClickListener mListener;
    Button btnOk;
    EditText etSystemKey;
    TextView tvTitle;

    public SetSystemKeyFragment() {
        // Required empty public constructor
    }

    public static SetSystemKeyFragment newInstance() {
        return new SetSystemKeyFragment();
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
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //TODO - handle passed in bundle
        }

        tvTitle.setText("SET SYSTEM KEY");

    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        homeButton.setVisibility(View.GONE);
        btnOk = view.findViewById(R.id.button_ok);
        btnOk.setOnClickListener(this);
        btnOk.setVisibility(View.GONE);
        etSystemKey = view.findViewById(R.id.edittext_system_key);
        etSystemKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (etSystemKey.getText().length() > 1 && actionId == EditorInfo.IME_ACTION_GO) {
                    btnOk.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        TextUtils.showKeyboard(getActivity(), etSystemKey);

        tvTitle = view.findViewById(R.id.textview_title);
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
        if (v == btnOk) {
            if (etSystemKey.getText().toString().length() > 3) {
                bundle.putString("system_key", etSystemKey.getText().toString());
                bundle.putBoolean("isFromSetup", true);
                bundle.putString("fragmentName", "VerifySystemKeyFragment");
                mListener.onButtonClicked(bundle);
            } else {
                TextUtils.showToast(getActivity(), "Please enter a valid system key");
            }
        }
    }
}