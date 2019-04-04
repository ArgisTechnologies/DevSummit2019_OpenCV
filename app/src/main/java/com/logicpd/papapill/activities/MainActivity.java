package com.logicpd.papapill.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.logicpd.papapill.App;
import com.logicpd.papapill.R;
import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.data.DaySchedule;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.fragments.HomeFragment;
import com.logicpd.papapill.fragments.IncorrectSystemKeyFragment;
import com.logicpd.papapill.fragments.RecoverSystemKeyFragment;
import com.logicpd.papapill.fragments.SystemKeyFragment;
import com.logicpd.papapill.fragments.help.CheckUpdatesFragment;
import com.logicpd.papapill.fragments.help.DeveloperTestFragment;
import com.logicpd.papapill.fragments.help.HelpFragment;
import com.logicpd.papapill.fragments.help.InstructionsFragment;
import com.logicpd.papapill.fragments.help.LegalNoticesFragment;
import com.logicpd.papapill.fragments.help.QuickStartFragment;
import com.logicpd.papapill.fragments.help.TrainingVideosFragment;
import com.logicpd.papapill.fragments.help.WarrantyFragment;
import com.logicpd.papapill.fragments.my_medication.AlarmReceivedFragment;
import com.logicpd.papapill.fragments.my_medication.CannotRetrieveMedFragment;
import com.logicpd.papapill.fragments.my_medication.CheckFillStatusFragment;
import com.logicpd.papapill.fragments.my_medication.DispenseMedsFragment;
import com.logicpd.papapill.fragments.my_medication.GetAsNeededDispenseAmountFragment;
import com.logicpd.papapill.fragments.my_medication.GetAsNeededMedicationFragment;
import com.logicpd.papapill.fragments.my_medication.GetScheduledMedEarlyFragment;
import com.logicpd.papapill.fragments.my_medication.GetSingleDoseEarlyFragment;
import com.logicpd.papapill.fragments.my_medication.MedicationScheduleSummaryFragment;
import com.logicpd.papapill.fragments.my_medication.MedicationTakenEarlyFragment;
import com.logicpd.papapill.fragments.my_medication.MedicationTakenFragment;
import com.logicpd.papapill.fragments.my_medication.MedicationTrackerFragment;
import com.logicpd.papapill.fragments.my_medication.MedicationsPausedFragment;
import com.logicpd.papapill.fragments.my_medication.MyMedicationFragment;
import com.logicpd.papapill.fragments.my_medication.MyMedsUserPinFragment;
import com.logicpd.papapill.fragments.my_medication.ReturnCupFragment;
import com.logicpd.papapill.fragments.my_medication.ScheduledMedicationTakenFragment;
import com.logicpd.papapill.fragments.my_medication.SelectMedScheduleListFragment;
import com.logicpd.papapill.fragments.my_medication.SelectPauseMedicationFragment;
import com.logicpd.papapill.fragments.my_medication.SelectReviewMedSchedFragment;
import com.logicpd.papapill.fragments.my_medication.SelectUserForGetMedsFragment;
import com.logicpd.papapill.fragments.my_medication.TakeMedsFragment;
import com.logicpd.papapill.fragments.power_on.SetSystemKeyFragment;
import com.logicpd.papapill.fragments.power_on.SetupFragment;
import com.logicpd.papapill.fragments.power_on.TrainingFragment;
import com.logicpd.papapill.fragments.power_on.UserAgreementFragment;
import com.logicpd.papapill.fragments.power_on.WelcomeFragment;
import com.logicpd.papapill.fragments.system_manager.SystemManagerFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ConfirmDeleteDispenseTimeFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ConfirmMedInfoFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ConfirmRefillMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ConfirmRemoveMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.DirectionsQuantityPerDoseFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.DispenseTimeDeletedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.DispenseTimeFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.DispenseTimeNameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.DispenseTimeSummaryFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.DuplicateMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.EditDispenseTimesFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.EnterDailyQuantityPerDoseFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.EnterMonthlyQuantityPerDoseFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.EnterUserPinFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.EnterWeeklyQuantityPerDoseFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ImportantMessageFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.InconsistentMedStrengthFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.LoadMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ManageMedsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MaximumNumberPerDoseFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MaximumUnitsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationAddedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationDosageFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationFitInstructionsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationNameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationNickNameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationQuantityFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationQuantityMessageFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationRefilledFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationRemovedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationScheduleFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationStrengthFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.MedicationTimeBetweenDosesFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.OptionalMedInfoFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.OtherLocationNameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.PatientNameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.PlaceMedDrawerFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.PlaceMedOtherFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.PlaceMedRefrigeratorFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.RemoveBinFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.RemoveBinHelpFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.RemoveMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.ScanBarCodeFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectChangeMedScheduleFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectDeleteDispenseTimeFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectDispensingTimesFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectEditDispenseTimeFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectMedEntryMethodFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectMedLocationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectMedScheduleFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectRefillMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectRemoveMedicationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectUseByDateFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectUserForChangeScheduleFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectUserForEditDispenseTimesFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectUserForMedsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectUserForRefillFragment;
import com.logicpd.papapill.fragments.system_manager.manage_medications.SelectUserForRemoveMedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.AddUserFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.AddUserPinFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ConfirmDeleteContactFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ConfirmDeleteMedsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ConfirmDeleteUserFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactAddedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactCategoryFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactDeletedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactEmailAddressFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactInfoFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactListFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactNameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactTextNumberFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactUpdatedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ContactVoiceNumberFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.EditNotificationSettingsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.EditUserFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.EditUserNicknameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.EditUserPinFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.ManageUsersFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NewUserSetupCompleteFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NotificationContactsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NotificationOptionsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NotificationPreferencesFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NotificationSettingsFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NotificationSettingsMessageFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.NotificationSettingsSavedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.SelectContactEditNotificationFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.SelectContactPinRecoveryFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.SelectDeleteUserFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.SelectEditUserFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.UserAddedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.UserDeletedFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.VerifyContactInfoFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.VerifyNicknameFragment;
import com.logicpd.papapill.fragments.system_manager.manage_users.VerifyUserInfoFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.BluetoothFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.BluetoothPairFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.ChangeSystemKeyFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.CurrentSystemKeyFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.SelectWifiNetworkFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.SystemSettingsFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.VerifySystemKeyFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.WifiConnectedFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.WifiPasswordFragment;
import com.logicpd.papapill.fragments.system_manager.system_settings.WirelessNetworkFragment;
import com.logicpd.papapill.fragments.user_settings.AudioSettingFragment;
import com.logicpd.papapill.fragments.user_settings.ChangeUserPinFragment;
import com.logicpd.papapill.fragments.user_settings.FontSettingFragment;
import com.logicpd.papapill.fragments.user_settings.IncorrectUserPinFragment;
import com.logicpd.papapill.fragments.user_settings.RecoverUserPinFragment;
import com.logicpd.papapill.fragments.user_settings.ScreenSettingFragment;
import com.logicpd.papapill.fragments.user_settings.SelectUserSettingsFragment;
import com.logicpd.papapill.fragments.user_settings.ThemeSettingFragment;
import com.logicpd.papapill.fragments.user_settings.UserPinChangedFragment;
import com.logicpd.papapill.fragments.user_settings.UserPinFragment;
import com.logicpd.papapill.fragments.user_settings.UserSettingsFragment;
import com.logicpd.papapill.fragments.user_settings.VerifyUserPinFragment;
import com.logicpd.papapill.fragments.user_settings.VoiceSettingFragment;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.misc.CloudManager;
import com.logicpd.papapill.receivers.ConnectionReceiver;
import com.logicpd.papapill.utils.FragmentUtils;
import com.logicpd.papapill.utils.PreferenceUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * The main activity - controls all of the associated fragments
 *
 * @author alankilloren
 */
