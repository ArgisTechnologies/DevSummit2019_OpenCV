package com.logicpd.papapill.fragments.system_manager.manage_users;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class NotificationContactsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "NotificationContactsFragment";

    private LinearLayout backButton, homeButton;
    private Button btnNewContact, btnDone;
    private OnButtonClickListener mListener;
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private List<Contact> selectedContactList;
    private DatabaseHelper db;
    private User user;
    private ContactsAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView emptyText;
    private boolean isFromAddNewUser = false;

    public NotificationContactsFragment() {
        // Required empty public constructor
    }

    public static NotificationContactsFragment newInstance() {
        return new NotificationContactsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_notification_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());

        setupViews(view);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (bundle.containsKey("isFromAddNewUser")) {
                isFromAddNewUser = bundle.getBoolean("isFromAddNewUser");
            }
        }
        if (contactList == null) {
            contactList = new ArrayList<>();
        }
        contactList.clear();
        contactList.addAll(db.getContacts());
        adapter = new ContactsAdapter(getActivity(), contactList, true);
        adapter.setOnItemClickListener(new ContactsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //open ContactCategoryFragment when list item tapped on
                Contact contact = contactList.get(position);
                bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
                bundle.putSerializable("contact", contact);
                bundle.putSerializable("user", user);
                bundle.putString("fragmentName", "ContactCategoryFragment");
                mListener.onButtonClicked(bundle);
            }
        });
        //}
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

        if (contactList == null || contactList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Press NEW CONTACT to add contact");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);

        btnNewContact = view.findViewById(R.id.button_new_contact);
        btnNewContact.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerview_notification_list);
        emptyText = view.findViewById(R.id.textview_add_contact);
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
        if (v == btnDone) {
            if (isFromAddNewUser) {
                bundle.putString("fragmentName", "NewUserSetupCompleteFragment");
                mListener.onButtonClicked(bundle);
            } else {
                bundle.putString("removeAllFragmentsUpToCurrent", "ManageUsersFragment");
                mListener.onButtonClicked(bundle);
            }
        }
        if (v == btnNewContact) {
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putBoolean("isFromNotifications", true);
            bundle.putString("fragmentName", "ContactNameFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
