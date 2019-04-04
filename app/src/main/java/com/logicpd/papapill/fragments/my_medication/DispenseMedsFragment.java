package com.logicpd.papapill.fragments.my_medication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logicpd.papapill.App;
import com.logicpd.papapill.R;
import com.logicpd.papapill.computervision.detection.DetectorException;
import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnBinReadyListener;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.computervision.BinCamera;
import com.logicpd.papapill.interfaces.OnErrorListener;

import java.nio.ByteBuffer;

import static com.logicpd.papapill.misc.AppConstants.TAG;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class DispenseMedsFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "DispenseMedsFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnDev;
    private TextView tvTitle, tvMedication, tvMedInfo;
    private User user;
    private Medication medication;
    private DatabaseHelper db;
    private boolean isEarlyDispense, isFromSchedule;

    private BinCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private TextureView mTextureView;

    private boolean firstPicture = true;
    private boolean surfaceInitialized = false;

    private int dispenseAmount = 0;
    private int dispenseCounter = 1;//this would be used for multiple dispenses

    public DispenseMedsFragment() {
        // Required empty public constructor
    }

    public static DispenseMedsFragment newInstance() {
        return new DispenseMedsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup Camera Thread
        if (AppConfig.getInstance().isCameraAvailable) {
            mCameraThread = new HandlerThread("CameraBackground");
            mCameraThread.start();
            mCameraHandler = new Handler(mCameraThread.getLooper());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meds_dispensing_meds, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(getActivity());
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            medication = (Medication) bundle.getSerializable("medication");
            tvTitle.setText("DISPENSING MEDICATION FOR " + user.getUsername());
            if (bundle.containsKey("dispense_amount")) {
                dispenseAmount = bundle.getInt("dispense_amount");
                tvMedication.setText("" + dispenseCounter + " OF " + dispenseAmount
                        + "   " + medication.getName() + " " + medication.getStrength_value()
                        + " " + medication.getStrength_measurement());
            }
            if (bundle.containsKey("isEarlyDispense")) {
                isEarlyDispense = bundle.getBoolean("isEarlyDispense");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
            if (bundle.containsKey("isFromSchedule")) {
                isFromSchedule = bundle.getBoolean("isFromSchedule");
            }
        }

        //check to see if this build has camera enabled
        if (AppConfig.getInstance().isCameraAvailable) {
            try {
                mCamera = BinCamera.getInstance();
                mCamera.initializeCamera(getActivity(), mCameraHandler, mOnImageAvailableListener, mTextureView);
            } catch (DetectorException de) {
                Log.e(TAG, "Error in Camera Initialization", de);
            }
        }

        //check to see if this build has TinyG available. If so, perform dispense
        if (AppConfig.getInstance().isTinyGAvailable) {
            TinyGDriver.getInstance().setDispenseListeners(
                    new OnBinReadyListener() {
                        @Override
                        public void onBinReady() {

                            Log.d(TAG, "Full Dispense is finished!");
                            //do any calculations here to update the remaining amount for this medication
                            medication.setMedication_quantity(medication.getMedication_quantity() - dispenseAmount);
                            // update db with new quantity
                            db.updateMedication(medication);

                            Bundle bundle = createBundle("TakeMedsFragment");
                            mListener.onButtonClicked(bundle);
                        }
                    },
                    new OnErrorListener() {
                        @Override
                        public void onVisionError() {
                            //TODO what do we do if dispense failed?
                            Log.d(TAG, "Vision detection failed !");
                            Bundle bundle = createBundle("CannotRetrieveMedFragment");
                            mListener.onButtonClicked(bundle);
                        }
                        @Override
                        public void onLimitError() {
                            //TODO what do we do if dispense failed?
                            Log.d(TAG, "Limit reached !");
                            Bundle bundle = createBundle("CannotRetrieveMedFragment");
                            mListener.onButtonClicked(bundle);
                        }

                        @Override
                        public void onBadParams(String msg) {
                            //TODO what do we do if dispense failed?
                            Log.d(TAG, msg);
                            Bundle bundle = createBundle("CannotRetrieveMedFragment");
                            mListener.onButtonClicked(bundle);
                        }
                    });

            Log.d(TAG, "Calling full dispense from real UI: " + medication.getMedication_location());
            TinyGDriver.getInstance().doFullDispense(medication.getMedication_location());
        }
    }

    private Bundle createBundle(String fragmentName)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEarlyDispense", isEarlyDispense);
        bundle.putBoolean("isFromSchedule", isFromSchedule);
        bundle.putSerializable("user", user);
        bundle.putSerializable("medication", medication);
        bundle.putString("fragmentName", fragmentName);
        return bundle;
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        tvMedication = view.findViewById(R.id.textview_medication_name);
        tvTitle = view.findViewById(R.id.textview_title);
        tvMedInfo = view.findViewById(R.id.textview_med_info);

        mTextureView = view.findViewById(R.id.camera_preview);
        if (AppConfig.getInstance().isCameraAvailable) {
            mTextureView.setSurfaceTextureListener(surfaceTextureListener);
            mTextureView.getLayoutParams().height = 293;
            mTextureView.getLayoutParams().width = 390;
        }

        btnDev = view.findViewById(R.id.button_developer);
        btnDev.setOnClickListener(this);
        if (AppConfig.getInstance().isTinyGAvailable) {
            btnDev.setVisibility(View.GONE);
        }
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
        if (v == btnDev) {
            medication.setMedication_quantity(medication.getMedication_quantity() - dispenseAmount);
            db.updateMedication(medication);
            bundle = createBundle("TakeMedsFragment");
            mListener.onButtonClicked(bundle);
        }
    }

    /*
     * The listener for when the camera has taken a picture.  Once the picture is taken, it will send
     * the image to the processor to identify meds and their locations.
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();


            //get the bytes
            ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
            final byte[] imageBytes = new byte[imageBuf.remaining()];
            imageBuf.get(imageBytes);
            image.close();

            if (mCamera.isCalibrated()) {
                //Visualizer mVisualizer = new Visualizer(getActivity());
                //mVisualizer.processImage(imageBytes);

                //turn on video
                if (surfaceInitialized) {
                    mCamera.startVideo(mTextureView);
                    Log.d(TAG, "Surface Initialized, Video Should Start!");
                } else {
                    Log.d(TAG, "Surface Is Not Initialized!!");
                }

                //Can't run the processer
                TinyGDriver.getInstance().binImageDone(imageBytes);

            } else {
                String msg, btn;
                if (firstPicture) {
                    msg = "The camera requires calibration.  Please hold a chessboard image (8x8 - black and white checkerboard) under the camera to begin.";
                    btn = "Proceed";
                } else {
                    msg = "Please move the picture slightly and click Proceed";
                    btn = "Proceed";
                }
                Log.d(TAG, "pre dialog");
                firstPicture = false;
                calibrationDialog(msg, btn, imageBytes);
            }
        }
    };

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        /*The surface texture is available, so this is where we will create and open the camera, as
        well as create the request to start the camera preview.
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mCamera.setSurface(surface);
            mCamera.startVideo(mTextureView);
            surfaceInitialized = true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mCamera.shutDown();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void calibrationDialog(String msg, String btn, final byte[] imageBytes) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle("Camera Calibration");
        alert.setMessage(msg);

        alert.setPositiveButton(btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                boolean flgFinished = mCamera.calibrate(imageBytes);
                mCamera.setCalibrated(flgFinished);
                Log.d(TAG, "post dialog finished?:" + flgFinished);
                mCamera.takePicture();
            }
        });

        alert.show();
    }
}