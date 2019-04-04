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
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

import java.util.List;

public class SelectUserForEditDispenseTimesFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Select User";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    Button btnUserA, btnUserB;
    OnButtonClickListener mListener;
    DatabaseHelper db;
    TextView tvTitle;
    List<User> userList;

    public SelectUserForEditDispenseTimesFragment() {
        // Required empty public constructor
    }

    public static SelectUserForEditDispenseTimesFragment newInstance() {
        return new SelectUserForEditDispenseTimesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_select_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/

        tvTitle.setText("SELECT USER TO EDIT DISPENSING TIMES FOR");

        db = DatabaseHelper.getInstance(getActivity());

        userList = db.getUsers();
        if (userList.size() > 0) {
            btnUserA.setText(userList.get(0).getUsername());
            if (userList.size() == 2) {
                btnUserB.setText(userList.get(1).getUsername());
            }
        }

        if (userList.size() == 1) {
            btnUserA.setVisibility(View.VISIBLE);
            btnUserB.setVisibility(View.GONE);
        } else if (userList.size() == 2) {
            btnUserA.setVisibility(View.VISIBLE);
            btnUserB.setVisibility(View.VISIBLE);
        }

    }


    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnUserA = view.findViewById(R.id.button_select_user_a);
        btnUserA.setOnClickListener(this);
        btnUserB = view.findViewById(R.id.button_select_user_b);
        btnUserB.setOnClickListener(this);
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
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
        }
        if (v == btnUserA) {
            bundle.putString("fragmentName", "EditDispenseTimesFragment");
            bundle.putSerializable("user", userList.get(0));
        }
        if (v == btnUserB) {
            bundle.putString("fragmentName", "EditDispenseTimesFragment");
            bundle.putSerializable("user", userList.get(1));
        }
        mListener.onButtonClicked(bundle);
    }
}
