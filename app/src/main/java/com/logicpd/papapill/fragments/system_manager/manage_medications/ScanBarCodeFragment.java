package com.logicpd.papapill.fragments.system_manager.manage_medications;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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

import com.logicpd.papapill.App;
import com.logicpd.papapill.R;
import com.logicpd.papapill.computervision.detection.DetectorException;
import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.computervision.barcode.BarcodeData;
import com.logicpd.papapill.computervision.barcode.BarcodeException;
import com.logicpd.papapill.computervision.barcode.BarcodeReader;
import com.logicpd.papapill.computervision.barcode.BarcodeTypes;
import com.logicpd.papapill.computervision.BinCamera;
import com.logicpd.papapill.computervision.barcode.MedicationBarcodeData;
import com.logicpd.papapill.computervision.barcode.RawBarcodeData;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.utils.TextUtils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static com.logicpd.papapill.misc.AppConstants.TAG;

/**
 * Blank fragment template
 *
 * @author alankilloren
 */
public class ScanBarCodeFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "ScanBarCodeFragment";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private Button btnScanMedication;
    private OnButtonClickListener mListener;
    private User user;

    private BinCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private TextureView mTextureView;
    private int cntTries = 0;
    private BarcodeReader mBarcodeReader;
    private BarcodeTypes mType;
    private boolean surfaceInitialized = false;

    public ScanBarCodeFragment() {
        // Required empty public constructor
    }

    public static ScanBarCodeFragment newInstance() {
        return new ScanBarCodeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppConfig.getInstance().isCameraAvailable) {
            //Setup Camera Thread
            mCameraThread = new HandlerThread("CameraBackground");
            mCameraThread.start();
            mCameraHandler = new Handler(mCameraThread.getLooper());

            //mVisualizer = new Visualizer(this.getActivity());

        try {
            mCamera = BinCamera.getInstance();
            mCamera.initializeCamera(getActivity(), mCameraHandler, mOnImageAvailableListener, mTextureView);
        } catch (DetectorException de) {
            Log.e(TAG, "Error in Camera Initialization", de);
        }

            mBarcodeReader = new BarcodeReader();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_meds_read_barcode, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("user")) {
                user = (User) bundle.getSerializable("user");
            }
        }

        if (AppConfig.getInstance().isCameraAvailable)
            mTextureView.setSurfaceTextureListener(surfaceTextureListener);

        Log.d(AppConstants.TAG, SCREEN_NAME + " displayed");
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnScanMedication = view.findViewById(R.id.button_scan_medication_barcode);
        btnScanMedication.setOnClickListener(this);
        mTextureView = view.findViewById(R.id.fake_scan_textview);

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
        if (v == btnScanMedication) {
            if (AppConfig.getInstance().isCameraAvailable) {
                cntTries = 0;
                mType = BarcodeTypes.MEDICATION;
                mCamera.takePicture();
                TextUtils.disableButton(btnScanMedication);
            }
        }

        //TODO - handle other button clicks here
    }

    /*
     * The listener for when the camera has taken a picture.  Once the picture is taken, it will send
     * the image to the processor to identify meds and their locations.
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();

            if (surfaceInitialized) {
                mCamera.startVideo(mTextureView);
            } else {
                mTextureView.setSurfaceTextureListener(surfaceTextureListener);
            }

            //get the bytes
            ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
            final byte[] imageBytes = new byte[imageBuf.remaining()];
            imageBuf.get(imageBytes);
            image.close();
            final Bitmap bcImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, null);

            mBarcodeReader.setBarcodeListener(new BarcodeReader.BarcodeReaderListener() {
                @Override
                public void onSuccess(BarcodeData bData) {
                    TextUtils.enableButton(btnScanMedication);
                    cntTries = 0;

                    if (bData.getType() == BarcodeTypes.MEDICATION) {
                        //Possible check to make sure this is a valid search? Probably pointless in this mode
                        MedicationBarcodeData mdb = (MedicationBarcodeData) bData;
                        Log.d(TAG, "Barcode is medication->" + mdb.getMedication().getName());
                    } else {
                        cntTries++;
                        if (cntTries < 10) {
                            Log.d(TAG, "Barcode Is Not Medication, trying again.");
                            mCamera.takePicture();
                        } else {
                            Log.d(TAG, "Barcode Read Failed, never detected barcode.");
                        }
                    }

                    int cntKeys = 1;
                    Iterator<String> it = bData.getRawDataKeys().iterator();

                    Mat drawableImage = new Mat();
                    Utils.bitmapToMat(bData.getBaseImage(), drawableImage);
                    MedicationBarcodeData mbd = (MedicationBarcodeData) bData;
                    //TODO: This mbd object has a Medication object to load into the database.

                    while (it.hasNext()) {
                        String key = it.next();
                        RawBarcodeData rbData = bData.getRawData(key);

                        Rect r = rbData.getBounds();
                        Imgproc.rectangle(drawableImage, new Point(r.left, r.top), new Point(r.right, r.bottom), new Scalar(0, 255, 0), 2);

                        cntKeys++;
                    }
                    Utils.matToBitmap(drawableImage, bcImage);
                    drawableImage.release();
                }

                @Override
                public void onFailure(final BarcodeData bData) {
                    cntTries++;
                    if (cntTries < 10) {
                        Log.d(TAG, "Barcode Read Failed, trying again.");
                        mCamera.takePicture();
                    } else {
                        Log.d(TAG, "Barcode Read Failed, never detected barcode.");
                        TextUtils.enableButton(btnScanMedication);
                    }
                }
            });

            try {
                if (mType == BarcodeTypes.MEDICATION)
                    mBarcodeReader.readMedicationBarcode(bcImage);
                else
                    mBarcodeReader.readBinBarcode(bcImage);

            } catch (BarcodeException be) {
                Log.d(TAG, "Barcode Read Failed via Exception, never detected barcode.");
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
}
