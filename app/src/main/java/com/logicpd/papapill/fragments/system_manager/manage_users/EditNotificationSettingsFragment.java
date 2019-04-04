package com.logicpd.papapill.fragments.system_manager.manage_users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.NotificationSetting;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.data.adapters.NotificationSettingsAdapter;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class EditNotificationSettingsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "EditNotificationSettingsFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnNext;
    private OnButtonClickListener mListener;
    private NotificationSettingsAdapter adapter;
    private RecyclerView recyclerView;
    private List<NotificationSetting> settings;
    private RecyclerView.LayoutManager mLayoutManager;
    private User user;
    private Contact contact;
    private DatabaseHelper db;
    private ProgressBar progressBar;
    private boolean isFromAddNewUser = false;

    public EditNotificationSettingsFragment() {
        // Required empty public constructor
    }

    public static EditNotificationSettingsFragment newInstance() {
        return new EditNotificationSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users_edit_notification_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            contact = (Contact) bundle.getSerializable("contact");
            if (bundle.containsKey("isFromAddNewUser")) {
                if (bundle.getBoolean("isFromAddNewUser")) {
                    isFromAddNewUser = true;
                }
            }
        }
        settings = new ArrayList<>();

        adapter = new NotificationSettingsAdapter(getActivity(), settings, mListener);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new NotificationSettingsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //TODO?
            }
        });

        new getDataTask().execute();
    }


    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnNext = view.findViewById(R.id.button_next);
        btnNext.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recyclerview_notification_settings_list);
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

    /**
     * Asynchronous task for grabbing settings data from the db or generating new data on the fly
     */
    @SuppressLint("StaticFieldLeak")
    private class getDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            // if a list of settings already exists in the db, use that
            // otherwise generate a new list from scratch to be saved to db
            List<NotificationSetting> tempList = db.getNotificationSettingList(user, contact);
            if (tempList.size() > 0) {
                settings.clear();
                settings.addAll(tempList);
            } else {
                String[] array = getResources().getStringArray(R.array.notification_settings);
                int i = 0;
                for (String s : array) {
                    NotificationSetting notificationSetting = new NotificationSetting();
                    notificationSetting.setUserId(user.getId());
                    notificationSetting.setId(i);
                    notificationSetting.setContactId(contact.getId());
                    notificationSetting.setName(s);
                    settings.add(notificationSetting);
                    i += 1;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
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
            // save current settings to db
            /*List<NotificationSetting> tempList = adapter.getListFromAdapter();
            db.deleteNotificationSettingsForContact(user, contact);//remove any settings if found
            for (NotificationSetting setting : tempList) {//add current settings
                db.addNotificationSetting(setting);
            }
            db.updateContact(contact);*/
            //bundle.putString("fragmentName", "NotificationSettingsSavedFragment");
            new dbTask().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class dbTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            /*
            Get current list from adapter and add/update to db
             */
            List<NotificationSetting> tempList = adapter.getListFromAdapter();
            db.deleteNotificationSettings(user, contact);//remove any settings if found
            /*for (NotificationSetting setting : tempList) {//add current settings
                db.addNotificationSetting(setting);
            }*/
            db.addNotificationSettings(tempList);
            contact.setSelected(true);
            db.updateContact(contact);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TODO - perform a check to see if no settings were made

            progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            bundle.putBoolean("isFromAddNewUser", isFromAddNewUser);
            bundle.putString("fragmentName", "NotificationSettingsSavedFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
