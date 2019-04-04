package com.logicpd.papapill.fragments.help;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.logicpd.papapill.R;
import com.logicpd.papapill.computervision.barcode.BarcodeData;
import com.logicpd.papapill.computervision.barcode.BarcodeException;
import com.logicpd.papapill.computervision.barcode.BarcodeReader;
import com.logicpd.papapill.computervision.barcode.BarcodeTypes;
import com.logicpd.papapill.computervision.barcode.BinBarcodeData;
import com.logicpd.papapill.computervision.BinCamera;
import com.logicpd.papapill.computervision.detection.DetectionData;
import com.logicpd.papapill.computervision.detection.Detector;
import com.logicpd.papapill.computervision.detection.DetectorException;
import com.logicpd.papapill.computervision.ImageUtility;
import com.logicpd.papapill.computervision.barcode.MedicationBarcodeData;
import com.logicpd.papapill.computervision.barcode.RawBarcodeData;
import com.logicpd.papapill.computervision.detection.PillLocation;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.interfaces.OnDetectionCompleteListener;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import static com.logicpd.papapill.misc.AppConstants.TAG;

/**
 * This test fragment for devs
 *
 * @author alankilloren
 */
public class DeveloperTestFragment extends Fragment implements View.OnClickListener, OnDetectionCompleteListener {

    private static final String SCREEN_NAME = "Vision Developer Test";

    RelativeLayout contentLayout;
    LinearLayout backButton, homeButton;
    OnButtonClickListener mListener;
    TextView tvDevText;
    ImageView ivDevImage;
    Button btnTest, btnTestEmpty, btnTemplate, btnAnalysis, btnBarcode,
            btnBinBarcode, btnHistogram, btnStoreImage;
    RadioButton rbNo, rb0, rb25, rb50, rb75, rb100;
    Handler h;

    private BinCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private TextureView mTextureView;
    private boolean firstPicture = true;
    private DetectionData mData = null;
    private int mBinId = -1;
    private boolean flgBarcode = false;
    private boolean flgHistogram = false;
    private boolean flgStoreImage = false;
    private int cntTries = 0;
    private BarcodeReader mBarcodeReader;
    private BarcodeTypes mType;

    public DeveloperTestFragment() {
        // Required empty public constructor
    }

    public static DeveloperTestFragment newInstance() {
        return new DeveloperTestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup Camera Thread
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());

        //mVisualizer = new Visualizer(this.getActivity());

        mCamera = BinCamera.getInstance();
        try {
            mCamera.initializeCamera(getActivity(), mCameraHandler, mOnImageAvailableListener, mTextureView);
        } catch (DetectorException de) {
            Log.e(TAG, "Error in Camera Initialization", de);
        }

