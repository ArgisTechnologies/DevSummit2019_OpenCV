package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.enums.BundleEnums;
import com.logicpd.papapill.enums.MedLocaEnum;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.interfaces.OnRotateCompleteListener;
import com.logicpd.papapill.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectMedLocationFragment
 *
 * @author alankilloren
 */
public class SelectMedLocationFragment extends Fragment implements View.OnClickListener {

    private String TAG;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnBin, btnFridge, btnDrawer, btnOther;
    private User user;
    private Medication medication;
    private boolean isFromSchedule;
    private int binLocation;
    private int mNumOfBins;
    private final int BIN_NOT_AVAILABLE = -1;

    public SelectMedLocationFragment() {
        // Required empty public constructor
        TAG = this.getClass().getName();
    }

    public static SelectMedLocationFragment newInstance() {
        return new SelectMedLocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNumOfBins = getResources().getInteger(R.integer.numOfBins);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_select_med_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable(BundleEnums.user.toString());
            medication = (Medication) bundle.getSerializable(BundleEnums.medication.toString());
            if (bundle.containsKey(BundleEnums.isFromSchedule.toString())) {
                isFromSchedule = bundle.getBoolean(BundleEnums.isFromSchedule.toString());
            }
        }
    }

    /*
     * Is there at least 1 bin for new medication ?
     * - can't rely on medication table row count because
     * there might be medication(s) in drawer or fridge.
     * @return: true if at least 1 bin available.
     */
    protected boolean hasBin4NewMed() {
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
        List<Medication> medicationList = db.getMedications();
        int NUM_OF_BINS = this.getResources().getInteger(R.integer.numOfBins);

        // for sure there is at least 1 bin
        if (medicationList.size() < NUM_OF_BINS)
            return true;

        // else -> check if any medication row(s) are drawer or fridge or other
        int binCount = 0;
        for (Medication med : medicationList) {
            // index for drawer(97), fridge(98), other(99)
            if (med.getMedication_location() <= NUM_OF_BINS)
                binCount++;

            // bin are filled ?
            if (binCount >= NUM_OF_BINS) {
                TextUtils.disableButton(btnBin);
                return false;
            }
        }
        return true;
    }

    private void setupViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnBin = view.findViewById(R.id.button_bin);
        if (hasBin4NewMed()) {
            btnBin.setOnClickListener(this);
        }
        btnDrawer = view.findViewById(R.id.button_drawer);
        btnDrawer.setOnClickListener(this);
        btnFridge = view.findViewById(R.id.button_fridge);
        btnFridge.setOnClickListener(this);
        btnOther = view.findViewById(R.id.button_other);
        btnOther.setOnClickListener(this);

        //TODO disabling these until workflows for each are done
        TextUtils.disableButton(btnDrawer);
        TextUtils.disableButton(btnFridge);
        TextUtils.disableButton(btnOther);
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
                    + this.getResources().getString(R.string.selectMedLocationFrag_must_impl_butClick));
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        if (v == backButton) {
            bundle = createBundle(null, null, false, "Back");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle = createBundle(null, null, false, "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnBin) {
            binLocation = getNextAvailableBin();
            if (BIN_NOT_AVAILABLE == binLocation || binLocation > mNumOfBins) {
                /*
                 * out of bin error !
                 */
                Log.e(TAG, "invalid bin location:" + binLocation);
                return;
            }

            Log.d(TAG, "Moving bin" + binLocation);

            if (AppConfig.getInstance().isTinyGAvailable) {
                TinyGDriver.getInstance().doMoveBin4User(binLocation);

                Log.d(TAG, "Set OnRotateCompleteListener");
                TinyGDriver.getInstance().setMoveBinListener(
                        new OnRotateCompleteListener() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "Bin moved completion OK");
                                medication.setMedication_location(binLocation);
                                medication.setMedication_location_name(MedLocaEnum.BIN.toString());
                                Bundle bundle = createBundle(user, medication, isFromSchedule, "RemoveBinFragment");
                                mListener.onButtonClicked(bundle);
                            }
                        });
            } else {
                medication.setMedication_location(binLocation);
                medication.setMedication_location_name(MedLocaEnum.BIN.toString());
                Bundle b = createBundle(user, medication, isFromSchedule, "RemoveBinFragment");
                mListener.onButtonClicked(b);
            }
        }
        if (v == btnDrawer) {
            int drawerLocation = this.getResources().getInteger(R.integer.drawerLocation);
            medication.setMedication_location(drawerLocation);//making this something separate from bin #s
            medication.setMedication_location_name(MedLocaEnum.DRAWER.toString());
            bundle = createBundle(user, medication, isFromSchedule, "PlaceMedDrawerFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnFridge) {
            int frdgeLocation = this.getResources().getInteger(R.integer.refridgeratorLocation);
            medication.setMedication_location(frdgeLocation);//making this something separate from bin #s
            medication.setMedication_location_name(MedLocaEnum.FRIDGE.toString());
            bundle = createBundle(user, medication, isFromSchedule, "PlaceMedRefrigeratorFragment");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnOther) {
            int otherLocation = this.getResources().getInteger(R.integer.otherLocation);
            medication.setMedication_location(otherLocation);//making this something separate from bin #s
            medication.setMedication_location_name(MedLocaEnum.OTHER.toString());
            bundle = createBundle(user, medication, isFromSchedule, "OtherLocationNameFragment");
            mListener.onButtonClicked(bundle);
        }
    }

    private Bundle createBundle(User user,
                                Medication medication,
                                boolean isFromSchedule,
                                String fragmentname) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleEnums.user.toString(), user);
        bundle.putSerializable(BundleEnums.medication.toString(), medication);
        bundle.putBoolean(BundleEnums.isFromSchedule.toString(), isFromSchedule);
        bundle.putString(BundleEnums.fragmentName.toString(), fragmentname);
        return bundle;
    }

    /**
     * Go through medications and find first empty bin
     * - no smarts, no rules, no algorithm
     * BigO performance of logN
     *
     * @return Integer of bin #
     */
    private int getNextAvailableBin() {
        List<Medication> medications = DatabaseHelper.getInstance(getActivity()).getMedicationsByBin();
        List<Integer> bins = new ArrayList<Integer>();
        int NUM_OF_BINS = this.getResources().getInteger(R.integer.numOfBins);

        for (int j = 1; j <= NUM_OF_BINS; j++) {
            int availableBin = j;

            for (Medication medication : medications) {
                if (medication.getMedication_location() == j) {
                    availableBin = BIN_NOT_AVAILABLE;
                    break;
                }
            }

            if (BIN_NOT_AVAILABLE != availableBin)
                return availableBin;
        }
        // none available -- should never happen ?
        return BIN_NOT_AVAILABLE;
    }
}