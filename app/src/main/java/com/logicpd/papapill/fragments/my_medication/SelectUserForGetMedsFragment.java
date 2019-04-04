package com.logicpd.papapill.fragments.my_medication;

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

public class SelectUserForGetMedsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Select User";

    private LinearLayout backButton, homeButton;
    private Button btnUserA, btnUserB;
    private OnButtonClickListener mListener;
    private DatabaseHelper db;
    private List<User> userList;
    private TextView tvTitle;
    private String authFragment;

    public SelectUserForGetMedsFragment() {
        // Required empty public constructor
    }

    public static SelectUserForGetMedsFragment newInstance() {
        return new SelectUserForGetMedsFragment();
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

        tvTitle.setText("SELECT USER");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("authFragment")) {
                authFragment = bundle.getString("authFragment");
            }
        }

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
            btnUserA.performClick();
        } else if (userList.size() == 2) {
            btnUserA.setVisibility(View.VISIBLE);
            btnUserB.setVisibility(View.VISIBLE);
        }

    }


    private void setupViews(View view) {
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
            //bundle.putString("fragmentName", "MyMedsUserPinFragment");
            bundle.putString("fragmentName", "MyMedsUserPinFragment");
            bundle.putString("authFragment", authFragment);
            bundle.putSerializable("user", userList.get(0));
        }
        if (v == btnUserB) {
            //bundle.putString("fragmentName", "MyMedsUserPinFragment");
            bundle.putString("fragmentName", "MyMedsUserPinFragment");
            bundle.putString("authFragment", authFragment);
            bundle.putSerializable("user", userList.get(1));
        }
        mListener.onButtonClicked(bundle);
    }
}