        mBarcodeReader = new BarcodeReader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_developer_test, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        h = new Handler();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //TODO - handle passed in bundle
        }
    }

    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnTest = view.findViewById(R.id.button_test);
        btnTest.setOnClickListener(this);
        btnTestEmpty = view.findViewById(R.id.button_test_2);
        btnTestEmpty.setOnClickListener(this);
        btnTemplate = view.findViewById(R.id.button_template);
        btnTemplate.setOnClickListener(this);
        btnAnalysis = view.findViewById(R.id.button_deep);
        btnAnalysis.setOnClickListener(this);
        btnBarcode = view.findViewById(R.id.button_barcode);
        btnBarcode.setOnClickListener(this);
        btnBinBarcode = view.findViewById(R.id.button_bin_barcode);
        btnBinBarcode.setOnClickListener(this);
        btnHistogram = view.findViewById(R.id.button_histogram);
        btnHistogram.setOnClickListener(this);
        btnStoreImage = view.findViewById(R.id.button_store_image);
        btnStoreImage.setOnClickListener(this);
        tvDevText = view.findViewById(R.id.developer_text);
        ivDevImage = view.findViewById(R.id.developer_image);
        mTextureView = view.findViewById(R.id.fake_textview);
        rbNo = view.findViewById(R.id.devtest_calib_no);
        rb0 = view.findViewById(R.id.devtest_calib_0);
        rb25 = view.findViewById(R.id.devtest_calib_25);
        rb50 = view.findViewById(R.id.devtest_calib_50);
        rb75 = view.findViewById(R.id.devtest_calib_75);
        rb100 = view.findViewById(R.id.devtest_calib_100);
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
        flgBarcode = false;
        if (v == backButton) {
            bundle.putString("fragmentName", "Back");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnTest) {
            flgBarcode = false;
            flgHistogram = false;
            flgStoreImage = false;
            mBinId = 1;
            mCamera.takePicture();
        }
        if (v == btnTestEmpty) {
            flgBarcode = false;
            flgHistogram = false;
            flgStoreImage = false;
            mBinId = 2;
            mCamera.takePicture();
        }
        if (v == btnAnalysis) {
            flgBarcode = false;
            flgHistogram = false;
            flgStoreImage = false;
            mBinId = 1;
        }
        if (v == btnTemplate) {
            flgBarcode = false;
            flgHistogram = false;
            flgStoreImage = false;
            ImageUtility.buildTemplates();
        }
        if (v == btnBarcode) {
            flgBarcode = true;
            flgHistogram = false;
            flgStoreImage = false;
            cntTries = 0;
            mType = BarcodeTypes.MEDICATION;
            mCamera.takePicture();
        }
        if (v == btnBinBarcode) {
            flgBarcode = true;
            flgHistogram = false;
            flgStoreImage = false;
            cntTries = 0;
            mType = BarcodeTypes.BIN;
            mCamera.takePicture();
        }
        if (v == btnHistogram) {
            flgHistogram = true;
            flgBarcode = false;
            flgStoreImage = false;
            mCamera.takePicture();
        }
        if (v == btnStoreImage) {
            flgHistogram = false;
            flgBarcode = false;
            flgStoreImage = true;
            mCamera.takePicture();
        }
    }

    /*
     * This runs the background process test to analysis (Bin 1) and create a record to give
     * analysis boost.
     */
    private void runBackgroundTask() {
        Detector mDetector = new Detector(this.getContext());
        mData = mDetector.backgroundDetection(mBinId);
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
                if (flgBarcode) {
                    //Do the barcode information.
                    //BarcodeReader bcReader = new BarcodeReader();
                    final Bitmap bcImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, null);

                    mBarcodeReader.setBarcodeListener(new BarcodeReader.BarcodeReaderListener() {
                        @Override
                        public void onSuccess(BarcodeData bData) {
                            flgBarcode = false;
                            cntTries = 0;

                            final StringBuilder sb = new StringBuilder("Returned Data:\n");
                            sb.append("Status: Success");
                            if (bData.getType() == BarcodeTypes.MEDICATION) {
                                MedicationBarcodeData mbd = (MedicationBarcodeData)bData;
                                sb.append("\nType: Medication");
                                sb.append("\nPatient: ");
                                sb.append(mbd.getMedication().getPatient_name());
                                sb.append("\nMed Name: ");
                                sb.append(mbd.getMedication().getName());
                                sb.append("\nPill Strength: ");
                                sb.append(mbd.getMedication().getStrength_value());
                                sb.append(" ");
                                sb.append(mbd.getMedication().getStrength_measurement());
                            } else if (bData.getType() == BarcodeTypes.BIN) {
                                BinBarcodeData bbd = (BinBarcodeData)bData;
                                sb.append("\nType: Bin");
                                sb.append(" Bin Id: ");
                                sb.append(bbd.getBinId());
                            } else {
                                sb.append("\nType: Other");
                            }

                            int cntKeys = 1;
                            Iterator<String> it = bData.getRawDataKeys().iterator();

                            Mat drawableImage = new Mat();
                            Utils.bitmapToMat(bData.getBaseImage(), drawableImage);

                            while(it.hasNext()){
                                String key = it.next();
                                RawBarcodeData rbData = bData.getRawData(key);
                                sb.append("\nBarcode: ");
                                sb.append(cntKeys);
                                sb.append(" Barcode Type: ");
                                if (rbData.getFormat() == FirebaseVisionBarcode.FORMAT_DATA_MATRIX) {
                                    sb.append("Data Matrix");
                                } else if (rbData.getFormat() == FirebaseVisionBarcode.FORMAT_UPC_A || rbData.getFormat() == FirebaseVisionBarcode.FORMAT_UPC_E) {
                                    sb.append("UPC");
                                } else {
                                    sb.append("Other");
                                }
                                sb.append("\nData: ");
                                sb.append(rbData.getRawData());
                                Rect r = rbData.getBounds();
                                Imgproc.rectangle(drawableImage, new Point(r.left, r.top), new Point(r.right, r.bottom), new Scalar(0,255,0), 2);

                                cntKeys++;
                            }
                            Utils.matToBitmap(drawableImage, bcImage);
                            drawableImage.release();

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvDevText.setText(sb.toString());
                                    ivDevImage.setImageBitmap(bcImage);
                                }
                            });
                        }

                        @Override
                        public void onFailure(final BarcodeData bData) {
                            cntTries++;
                            if (cntTries < 10) {
                                Log.d(TAG, "Barcode Read Failed, trying again.");
                                mCamera.takePicture();
                            } else {
                                Log.d(TAG, "Barcode Read Failed, never detected barcode.");
                                flgBarcode = false;

                                final StringBuilder sb = new StringBuilder("Returned Data:\n");
                                sb.append("Status: Failed");
                                sb.append("\nError: ");
                                sb.append(bData.getErrorMessage());
                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvDevText.setText(sb.toString());
                                        ivDevImage.setImageBitmap(bData.getBaseImage());
                                    }
                                });
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
                        flgBarcode = false;

                        final StringBuilder sb = new StringBuilder("Returned Data:\n");
                        sb.append("Status: Failed (Exception)");
                        sb.append("\nError: ");
                        sb.append(be.getMessage());
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDevText.setText(sb.toString());
                                ivDevImage.setImageBitmap(bcImage);
                            }
                        });
                    }
                } else if (flgHistogram) {
                    Detector mDetector = new Detector();

                    //try {
                        //double rotation = mDetector.adjustBinAlignment(imageBytes, 1);
                        final StringBuilder sb = new StringBuilder("Returned Data:\n");
                        sb.append("Rotation Adjustment:");
                        //sb.append(rotation);

                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDevText.setText(sb.toString());
                                //ivDevImage.setImageBitmap(bcImage);
                            }
                        });
