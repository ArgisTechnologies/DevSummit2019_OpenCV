package com.logicpd.papapill.fragments.my_medication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.NotificationSetting;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.CloudManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class AlarmReceivedFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "AlarmReceivedFragment";

    LinearLayout contentLayout;
    LinearLayout backButton, homeButton;
    OnButtonClickListener mListener;
    Button btnOk;
    TextView tvTitle, tvUsername;
    ScheduleItem scheduleItem;
    User user;
    Medication medication;
    DatabaseHelper db;
    MediaPlayer mp;

    public AlarmReceivedFragment() {
        // Required empty public constructor
    }

    public static AlarmReceivedFragment newInstance() {
        return new AlarmReceivedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_alarm_received, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            scheduleItem = (ScheduleItem) bundle.getSerializable("scheduleItem");
            DispenseTime dispenseTime = db.getDispenseTime(scheduleItem.getDispenseTimeId());
            tvTitle.setText("IT'S TIME FOR YOUR\n" + dispenseTime.getDispenseName() + " MEDICATION(S)");
            user = db.getUserByID(scheduleItem.getUserId());
            tvUsername.setText(user.getUsername());
            medication = db.getMedication(scheduleItem.getMedicationId());
        }

        // play alarm sound
        mp = MediaPlayer.create(getActivity(), R.raw.emn_13_l_n);
        mp.start();

        //get contacts for notifications
        List<NotificationSetting> notificationSettingList = db.getNotificationSettingsForUser(user, 0);
        for (NotificationSetting notificationSetting : notificationSettingList) {
            Contact contact = db.getContact(notificationSetting.getContactId());
            Log.i(AppConstants.TAG, "Preparing notification (Time to take medication) for contact: " + contact.getName());
            if (notificationSetting.isTextSelected()) {
                //this contact has text (SMS) notification selected
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Content", "Time to Papapill");//TODO change to actual content message
                    jsonObject.put("ToNumber", contact.getTextNumber());
                    jsonObject.put("Type", "Text");
                    String json = jsonObject.toString();
                    Log.i(AppConstants.TAG, "Sending text notification data to cloud - " + json);
                    CloudManager.getInstance(getActivity()).sendMessage("sendnotification", json, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (notificationSetting.isEmailSelected()) {
                //this contact has email notification selected
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Subject", "Test Subject");//TODO change to actual subject
                    jsonObject.put("Content", "Time to Papapill");//TODO change to actual content
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(contact.getEmail());
                    jsonObject.put("ToAddresses", jsonArray);
                    jsonObject.put("Type", "Email");
                    String json = jsonObject.toString();
                    Log.i(AppConstants.TAG, "Sending email notification data to cloud - " + json);
                    CloudManager.getInstance(getActivity()).sendMessage("sendnotification", json, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (notificationSetting.isVoiceSelected()) {
                //this contact has voice notification selected
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Content", "Time to Papapill");//TODO change to actual content
                    jsonObject.put("ToNumber", contact.getVoiceNumber());
                    jsonObject.put("Type", "Voice");
                    String json = jsonObject.toString();
                    Log.i(AppConstants.TAG, "Sending voice notification data to cloud - " + json);
                    CloudManager.getInstance(getActivity()).sendMessage("sendnotification", json, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnOk = view.findViewById(R.id.button_ok);
        btnOk.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.textview_title);
        tvUsername = view.findViewById(R.id.textview_username);
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
        if (v == btnOk) {
            //dispense meds
            bundle.putInt("dispense_amount", scheduleItem.getDispenseAmount());
            bundle.putBoolean("isFromSchedule", true);
            bundle.putSerializable("user", user);
            bundle.putSerializable("medication", medication);
            bundle.putString("fragmentName", "DispenseMedsFragment");
        }
        mListener.onButtonClicked(bundle);
    }
}