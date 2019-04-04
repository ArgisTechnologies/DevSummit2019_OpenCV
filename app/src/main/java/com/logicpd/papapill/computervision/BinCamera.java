package com.logicpd.papapill.computervision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.logicpd.papapill.computervision.detection.DetectorException;
import com.logicpd.papapill.data.AppConfig;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.CAMERA_SERVICE;

/*
 * This class represents the camera that is on the picker arm.  This will handle the device,
 * capturing images, and basic camera and image utilities as needed.
 * Logic PD - Brady Hustad - 2018-06-29 - Initial Creation
 * Logic PD - Brady Hustad - 2018-08-03 - Added Calibration from various examples, primarily Huiying S. Blog
 *  - http://computervisionandjava.blogspot.com/2013/10/camera-cailbration.html
 */
public class BinCamera {

    //Class Variables
    private static final String TAG = BinCamera.class.getSimpleName();

    private static final int MAX_IMAGES = 1;
    private static final int IMG_FORMAT = ImageFormat.JPEG;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private ImageReader mImageReader;

    //Stuff for the camera capture
    private Surface mPreviewSurface;
    private android.util.Size[] mSizes;
    private CaptureRequest.Builder mRequestBuilder;
    //private TextureView mTextureView;

    //After Calibration is done these settings manually set to keep camera calibrated without repeating.
    //Calibration only has to be done a single time for a specific camera, theory states it should be
    //identical for every camera in the same place and the same model
    private static final boolean CAMERA_CALIBRATED = true;
    private static final Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64F);
    private static final Mat distortionCoefficients = Mat.zeros(8, 1, CvType.CV_64F);

    //Camera Calibration
    private boolean calibrated;
    private final ArrayList<byte[]> calibImages = new ArrayList<>();
    private Size patternSize;
    private final TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 40, 0.001);
    private final Size winSize = new Size(5, 5);
    private final Size zoneSize = new Size(-1, -1);

    //Camera Calibration Output
    private final AppConfig dConfig;
    private final Mat camMatrix = Mat.eye(3, 3, CvType.CV_64F);
    private final Mat distCoeffs = Mat.zeros(8, 1, CvType.CV_64F);
    private final ArrayList rvecs = new ArrayList();
    private final ArrayList tvecs = new ArrayList();

    private static Handler mHandler;

    //Lazy-loaded singleton to keep a single camera instance only.
    private BinCamera() {
        dConfig = AppConfig.getInstance();
    }

    private static class InstanceHolder {
        private static final BinCamera mCamera = new BinCamera();
    }

    public static BinCamera getInstance() {
        return InstanceHolder.mCamera;
    }
    //END singleton code

    /**
     * Initialize the Camera
     */
    public void initializeCamera(Context context, Handler backgroundHandler,
                                 ImageReader.OnImageAvailableListener imageAvailableListener,
                                 TextureView tView) throws DetectorException {

        //mTextureView = tView;

        CameraManager manager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        String[] camIds;

        try {
            camIds = manager.getCameraIdList();

            //The capabilities of the specified camera. On my Nexus 5, 1 is back camera.
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camIds[0]);

            /*A map that contains all the supported sizes and other information for the camera.
            Check the documentation for more information on what is available.
             */
            StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);

        } catch (CameraAccessException cae) {
            Log.e(TAG, "Camera access exception getting IDs", cae);
            throw new DetectorException(cae.getMessage());
        } catch (NullPointerException npe) {
            Log.e(TAG, "No Cameras found", npe);
            throw new DetectorException(npe.getMessage());
        }

        Log.d(TAG, "Cameras Found: " + camIds.length + " first camera: " + camIds[0]);
        String id = camIds[0];


        mImageReader = ImageReader.newInstance(AppConfig.getInstance().getImageWidth(), AppConfig.getInstance().getImageHeight(), IMG_FORMAT, MAX_IMAGES);
        mImageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);

        mHandler = backgroundHandler;

        //Open Camera Resource
        try {
            manager.openCamera(id, mStateCallback, backgroundHandler);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Camera access exception", cae);
        }

        //Calibration Initialization
        setCalibrated(CAMERA_CALIBRATED);
        patternSize = new Size(7,7);
    }

    /****************************************************************************
     * Getters and Setters
     ****************************************************************************/
    public boolean isCalibrated() {
        return calibrated;
    }

    public void setCalibrated(boolean calibrated) {
        this.calibrated = calibrated;
    }

    public void setSurface(SurfaceTexture texture) {
        mPreviewSurface = new Surface(texture);
    }

    public Mat getCameraMatrix() {
        if (dConfig.getCameraMatrix() != null) {
            cameraMatrix.put(0, 0, dConfig.getCameraMatrix());
        } else{
            cameraMatrix.put(0, 0, new double[9]);
            setCalibrated(false);
        }

        return cameraMatrix;
    }

    public Mat getDistortionCoefficients() {
        if (dConfig.getDistortionCoeffs() != null) {
            distortionCoefficients.put(0, 0, dConfig.getDistortionCoeffs());
        } else {
            distortionCoefficients.put(0, 0, new double[5]);
            setCalibrated(false);
        }

        return distortionCoefficients;
    }

    /****************************************************************************
     * Callbacks
     ****************************************************************************/

    /*
     * This method handles Camera state and any cleanup and responses required.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "Opened Camera");
            mCameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera disconnected, closing.");
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, "Camera device error, closing.");
            camera.close();
            mCameraDevice = null;
        }
    };

    /*
     * This method handles callbacks for a picture capture session.
     */
    private final CameraCaptureSession.StateCallback mSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            if (mCameraDevice == null) {
                return;
            }

            mCaptureSession = session;
            triggerImageCapture();
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "Failed to configure camera.");
        }
    };

    /*
     * This method handles the callbacks for picture capture directly.
     */
    private final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);

            Log.d(TAG, "Camera Image Capture Partial Result");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            session.close();
            mCaptureSession = null;

            Log.d(TAG, "Camera Capture Session completed and closed.");
        }
    };

    /*****************************************************************************
     * End Callbacks
     *****************************************************************************/

    private void triggerImageCapture() {
        try {
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            Log.d(TAG, "Camera capture session initialized.");
            mCaptureSession.capture(captureBuilder.build(), mCaptureCallback, null);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Camera capture exception", cae);
        }
    }

    /*
     * Fire the process to capture a still image. This finishes with the OnImageAvailableListener
     * in the ManageMedsFragment
     */
    public void takePicture() {
        if (mCameraDevice == null) {
            Log.e(TAG, "No Camera is initialized.");
            return;
        }

        try {
            mCameraDevice.createCaptureSession(Collections.singletonList(mImageReader.getSurface()),
                    mSessionCallback, mHandler);
        } catch (CameraAccessException cae) {
            Log.e(TAG, "Access exception while capturing the picture", cae);
        }
    }

    /*
     * Shuts the camera down and closes the resources
     */
    public void shutDown() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
    }

    public boolean calibrate(byte[] imageBytes) {
        ArrayList<Mat> mats;
        ArrayList objectPoints, imagePoints;
        final int flagsCalib = Calib3d.CALIB_ZERO_TANGENT_DIST | Calib3d.CALIB_FIX_PRINCIPAL_POINT | Calib3d.CALIB_FIX_K4 | Calib3d.CALIB_FIX_K5;
        boolean flgFinished = false;

        calibImages.add(imageBytes);

        if (calibImages.size() >= 20) {
            mats = new ArrayList<>();
            objectPoints = new ArrayList();
            imagePoints = new ArrayList();
            MatOfPoint3f corners3f = getCorner3f();

            //Time to calibrate
            for (byte[] img: calibImages) {
                Mat matColor = new Mat();
                Bitmap bmapImage = BitmapFactory.decodeByteArray(img, 0, img.length);
                Utils.bitmapToMat(bmapImage, matColor); //matColor is Type CV_8UC4; (Type = 24)

                Log.d(TAG, "----> Mat Channels, Rows, Cols:" + matColor.channels() + ", " + matColor.rows() + ", " + matColor.cols());

                Mat matGrey = new Mat();

                Imgproc.cvtColor(matColor, matGrey, Imgproc.COLOR_BGR2GRAY);

                MatOfPoint2f corners = new MatOfPoint2f();
                if (getCorners(matGrey, corners)) {
                    Log.d(TAG, "Corners detected");
                    objectPoints.add(corners3f);
                    imagePoints.add(corners);
                    mats.add(matColor);
                } else {
                    Log.d(TAG, "NO Corners detected");
                }
            }

            double errReproj = Calib3d.calibrateCamera(objectPoints, imagePoints, mats.get(0).size(), camMatrix, distCoeffs, rvecs, tvecs, flagsCalib);
            flgFinished = true;

            Log.d(TAG, "Calibrated: Errors:" + + errReproj);
            Log.d(TAG, "camMatrix:->" + camMatrix.dump());
            Log.d(TAG, "distCoeffs:->" + distCoeffs.dump());
        }

        return flgFinished;
    }

    private MatOfPoint3f getCorner3f() {
        MatOfPoint3f c3f = new MatOfPoint3f();
        double squareSize = 8; //It says it can be in pixels or millimeters, mine is millimeters.
        Point3[] vp = new Point3[(int) (patternSize.height * patternSize.width)];
        int cnt = 0;
        for (int i = 0; i < patternSize.height; i++) {
            for (int j = 0; j < patternSize.width; j++, cnt++) {
                vp[cnt] = new Point3(j * squareSize, i * squareSize, 0.0d);
            }
        }

        c3f.fromArray(vp);
        return c3f;
    }

    private boolean getCorners(Mat greyMat, MatOfPoint2f corners) {
        final int flagsCorner = Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FAST_CHECK | Calib3d.CALIB_CB_NORMALIZE_IMAGE;
        if (!Calib3d.findChessboardCorners(greyMat, patternSize, corners, flagsCorner)) {
            return false;
        }
        Imgproc.cornerSubPix(greyMat, corners, winSize, zoneSize, criteria);

        return true;
    }

    /**********************************************
     * Video Capture
     **********************************************/
    public void startVideo(TextureView tv) {
        try {
            //Used to create the surface for the preview.
            //mTextureView = tv;
            SurfaceTexture surfaceTexture = tv.getSurfaceTexture();

            /*VERY IMPORTANT.  THIS MUST BE SET FOR THE APP TO WORK.  THE CAMERA NEEDS TO KNOW ITS PREVIEW SIZE.*/
            surfaceTexture.setDefaultBufferSize(mSizes[2].getWidth(), mSizes[2].getHeight());

                /*A list of surfaces to which we would like to receive the preview.  We can specify
                more than one.*/
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(mPreviewSurface);

                /*We humbly forward a request for the camera.  We are telling it here the type of
                capture we would like to do.  In this case, a live preview.  I could just as well
                have been CameraDevice.TEMPLATE_STILL_CAPTURE to take a singe picture.  See the CameraDevice
                docs.*/
            mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mRequestBuilder.addTarget(mPreviewSurface);

            //A capture session is now created. The capture session is where the preview will start.
            mCameraDevice.createCaptureSession(surfaces, videoPreviewSessionStateCallback, mHandler);

        } catch (CameraAccessException e) {
            Log.e("Camera Exception", e.getMessage());
        }
    }

    /**
     * The CameraCaptureSession.StateCallback class  This is where the preview request is set and started.
     */
    private final CameraCaptureSession.StateCallback videoPreviewSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                /* We humbly set a repeating request for images.  i.e. a preview. */
                session.setRepeatingRequest(mRequestBuilder.build(), videoPreviewSessionCallback, mHandler);
            } catch (CameraAccessException e) {
                Log.e("Camera Exception", e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private final CameraCaptureSession.CaptureCallback videoPreviewSessionCallback = new CameraCaptureSession.CaptureCallback() {
        @SuppressWarnings("EmptyMethod")
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @SuppressWarnings("EmptyMethod")
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };
}
