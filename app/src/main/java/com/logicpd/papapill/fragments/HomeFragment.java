package com.logicpd.papapill.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.DaySchedule;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.CloudManager;
import com.logicpd.papapill.utils.PreferenceUtils;
import com.logicpd.papapill.utils.SystemUtils;
import com.logicpd.papapill.utils.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment for Home screen
 *
 * @author alankilloren
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Home";

    private LinearLayout contentLayout, usersLayout, userALayout, userBLayout;
    private Button btnMedication, btnUserSettings, btnSystemManager, btnHelp;
    private TextView btnHidden, tvFirstTimeSetup, tvUserAName, tvUserBName, tvUserAText, tvUserBText, tvBatteryPercent;
    private ImageView imgBattery;
    private RelativeLayout indicatorWifi, indicatorBluetooth, indicatorDrawer, indicatorDoor, indicatorBattery;
    private TextClock timeText, dateText;
    private OnButtonClickListener mListener;
    private int taps = 0;
    private DatabaseHelper db;
    private List<User> userList;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private BluetoothAdapter bluetoothAdapter;
    private NetworkInfo networkInfo;
    private ConnectivityManager connManager;
    private PreferenceUtils prefs;
    private CloudManager cloudManager;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    private boolean isUserADayScheduleDone, isUserBDayScheduleDone;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        handler = new Handler();

        prefs = new PreferenceUtils(getActivity());

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            wifiInfo = wifiManager.getConnectionInfo();
        }
        connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        cloudManager = CloudManager.getInstance(getActivity());

        displayStatusBar();

        db = DatabaseHelper.getInstance(getActivity());
        userList = db.getUsers();
        if (userList.size() == 0) {
            tvFirstTimeSetup.setVisibility(View.VISIBLE);
            usersLayout.setVisibility(View.GONE);
            TextUtils.disableButton(btnMedication);
            TextUtils.disableButton(btnUserSettings);
        } else if (userList.size() > 0) {
            tvFirstTimeSetup.setVisibility(View.GONE);
            usersLayout.setVisibility(View.VISIBLE);

            startTimer();
        } else {
            //TODO handle any other situation here?
        }

        if (db.getMedications().size() == 0) {
            TextUtils.disableButton(btnMedication);
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(AppConstants.TAG, "Updating schedule...");
                // refresh view with updated schedule
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getScheduleData();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 60000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void getScheduleData() {
        userList = db.getUsers();
        if (userList.size() > 0) {
            //get first user
            User userA = userList.get(0);
            tvUserAName.setText(userA.getUsername());
            if (db.getMedicationsForUser(userA).size() == 0) {
                tvUserAText.setText("Currently no medications entered.\n\nTap System Manager...Manage Medications to add a medication.");
                userALayout.setVisibility(View.VISIBLE);
            } else {
                DaySchedule daySchedule = db.getDaySchedule(userA);
                Log.d(AppConstants.TAG, "There are " + daySchedule.getScheduleItemList().size()
                        + " item(s) in the schedule for user " + userA.getUsername());

                // display next scheduled dose for the day
                if (daySchedule.getScheduleItemList().size() > 0) {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/dd/yyyy h:mm a", Locale.getDefault());
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/dd/yyyy h:mm:ss a", Locale.getDefault());

                    for (final ScheduleItem scheduleItem : daySchedule.getScheduleItemList()) {
                        DispenseTime dispenseTime = db.getDispenseTime(scheduleItem.getDispenseTimeId());

                        //get current date
                        SimpleDateFormat monthDateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
                        Date now = new Date();
                        String today = monthDateFormat.format(now);
                        String schedtimestamp = today + " " + dispenseTime.getDispenseTime();
                        String nowtimestamp = outputDateFormat.format(now);
                        try {
                            Date schedDate = inputDateFormat.parse(schedtimestamp);
                            Date nowDate = outputDateFormat.parse(nowtimestamp);
                            if (schedDate.compareTo(nowDate) < 0) {
                                //event has passed, skip
                                tvUserAText.setText("No items currently scheduled");
                                isUserADayScheduleDone = true;
                            } else {
                                //next scheduled item
                                tvUserAText.setText("NEXT DOSE:\n" + dispenseTime.getDispenseName() + " " + dispenseTime.getDispenseTime());
                                isUserADayScheduleDone = false;
                                break;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    tvUserAText.setText("No items currently scheduled");
                    isUserADayScheduleDone = true;
                }
                userALayout.setVisibility(View.VISIBLE);

                if (isUserADayScheduleDone) {
                    checkRemainingSchedule(userA, tvUserAText);
                }
            }

            // USER B
            if (userList.size() > 1) {
                //get second user
                User userB = userList.get(1);
                tvUserBName.setText(userB.getUsername());
                if (db.getMedicationsForUser(userB).size() == 0) {
                    tvUserBText.setText("You haven't entered any medications yet.\n\nTap System Manager...Manage Medications to add a medication.");
                    userBLayout.setVisibility(View.VISIBLE);
                } else {
                    DaySchedule daySchedule = db.getDaySchedule(userB);
                    Log.d(AppConstants.TAG, "There are " + daySchedule.getScheduleItemList().size()
                            + " item(s) in the schedule for user " + userB.getUsername());

                    // display next scheduled dose for the day
                    if (daySchedule.getScheduleItemList().size() > 0) {
                        SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/dd/yyyy h:mm a", Locale.getDefault());
                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/dd/yyyy h:mm:ss a", Locale.getDefault());

                        for (ScheduleItem scheduleItem : daySchedule.getScheduleItemList()) {
                            DispenseTime dispenseTime = db.getDispenseTime(scheduleItem.getDispenseTimeId());

                            //get current date
                            SimpleDateFormat monthDateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
                            Date now = new Date();
                            String today = monthDateFormat.format(now);
                            String schedtimestamp = today + " " + dispenseTime.getDispenseTime();
                            String nowtimestamp = outputDateFormat.format(now);
                            try {
                                Date schedDate = inputDateFormat.parse(schedtimestamp);
                                Date nowDate = outputDateFormat.parse(nowtimestamp);
                                if (schedDate.compareTo(nowDate) < 0) {
                                    //skip passed schedule item
                                    tvUserBText.setText("No items currently scheduled");
                                    isUserBDayScheduleDone = true;
                                } else {
                                    //future schedule item
                                    tvUserBText.setText("NEXT DOSE:\n" + dispenseTime.getDispenseName() + " " + dispenseTime.getDispenseTime());
                                    isUserBDayScheduleDone = false;
                                    break;
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        tvUserBText.setText("No items currently scheduled");
                        isUserBDayScheduleDone = true;
                    }

                    if (isUserBDayScheduleDone) {
                        checkRemainingSchedule(userB, tvUserBText);
                    }
                }
                userBLayout.setVisibility(View.VISIBLE);
            } else {
                //hide user B
                userBLayout.setVisibility(View.GONE);
            }
        }
    }

    private void checkRemainingSchedule(User user, TextView tv) {
        // get all scheduled items for this user and iterate through them to find the next available schedule item
        DaySchedule daySchedule = db.getDaySchedule(user);
        List<ScheduleItem> scheduleItems = daySchedule.getScheduleItemList();
        if (scheduleItems.size() == 0) {
            tv.setText("No items currently scheduled");
        } else {
            for (ScheduleItem scheduleItem : scheduleItems) {
                //next scheduled item
                tv.setText("NEXT DOSE:\n" + scheduleItem.getDispenseName() + " " + scheduleItem.getDispenseTime());
                //isUserADayScheduleDone = false;
                break;
            }
        }
    }

    private void setBluetoothIcon()
    {
        try {
            //TODO check bluetooth
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                indicatorBluetooth.setVisibility(View.GONE);
            } else {
                indicatorBluetooth.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex)
        {
            indicatorBluetooth.setVisibility(View.GONE);
            Log.e(AppConstants.TAG, "setBluetoothIcon() exception:"+ex.toString());
        }
    }

    private void displayStatusBar() {
        //check wifi
        if (networkInfo.isConnected()) {
            indicatorWifi.setVisibility(View.VISIBLE);
            indicatorWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextUtils.showToast(getActivity(), "Connected to " + wifiInfo.getSSID());
                }
            });
        } else {
            indicatorWifi.setVisibility(View.GONE);
        }

        setBluetoothIcon();

        // TODO battery
        indicatorBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                sb.append("Charging: ");
                if (SystemUtils.isCharging(getActivity())) {
                    sb.append("YES\n");
                    sb.append("Percentage: " + SystemUtils.getBatteryPercentage(getActivity()) + "%");
                } else {
                    sb.append("NO\n");
                }

                TextUtils.showToast(getActivity(), sb.toString());
            }
        });
        if (SystemUtils.isPowerConnected(getActivity())) {
            Log.d(AppConstants.TAG, "Power is connected");
            imgBattery.setImageResource(R.drawable.ic_battery_charging);
            tvBatteryPercent.setText("AC POWER");
        } /*else {  //TODO - handle icons when running on battery only
            Log.d(AppConstants.TAG, "Running on battery power");
            BatteryManager bm = (BatteryManager) getActivity().getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (batLevel < 100) {
                    imgBattery.setImageResource(R.drawable.ic_battery_charging);
                } else if (batLevel == 100) {
                    imgBattery.setImageResource(R.drawable.ic_battery_full);
                }
                tvBatteryPercent.setText(batLevel + "%");
            } else {
                imgBattery.setImageResource(R.drawable.ic_battery_none);
                tvBatteryPercent.setText("N/A");
            }
        }*/

        //TODO door - Need to be able to obtain a status somewhere from the hardware
        //indicatorDoor.setVisibility(View.GONE);

        //TODO drawer - Need to be able to obtain a status somewhere from the hardware
        //indicatorDrawer.setVisibility(View.GONE);

    }

    /**
     * Uses reflection to draw the vertical thumb and track on the scrollbar
     *
     * @param scrollView     Passed in ScrollView object
     * @param declaredMethod String method to call (either "setVerticalTrackDrawable" or "setVerticalThumbDrawable")
     * @param drawable       Drawable to use
     */
    private void styleScrollbar(ScrollView scrollView, String declaredMethod, int drawable) {
        try {
            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(scrollView);
            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);
            Method method = scrollBar.getClass().getDeclaredMethod(declaredMethod, Drawable.class);
            method.setAccessible(true);
            method.invoke(scrollBar, ContextCompat.getDrawable(getActivity(), drawable));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViews(View view) {

        contentLayout = view.findViewById(R.id.layout_content);
        btnMedication = view.findViewById(R.id.button_my_medication);
        btnUserSettings = view.findViewById(R.id.button_user_settings);
        btnSystemManager = view.findViewById(R.id.button_system_manager);
        btnHelp = view.findViewById(R.id.button_help);
        btnHidden = view.findViewById(R.id.hiddenButton);
        timeText = view.findViewById(R.id.textclock_time);
        dateText = view.findViewById(R.id.textclock_date);
        /*timeText.setTimeZone("GMT-5:00");
        dateText.setTimeZone("GMT-5:00");*/

        btnMedication.setOnClickListener(this);
        btnUserSettings.setOnClickListener(this);
        btnSystemManager.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnHidden.setOnClickListener(this);

        tvFirstTimeSetup = view.findViewById(R.id.textview_first_time_setup);
        usersLayout = view.findViewById(R.id.layout_users);
        userALayout = view.findViewById(R.id.layout_userA);
        userBLayout = view.findViewById(R.id.layout_userB);
        tvUserAName = view.findViewById(R.id.textview_usernameA);
        tvUserBName = view.findViewById(R.id.textview_usernameB);
        tvUserAText = view.findViewById(R.id.textview_infotextA);
        tvUserBText = view.findViewById(R.id.textview_infotextB);

        indicatorWifi = view.findViewById(R.id.layout_wifi);
        indicatorBluetooth = view.findViewById(R.id.layout_bluetooth);
        indicatorDoor = view.findViewById(R.id.layout_door);
        indicatorDrawer = view.findViewById(R.id.layout_drawer);
        indicatorBattery = view.findViewById(R.id.layout_battery);

        tvBatteryPercent = view.findViewById(R.id.textview_battery_percentage);

        imgBattery = view.findViewById(R.id.symbol_battery);
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
        if (v == btnSystemManager) {
            //open system manager screen
            bundle.putString("fragmentName", "SystemManagerFragment");
            mListener.onButtonClicked(bundle);
        }

        //hidden exit button for dev
        if (v == btnHidden) {
            if (taps == 2) {
                taps = 0;
                bundle.putString("fragmentName", "Exit");
                mListener.onButtonClicked(bundle);
            } else {
                taps += 1;
            }
        }
        if (v == btnHelp) {
            bundle.putString("fragmentName", "HelpFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnUserSettings) {
            bundle.putString("fragmentName", "UserSettingsFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnMedication) {
            bundle.putString("fragmentName", "MyMedicationFragment");
            mListener.onButtonClicked(bundle);
        }
    }
}
