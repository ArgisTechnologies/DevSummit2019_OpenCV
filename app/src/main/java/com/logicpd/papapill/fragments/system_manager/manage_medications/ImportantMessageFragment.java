package com.logicpd.papapill.fragments.system_manager.manage_medications;

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
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

/**
 * ImportantMessageFragment
 *
 * @author alankilloren
 */
public class ImportantMessageFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Important Message";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    TextView tvMessage;
    Button btnNext;
    OnButtonClickListener mListener;
    User user;

    public ImportantMessageFragment() {
        // Required empty public constructor
    }

    public static ImportantMessageFragment newInstance() {
        return new ImportantMessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_important_message, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }
        tvMessage.setText("IT IS IMPORTANT THAT INFORMATION IS ENTERED EXACTLY AS IT APPEARS ON THE PRESCRIPTION BOTTLE LABEL." +
                "\n\nENTERING INCORRECT OR INCOMPLETE INFORMATION CAN BE DANGEROUS.");
    }


    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMessage = view.findViewById(R.id.textview_important_message);
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
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnNext) {
            bundle.putString("fragmentName", "PatientNameFragment");
            bundle.putSerializable("user", user);
        }
        mListener.onButtonClicked(bundle);
    }
}