public class MainActivity extends Activity implements OnButtonClickListener, ConnectionReceiver.ConnectionReceiverListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final int REQUEST_PERMISSION_CAMERA = 1;

    private FrameLayout rootLayout;
    private PreferenceUtils prefs;
    private AudioManager audioManager;

    /* OpenCV requires an initialization for the Android/Jsva version of their toolset at the
     * activity level.  This is that initialization.
     */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setTheme(R.style.Sunrise);//TODO testing
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 75, 0);//TODO setting default volume at 75
        }

        prefs = new PreferenceUtils(this);
        rootLayout = findViewById(R.id.layout_root);

        //System is yelling that I need to code for permissions to the camera.
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showExplanation("Permission Needed", "The system requires access to the camera", Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);
            }
        }

        // first-time run
        FragmentUtils.showFragment(this, new HomeFragment(), rootLayout, null, "Home");

        checkConnection();//check to see if we are connected to network

        AppConfig appConfig = AppConfig.getInstance();
        appConfig.loadFile(this.getApplicationContext());
    }

    @Override
    public void onButtonClicked(Bundle bundle) {
        processFragment(bundle);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();//exit
        } else {
            getFragmentManager().popBackStack();//go back to previous fragment
        }
    }

    /**
     * Handles displaying of fragments via onButtonClicked callback
     *
     * @param bundle passed in Bundle object from fragment
     */
    private void processFragment(Bundle bundle) {
        String fragmentName;

        if (bundle != null) {
            fragmentName = bundle.getString("fragmentName");

            //removes a fragment from the back stack
            if (bundle.containsKey("removeFragment")) {
                if (bundle.getBoolean("removeFragment")) {
                    FragmentUtils.removeFragment(this);
                }
            }

            //removes all fragments
            if (bundle.containsKey("removeAllFragments")) {
                if (bundle.getBoolean("removeAllFragments")) {
                    FragmentUtils.removeAllFragments(this);
                }
            }

            //removes all fragments up to the specified fragment
            if (bundle.containsKey("removeAllFragmentsUpToCurrent")) {
                FragmentUtils.removeAllFragmentsUpToCurrent(this, bundle.getString("removeAllFragmentsUpToCurrent"));
            }

            //displays a fragment and adds to back stack
            if (fragmentName != null) {
                //FragmentUtils.showFragment(this, rootLayout, bundle, fragmentName);//TODO save this in case we change it later
                if (fragmentName.equals("Back")) {
                    onBackPressed();
                }
                if (fragmentName.equals("Home")) {
                    FragmentUtils.removeAllFragments(this);
                    FragmentUtils.showFragment(this, new HomeFragment(), rootLayout, null, fragmentName);
                }
                if (fragmentName.equals("Exit")) {
                    finish();
                }
                if (fragmentName.equals("HelpFragment")) {
                    FragmentUtils.showFragment(this, new HelpFragment(), rootLayout, null, fragmentName);
                }
                if (fragmentName.equals("DeveloperTestFragment")) {
                    FragmentUtils.showFragment(this, new DeveloperTestFragment(), rootLayout, bundle, fragmentName);
                }
            }
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            //TODO show a No Internet Alert or Dialog?
            Log.e(AppConstants.TAG, "No network connection!");
        } else {
            Log.d(AppConstants.TAG, "Connected to network");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        App.getInstance().setConnectionListener(this);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            Log.e(AppConstants.TAG, "No network connection!");
            //TODO show a No Internet Alert or Dialog?
        } else {
            Log.d(AppConstants.TAG, "Connected to network");
        }
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

}