//                    } catch (final DetectorException de) {
//                        Log.e(TAG, "Error in Histogram:" + de.getMessage());
//                        h.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvDevText.setText(de.getMessage());
//                                //ivDevImage.setImageBitmap(bcImage);
//                            }
//                        });
//                    }
                } else if (flgStoreImage) {
                    Detector mDetector = new Detector();

                    try {
                        mDetector.storeBinImage(imageBytes, 1);
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDevText.setText("Image Stored To SD Card");
                                //ivDevImage.setImageBitmap(bcImage);
                            }
                        });
                    } catch (final DetectorException de) {
                        Log.e(TAG, "Error in Image Storage:" + de.getMessage());
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDevText.setText(de.getMessage());
                                //ivDevImage.setImageBitmap(bcImage);
                            }
                        });
                    }
                } else {
                    if (rbNo.isChecked()) {
                        Detector mDetector = new Detector();
                        mData = mDetector.processImage(imageBytes, mBinId);

                        final StringBuilder sb = new StringBuilder("Returned Data:\n");
                        sb.append("Status: ");
                        sb.append(mData.getStatus());
                        sb.append("\nError: ");
                        sb.append(mData.getErrorMessage());
                        sb.append("\nPill Count: ");
                        sb.append(mData.getPillCount());
                        for (PillLocation pl: mData.getSelectedPills()) {
                            sb.append("\nCoordinates:\nrad: ");
                            sb.append(String.format("%.3f", pl.getCoordinateData().radius));
                            sb.append(" deg: ");
                            sb.append(String.format("%.3f", pl.getCoordinateData().degrees));
                            sb.append(" z: ");
                            sb.append(String.format("%.2f", pl.getCoordinateData().z));
                            sb.append(" mmZ: ");
                            sb.append(String.format("%.2f", pl.getCoordinateData().mmZ));
                            sb.append("\nPixel Coords:\nx: ");
                            sb.append(String.format("%.3f", pl.getPixelData().x));
                            sb.append(" y: ");
                            sb.append(String.format("%.3f", pl.getPixelData().y));
                        }
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDevText.setText(sb.toString());
                                ivDevImage.setImageBitmap(mData.getSelectedPill());
                            }
                        });
                    } else {
                        double height = 0;
                        if (rb0.isChecked())
                            height = 0;
                        else if (rb25.isChecked())
                            height = 25;
                        else if (rb50.isChecked())
                            height = 50;
                        else if (rb75.isChecked())
                            height = 75;
                        else
                            height = 100;

                        Bitmap bmapImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        final Bitmap bMap = ImageUtility.buildScaleCalibrationImage(bmapImage, height);
                        final String devText = "Calibration Height: " + height;
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDevText.setText(devText);
                                ivDevImage.setImageBitmap(bMap);
                            }
                        });
                    }
                }
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

    @Override
    public void onDetectionCompleted(DetectionData data) {
        mData = data;

        StringBuilder sb = new StringBuilder("Returned Data:\n");
        sb.append("Status: ");
        sb.append(mData.getStatus());
        sb.append("\nError: ");
        sb.append(mData.getErrorMessage());
        sb.append("\nPill Count: ");
        sb.append(mData.getPillCount());
        for (PillLocation pl: mData.getSelectedPills()) {
            sb.append("\nCoordinates:\nrad: ");
            sb.append(pl.getCoordinateData().radius);
            sb.append(" deg: ");
            sb.append(pl.getCoordinateData().degrees);
            sb.append(" z: na");
            //sb.append(data.getCoordinateData().)
        }

        tvDevText.setText(sb.toString());
        ivDevImage.setImageBitmap(mData.getSelectedPill());

    }
}