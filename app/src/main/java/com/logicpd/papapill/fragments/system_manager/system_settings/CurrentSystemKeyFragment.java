package com.logicpd.papapill.fragments.system_manager.system_settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.utils.TextUtils;

/**
 * Fragment for System Manager...System Settings...Change System Key
 *
 * @author alankilloren
 */
public class CurrentSystemKeyFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backButton, homeButton;
    private TextView tvTitle;
    private Button btnOk;
    private EditText etSystemKey;
    private OnButtonClickListener mListener;
    private DatabaseHelper db;
    private String currentKey;

    public CurrentSystemKeyFragment() {
        // Required empty public constructor
    }

    public static CurrentSystemKeyFragment newInstance() {
        return new CurrentSystemKeyFragment();
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
        db = DatabaseHelper.getInstance(getActivity());

        currentKey = db.getSystemKey();
        setupViews(view);

        tvTitle.setText("PLEASE RE-ENTER SYSTEM KEY");
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/

    }


    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        etSystemKey = view.findViewById(R.id.edittext_system_key);

        btnOk = view.findViewById(R.id.button_ok);
        btnOk.setOnClickListener(this);

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
                if (etSystemKey.getText().toString().equals(currentKey)) {
                    bundle.putString("fragmentName", "ChangeSystemKeyFragment");
                    bundle.putBoolean("removeFragment", true);
                    bundle.putString("system_key", etSystemKey.getText().toString());
                    mListener.onButtonClicked(bundle);
                } else {
                    TextUtils.showToast(getActivity(), "Invalid key entered");
                }

            } else {
                TextUtils.showToast(getActivity(), "Please enter at least 4 numbers");
            }
        }
    }
}
