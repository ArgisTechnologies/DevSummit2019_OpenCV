package com.logicpd.papapill.fragments.system_manager.manage_users;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.ContactsAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class SelectContactPinRecoveryFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "SelectContactPinRecoveryFragment";

    private LinearLayout backButton, homeButton;
    private Button btnNewContact, btnEdit;
    private OnButtonClickListener mListener;
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private List<Contact> selectedContactList;
    private DatabaseHelper db;
    private User user;
    private TextView emptyText, tvTitle;
    private ContactsAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isFromAddNewUser = false;
    private boolean isFromNotifications = false;
    private boolean isFromChangePIN = false;

    public SelectContactPinRecoveryFragment() {
        // Required empty public constructor
    }

    public static SelectContactPinRecoveryFragment newInstance() {
        return new SelectContactPinRecoveryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_contact_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (bundle.containsKey("isFromAddNewUser")) {
                isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            }
            if (bundle.containsKey("isFromNotifications")) {
                isFromNotifications = bundle.getBoolean("isFromNotifications");
            }
            if (bundle.containsKey("isFromChangePIN")) {
                isFromChangePIN = bundle.getBoolean("isFromChangePIN");
            }
        }

        tvTitle.setText("SELECT A CONTACT FOR PIN RECOVERY");

        contactList = db.getContacts();
        adapter = new ContactsAdapter(getActivity(), contactList, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);
        if (contactList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Press NEW CONTACT to add a contact");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(new ContactsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //go to VerifyContactInfoFragment
                user.setRecoveryContactId(contactList.get(position).getId());
                Bundle bundle = new Bundle();
                if (isFromChangePIN) {
                    bundle.putBoolean("isFromChangePIN", isFromChangePIN);
                    bundle.putString("fragmentName", "VerifyUserPinFragment");
                } else {
                    bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
                    bundle.putString("fragmentName", "VerifyUserInfoFragment");
                }
                bundle.putSerializable("user", user);
                bundle.putSerializable("contact", contactList.get(position));
                mListener.onButtonClicked(bundle);
            }
        });
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnNewContact = view.findViewById(R.id.button_new_contact);
        btnNewContact.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerview_contact_list);
        emptyText = view.findViewById(R.id.textview_add_contact);

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
        if (v == btnNewContact) {
            bundle.putBoolean("isFromChangePIN", isFromChangePIN);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putBoolean("isFromNotifications", isFromNotifications);
            bundle.putString("fragmentName", "ContactNameFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}
