package com.logicpd.papapill.computervision.detection;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.logicpd.papapill.App;
import com.logicpd.papapill.R;
import com.logicpd.papapill.computervision.BinCamera;
import com.logicpd.papapill.computervision.ImageUtility;
import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.data.BinDetection;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.device.models.CoordinateData;
import com.logicpd.papapill.interfaces.OnDetectionCompleteListener;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * This class manages the processing of the image to find, locate and count medications in a bin.
 * For confirming bin identity, this will call a barcode reading class.
 * Logic PD - Brady Hustad - 2018-06-29 - Initial Creation
 * Logic PD - Brady Hustad - 2018-07-19 - Built Thresholding and Contours, removing extra camera visuals.
 *     And added tuning variables.
 */
public class Detector {

    //Class Variables
    private static final String TAG = Detector.class.getSimpleName();
    private static final boolean flgWriteImage = true;
    private boolean mTestRun = false;
    private boolean mCannyReplacementTestRun = false;
    private Mat mCannyReplaceMat;

    //Tuning Variables.  These are variables that likely will have to be tuned as part of implementing
    //within the final environment, and as the system is improved.
    private static final int CONTOUR_AREA_LIMIT = 950; //This is how big of an area the threshold picks up we should keep.  An m&m is around 10000.  Most random bright points are around 500 or less.
    private static final int EDGE_LOW_THRESHOLD = 10; //This is the current low threshold for the edge detection. (13)
    private static final int EDGE_HIGH_THRESHOLD = 30; //This is the current high threshold for the edge detection. (32)
    private static final int THRESHOLD_INTENSITY_LIMIT = 50; //This is the intensity from 0 to 255 that should be removed, anything below this number is removed via threshold.
    private static final int HOUGH_THRESHOLD = 13; //This is the current threshold for the hough circle detection.

    //This is the threshold to limit how intense of pixels are allowed to create blobs.
    // ORANGE M&M
    //public static final double PERCENT_FOR_BLOB_THRESHOLD = 0.360; //Grey for Blob. Full Bin
    //public static final double PERCENT_FOR_BLOB_THRESHOLD = 0.38; //Grey for Blob. Medium Bin
    //public static final double PERCENT_FOR_BLOB_THRESHOLD = 0.36; //Grey for Blob. Nearly Empty Bin
    //New Shrouded Numbers - We put the device under a shroud which should standardize light impact.
    private static final double PERCENT_FOR_BLOB_THRESHOLD = 0.380;

    //Variables for Threshold Test
    private int ptrLow = 0;
    private int ptrHigh = 0;
    private int ptrThresh = 0;

    //Chosen Pills
    double fillMmZ = 0d;
    double fillZ = 0d;
    private ArrayList<PillLocation> pills = new ArrayList<PillLocation>();

    //Upper Left points for image reduction to resize and match on templates for fill.
    private int startOfCutX = 0;
    private int startOfCutY = 0;

    //The contour map to cut the inverted image to
    private Mat mContours;

    //The Bin ID for database and improved information available.
    private byte[] mCurrentImageBytes;
    private Context mainContext = null;
    private int mBinId = -1;
    private BinDetection mBinData = null;

    //Mostly for testing to show various images in the edge detection process
    private Dialog imageDialog;

    //Constructors
    public Detector(Context c) {
        this();
        mainContext = c;
    }

    public Detector() {
        if (mainContext == null) {
            mainContext = App.getContext();
        }

        // create picture directory if necessary
        ImageUtility.initializePictureDirectory();

        ptrLow = EDGE_LOW_THRESHOLD;
        ptrHigh = EDGE_HIGH_THRESHOLD;
        ptrThresh = HOUGH_THRESHOLD;

        mCannyReplaceMat = new Mat();
    }

    //********************************************************
    //* Main Methods
    //********************************************************

    /*
     * This method takes an image, parses it into a histogram of the various colors, and determines
     * where the bin edges are, then determines if those are in the correct place, and if not, returns
     * a value to rotate on the theta.  0.0 will returned if either no result is determined, or no
     * rotation is required.
     */
    public void storeBinImage(final byte[] imageBytes, int binId) throws DetectorException {
        Mat matColor = new Mat();
        mCurrentImageBytes = imageBytes;

        Bitmap bmapImage = BitmapFactory.decodeByteArray(mCurrentImageBytes, 0, mCurrentImageBytes.length);
        Utils.bitmapToMat(bmapImage, matColor); //matColor is Type CV_8UC4; (Type = 24)
        matColor = removeDistortion(matColor, bmapImage);
        Utils.matToBitmap(matColor, bmapImage);

        ImageUtility.storeImage(bmapImage, "/bin-" + binId + "-image");
    }

    /*
     * This runs in the background to use captured pictures to do a deep analysis to identify
     * possible pill counts and areas of interest to speed the on the fly detection.
     */
    public DetectionData backgroundDetection(int binId) {
        long startTime = System.currentTimeMillis();
        mBinId = binId;
        Mat matColor = new Mat();
        Mat matOutput = new Mat();

        DetectionData sendData = new DetectionData();

        if (mBinId > 0) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(mainContext);
            mBinData = dbHelper.getBinDetection(mBinId);

            //Check to make sure there is a record to analyze
            if (mBinData != null && mBinData.getDetectImage() != null) {
                Bitmap bmapImage = mBinData.getDetectImage();
                Utils.bitmapToMat(bmapImage, matColor); //matColor is Type CV_8UC4; (Type = 24)

                //Do the deep analysis
                // *********************************
                // * STEP: Remove Out of Bin Image
                // *********************************
                matColor = removeOutsideBin(matColor, bmapImage);

                //This also removes anything out of the bounding box of the bin, removing lots of pixels.
                bmapImage = Bitmap.createBitmap(bmapImage, 0, 0, matColor.cols(), matColor.rows());

                // *********************************
                // * STEP: Remove Area Around Pills
                // *********************************
                matColor = removeAroundPills(matColor, bmapImage);

                // *********************************
                // * STEP: Create Edge Image
                // *********************************
                Mat matEdge = edgeDetection(matColor, bmapImage);

                // *********************************
                // * STEP: Reverse Edge and create solid pill structures
                // *********************************
                matOutput = reverseEdge(matEdge, bmapImage);

                dbHelper.deleteBinDetection(mBinId);
                dbHelper.addBinDetection(mBinData);

                pills.add(new PillLocation());

                sendData = new DetectionData(0, DetectionStatus.SUCCESSFUL, "", bmapImage, pills);
            } else {
                sendData.setErrorMessage("No Database Record to Analyze");
                sendData.setStatus(DetectionStatus.ABORTED);
            }
        } else {
            sendData.setErrorMessage("No Bin Selected to Analyze");
            sendData.setStatus(DetectionStatus.ABORTED);
        }
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "*****Time for Process:" + (endTime - startTime));

        return sendData;
    }

    /*
     * This is an overload to allow a specific test.  Once this test is determined this code, and the according tests,
     * should be deprecated and deleted.  This tests if the improved edge detection makes a signification difference in the
     * ability to select pills.
     */
    public DetectionData processImage(final byte[] imageBytes, int binId, boolean testRun, final byte[] edgePic, boolean edgeTest) {
        mCannyReplacementTestRun = edgeTest;

        Bitmap bmapImage = BitmapFactory.decodeByteArray(edgePic, 0, edgePic.length);
        Utils.bitmapToMat(bmapImage, mCannyReplaceMat); //matColor is Type CV_8UC4; (Type = 24)

        return processImage(imageBytes, binId, testRun);
    }

    /*
     * This is an overload to allow a test run.  This will set a class variable that allows
     * critical decisions to use test settings when applicable.
     */
    public DetectionData processImage(final byte[] imageBytes, int binId, boolean testRun) {
        mTestRun = testRun;
        return processImage(imageBytes, binId);
    }

    /*
     * This is an overload for capturing the bin id.
     */
    public DetectionData processImage(final byte[] imageBytes, int binId) {
        //Handle Bin Id
        mBinId = binId;
        return processImage(imageBytes);
    }

    /*
     * This receives the image from the camera and instigates the processing of the image and the
     * locating of the medication.  This will, in the future, also estimate pill count, and confirm
     * proper bin delivery.
     */
    public DetectionData processImage(final byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        mCurrentImageBytes = imageBytes;
        Mat matColor = new Mat();
        Mat matOutput = new Mat();
        Mat matOriginal = new Mat();
        DatabaseHelper dbHelper  = DatabaseHelper.getInstance(mainContext);

        if (mTestRun) {
            mBinData = null;
        } else {
            if (mBinId > 0) {
                mBinData = dbHelper.getBinDetection(mBinId);
            }
        }

        Bitmap bmapImage = BitmapFactory.decodeByteArray(mCurrentImageBytes, 0, mCurrentImageBytes.length);
        Utils.bitmapToMat(bmapImage, matColor); //matColor is Type CV_8UC4; (Type = 24)

        if (flgWriteImage)
            ImageUtility.storeImage(bmapImage, "/1a-Original");

        // *********************************
        // * STEP: Undistort Image
        // *********************************
        if (!mTestRun)
            matColor = removeDistortion(matColor, bmapImage);

        //Always setup after a picking process to re-analyze the bin
        if (mBinData == null) {
            mBinData = new BinDetection();
            mBinData.setUpperLeftX(-1);
            mBinData.setUpperLeftY(-1);
            mBinData.setLowerRightX(-1);
            mBinData.setLowerRightY(-1);
        }

        if (mBinId > 0) {
            Utils.matToBitmap(matColor, bmapImage);
            mBinData.setDetectImage(bmapImage);
            mBinData.setBinId(mBinId);
        }

        //This starts using the pre-stored information to improve and speed analysis
        Log.d(TAG, "Bin ID:" + mBinId + " ULX:" + mBinData.getUpperLeftX());
        if (mBinId > 0 && mBinData.getUpperLeftX() >=0) {
            //Process using stored information

            // *********************************
            // * STEP: Remove Excess Image
            // *********************************
            matColor = trimToFocusArea(matColor, bmapImage);

            //This also removes anything out of the bounding box of the bin, removing lots of pixels.
            bmapImage = Bitmap.createBitmap(bmapImage, 0, 0, matColor.cols(), matColor.rows());

        } else {
            //Process without using any prestored information

            // *********************************
            // * STEP: Remove Out of Bin Image
            // *********************************
            matColor = removeOutsideBin(matColor, bmapImage);

            //This also removes anything out of the bounding box of the bin, removing lots of pixels.
            bmapImage = Bitmap.createBitmap(bmapImage, 0, 0, matColor.cols(), matColor.rows());

            // *********************************
            // * STEP: Remove Area Around Pills
            // *********************************
            matColor = removeAroundPills(matColor, bmapImage);
        }

        matColor.copyTo(matOriginal);

        // *********************************
        // * STEP: Create Edge Image
        // *********************************
        Mat matEdge = edgeDetection(matColor, bmapImage);

        // *********************************
        // * STEP: Reverse Edge and create solid pill structures
        // *********************************
        matOutput = reverseEdge(matEdge, bmapImage);

        // *********************************
        // * STEP: Add Blobs to pills
        // *********************************
        matOutput = detectBlob(matOutput, matOriginal, bmapImage);

        // **********************************
        // * STEP: Use Watershed Method
        // * ********************************
        //Mat matOutput = detectByWatershed(matColor, matEdge, bmapImage);

        //This is primarily for testing and viewing results.  Will not be used in final product.
        //Though it should be left in code for future debugging and product growth.
        Utils.matToBitmap(matOutput, bmapImage);
        if (flgWriteImage) {
            ImageUtility.storeImage(bmapImage, "/6-Final");
        }

        //Not sure this helps, there is lots of 'debate' on the web, but since it seems something isn't
        //cleaning well I figure it can't hurt.
        matOutput.release();
        matEdge.release();
        matColor.release();
        matOriginal.release();

        DetectionData sendData = new DetectionData();
        if (pills.size() > 0) {
            sendData.setStatus(DetectionStatus.SUCCESSFUL);
            sendData.setErrorMessage("");
        } else {
            sendData.setStatus(DetectionStatus.FAILED);
            sendData.setErrorMessage("No suitable pills found within the picking zone");
        }
        sendData.setSelectedPill(bmapImage);
        sendData.setSelectedPills(pills);

        mListener.onDetectionCompleted(sendData);

        long endTime = System.currentTimeMillis();
        Log.d(TAG, "*****Time for Process:" + (endTime - startTime));

        //Update database
        if (mBinId > 0 && !mTestRun) {
            dbHelper.deleteBinDetection(mBinId);
            dbHelper.addBinDetection(mBinData);
            Log.d(TAG, "Database Record Written for Bin: " + mBinId);
        }

        return sendData;
    }

    /*
     * This uses the found calibration from the calibration on the camera to remove distortion
     * from the image and allow direct comparison of pixel to location for rho, theta, z
     * conversion.  Could also help with detection.
     */
    private Mat removeDistortion(Mat original, Bitmap bmapImage) {
        Mat undistort = new Mat();

        BinCamera mCamera = BinCamera.getInstance();

        Imgproc.undistort(original, undistort, mCamera.getCameraMatrix(), mCamera.getDistortionCoefficients());

        if (flgWriteImage) {
            Utils.matToBitmap(undistort, bmapImage);
            ImageUtility.storeImage(bmapImage, "/1b-Undistorted");
        }

        return undistort;
    }

    /*
     * This removes everything out of the bin to eliminate bad intensities and various other
     * detractors for a valid detect.
     */
    private Mat removeOutsideBin(Mat inputMat, Bitmap bmapImage) {
        File dir = Environment.getExternalStorageDirectory();
        String templatePath = dir.getPath() + "/Pictures/";

        if (mTestRun) {
            templatePath += "testImages/Mask1.jpg";
        } else {
            templatePath += "template-091.jpg";
        }

        Mat template = new Mat(inputMat.rows(), inputMat.cols(), CvType.CV_8UC3, Scalar.all(0));
        template.create(inputMat.rows(), inputMat.cols(), CvType.CV_8UC4);
        template = Imgcodecs.imread(templatePath, Imgcodecs.IMREAD_COLOR );
        Imgproc.cvtColor(template, template, Imgproc.COLOR_BGR2BGRA);

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat boundingTemp = new Mat();
        Imgproc.cvtColor(template, boundingTemp, Imgproc.COLOR_BGR2GRAY);
        Imgproc.findContours(boundingTemp, contourList, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        int startX = 10000;
        int endX = -1;
        int startY = 10000;
        int endY = -1;

        Log.d(TAG, "Countours in Template:" + contourList.size());
        for (int i = 0; i < contourList.size(); i++ ) {
            Rect rect = Imgproc.boundingRect(contourList.get(i));
            if(rect.x < startX)
                startX = rect.x;
            if(rect.x + rect.width > endX)
                endX = rect.x + rect.width;
            if(rect.y < startY)
                startY = rect.y;
            if(rect.y + rect.height > endY)
                endY = rect.y + rect.height;

            //Log.d(TAG, "bounding box: (" + startX + ", " + startY + "),(" + endX + ", " + endY + ")");
        }

        startOfCutX = startX;
        startOfCutY = startY;

        //Log.d(TAG, "Template has channels: " + template.channels() + " depth: " + template.depth() + " and rows: " + template.rows() + " and cols: " + template.cols());

        Mat outputMat = new Mat();
        inputMat.copyTo(outputMat, template);
        Mat newOutputMat = outputMat.submat(startY, endY, startX, endX);

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/1c-Removal");
        }

        template.release();

        //return outputMat;
        return newOutputMat;
    }

    /*
     * This removes everything determined by a Area of Interest (AOI) that was figured out
     * by the post process analysis process run and stored within the database.  This is used
     * in replacement of the template and contour cut.
     */
    private Mat trimToFocusArea(Mat inputMat, Bitmap bmapImage) {

        startOfCutX = mBinData.getUpperLeftX();
        startOfCutY = mBinData.getUpperLeftY();

        Mat newOutputMat = inputMat.submat(startOfCutY, mBinData.getLowerRightY(), startOfCutX, mBinData.getLowerRightX());

        if (flgWriteImage) {
            bmapImage = Bitmap.createBitmap(bmapImage, 0, 0, newOutputMat.cols(), newOutputMat.rows());
            Utils.matToBitmap(newOutputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/1c-Removal");
        }

        return newOutputMat;
    }

    /*
     * This replaces the original method and does not use intensity thresholds that hopefully is more
     * robust from machine to machine, lighting situation to lighting situation.
     */
    private Mat removeAroundPills(Mat inputMat, Bitmap bmapImage) {
        Mat matColor = new Mat();
        //mCurrentImageBytes = imageBytes;

        //Bitmap bmapImage = BitmapFactory.decodeByteArray(mCurrentImageBytes, 0, mCurrentImageBytes.length);
        //Utils.bitmapToMat(bmapImage, matColor); //matColor is Type CV_8UC4; (Type = 24)
        //matColor = removeDistortion(matColor, bmapImage);

        inputMat.copyTo(matColor);

        //***************************************************
        //Subtract Picture Test
        //***************************************************
        Bitmap emptyBin;
        if (mTestRun) {
            emptyBin = ImageUtility.retrieveImage("/testImages/EmptyBin");
        } else {
            emptyBin = ImageUtility.retrieveImage("bin-1-image");
        }
        Mat matBigEmpty = new Mat();
        Mat matSubtract = new Mat();
        Utils.bitmapToMat(emptyBin, matBigEmpty);
        Mat matEmpty = matBigEmpty.submat(startOfCutY, matColor.rows() + startOfCutY, startOfCutX, matColor.cols() + startOfCutX);

        Core.subtract(matColor, matEmpty, matSubtract);

        //*******************************************************
        //Cut away background for analysis.  Doesn't necessarily
        //black correctly, so there for remove it from thought
        //only use bottom of bin for pill selection area.
        //*******************************************************

        Bitmap bottomBin;
        if (mTestRun) {
            bottomBin = ImageUtility.retrieveImage("/testImages/Mask3bottom");
        } else {
            bottomBin = ImageUtility.retrieveImage("template-000");
        }
        Mat matBottom = new Mat();
        Utils.bitmapToMat(bottomBin, matBottom);
        Mat matCutBottom = matBottom.submat(startOfCutY, matColor.rows() + startOfCutY, startOfCutX, matColor.cols() + startOfCutX);

        //Cut away the subtraction
        Mat newSubtract = new  Mat();
        matSubtract.copyTo(newSubtract, matCutBottom);

        //Cutaway the empty
        Mat newEmpty = new Mat();
        matEmpty.copyTo(newEmpty, matCutBottom);

        if (flgWriteImage) {
            Utils.matToBitmap(newSubtract, bmapImage);
            ImageUtility.storeImage(bmapImage, "/H1-Subtract");
        }
        matSubtract.release();
        matCutBottom.release();
        matBottom.release();
        matBigEmpty.release();
        matEmpty.release();

        //***************************************************
        //Histogram Analysis
        //***************************************************
        Mat matEmptyGray = new Mat();
        Mat matGray = new Mat();
        Mat histogram = new Mat();
        Mat histogramEmpty = new Mat();
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        MatOfInt histSize = new MatOfInt(64);

        Imgproc.cvtColor(newEmpty, matEmptyGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(newSubtract, matGray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(matEmptyGray, matEmptyGray, new Size(7,7), 3);
        Imgproc.GaussianBlur(matGray, matGray, new Size(7,7), 3);
        //Imgproc.threshold(matGray, matGray, 15, 255, Imgproc.THRESH_TOZERO);
        Imgproc.threshold(matGray, matGray, 15, 255, Imgproc.THRESH_BINARY);

        if (flgWriteImage) {
            Utils.matToBitmap(matGray, bmapImage);
            ImageUtility.storeImage(bmapImage, "/H2-Gray");
        }

        Imgproc.calcHist(Arrays.asList(matEmptyGray), new MatOfInt(0), new Mat(), histogramEmpty, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(matGray), new MatOfInt(0), new Mat(), histogram, histSize, ranges);

        //Now do grey scale
        Mat emptyHist = new Mat(64, 1, CvType.CV_32FC1);
        Mat calcHist = new Mat(64, 1, CvType.CV_32FC1);
        //double[] emptyArray = new double[]{0.0, 0.0, 0.0, 96.0, 9428.0, 49911.0, 106049.0, 111951.0, 113133.0, 82562.0, 56931.0, 36901.0, 26901.0, 24294.0, 26511.0, 27228.0, 25708.0, 20794.0, 12750.0, 4354.0, 3710.0, 4114.0, 4767.0, 4554.0, 3637.0, 3268.0, 2976.0, 2860.0, 2678.0, 2187.0, 1888.0, 1775.0, 1809.0, 1882.0, 2118.0, 1677.0, 1196.0, 1006.0, 791.0, 547.0, 442.0, 363.0, 289.0, 182.0, 98.0, 42.0, 16.0, 12.0, 9.0, 11.0, 10.0, 14.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        //emptyHist.put(0, 0, new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 14.0, 10.0, 9.0, 10.0, 13.0, 23.0, 53.0, 115.0, 169.0, 309.0, 405.0, 574.0, 1004.0, 1507.0, 1718.0, 1847.0, 2301.0, 2735.0, 2447.0, 2212.0, 2261.0, 2339.0, 2615.0, 3005.0, 3218.0, 3402.0, 3447.0, 4031.0, 4860.0, 4866.0, 4053.0, 3767.0, 6003.0, 14417.0, 19996.0, 25912.0, 28720.0, 26246.0, 24993.0, 28353.0, 38904.0, 59656.0, 88003.0, 118998.0, 116699.0, 91639.0, 30678.0, 7765.0, 109.0, 0.0, 0.0, 0.0});
        //Mat matInverse = new Mat();


        double[] histArray = new double[64];
        double[] emptyArray = new double[64];

        StringBuilder empty = new StringBuilder("empty->");
        StringBuilder full = new StringBuilder("full->");
        for (int i = 0; i < histogram.rows(); i++) {
            double[] ans = histogram.get(i, 0);
            double[] emptyAns = histogramEmpty.get(i, 0);
            histArray[i] = ans[0];
            emptyArray[i] = emptyAns[0];

            empty.append(emptyArray[0]);
            full.append(histArray[i]);
            if (i + 1 < histogram.rows()) {
                empty.append(", ");
                full.append(", ");
            }
        }

        double[] calcArray = new double[64];
        for (int i = 0; i < histArray.length; i++) {
            if (emptyArray[i] > 20000.0) {
                calcArray[i] = 0.0;
            } else if (emptyArray[i] > histArray[i]) {
                calcArray[i] = 0.0;
            } else {
                calcArray[i] = Math.pow(histArray[i] - emptyArray[i], 2);
            }
        }
        calcHist.put(0, 0, calcArray);

        Mat matBack = new Mat();
        Imgproc.calcBackProject(Arrays.asList(matGray), new MatOfInt(0), calcHist, matBack, ranges, 0.1); //Replaced histogram with emptyHist

        //TODO: This one could be important for a bit more as this is a final level of tweaking, and depending on
        //'how' we do things above this is the reverser for binary colors.  I would say, let's get through the
        // next round of revisions, and if our new 'lack of reverse' stands, we can then remove this.
        //Core.bitwise_not(matBack, matBack);
        //Imgproc.threshold(matBack, matBack, 220, 255, Imgproc.THRESH_BINARY);

        if (flgWriteImage) {
            Utils.matToBitmap(matBack, bmapImage);
            ImageUtility.storeImage(bmapImage, "/H3-GrayBack");
        }

        //****************************************************************************
        //Contour Build and Final Pill Area
        //****************************************************************************
        Mat hierarchy = new Mat();
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        Imgproc.findContours(matBack, contourList, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Log.d(TAG, "Contours Found: ->" + contourList.size());

        mContours = new Mat(matBack.rows(), matBack.cols(), CvType.CV_8UC3, Scalar.all(0));
        mContours.create(matBack.rows(), matBack.cols(), CvType.CV_8UC4);
        mContours.setTo(Scalar.all(0));
        for (int i = 0; i < contourList.size(); i++) {
            double contourArea = Imgproc.contourArea(contourList.get(i));

            //It seems a 'pill' size of the current size is around 9000 and the random detritus is 500...
            //very, very small pills will need something different....
            if (contourArea > CONTOUR_AREA_LIMIT) {
                //Log.d(TAG, "Contour Area:->" + contourArea);
                Imgproc.drawContours(mContours, contourList, i, new Scalar(255, 255, 255, 255), -1);
                Imgproc.drawContours(mContours, contourList, i, new Scalar(255, 255, 255, 255), 10);
            }
        }

        if (flgWriteImage) {
            Utils.matToBitmap(mContours, bmapImage);
            ImageUtility.storeImage(bmapImage, "/H4-PillArea");
        }

        long cntPixels = 0;
        int topX = -1;
        int topY = 10000;
        double slope = AppConfig.getInstance().getTemplateSlope();
        int coreX = 100;
        bmapImage = Bitmap.createBitmap(bmapImage, 0, 0, mContours.cols(), mContours.rows());
        Utils.matToBitmap(mContours, bmapImage);
        //Log.d(TAG, "Pixel Count is: " + bmapImage.getHeight() * bmapImage.getWidth());
        for (int x = 0; x < bmapImage.getWidth(); x++) {
            for (int y = 0; y < bmapImage.getHeight(); y++) {
                int pix = bmapImage.getPixel(x, y);
                int red = Color.red(pix);
                int blue = Color.blue(pix);
                int green = Color.green(pix);
                if (pix > 0 || red > 0 || blue > 0 || green > 0) {
                    //Log.d(TAG, "Pixel has a value" + pix);
                    cntPixels++;
                    double newY = y + ((coreX - x) * slope);
                    if (newY < topY) {
                        topY = (int)newY;
                        topX = 100;
                        //Log.d(TAG, "x, y: (" + x + ", " + y + " ) - New top X, Y: (" + topX + ", " + topY + ").");
                    }
                }
            }
        }

        fillZ = getFillLevelByUpperLeftPoint(topX + startOfCutX, topY + startOfCutY);
        fillMmZ = AppConfig.getInstance().getBinDepth() - (AppConfig.getInstance().getBinDepth() * (fillZ / 100));

        Mat outputMat = new Mat();
        inputMat.copyTo(outputMat, mContours);

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/3-Cleaned");
        }

        return outputMat;
    }

    /*
     * This handles the edge detection that limits the picture and remove everything but the edges
     * for use with the object detection.  NOTE: Height analysis will have to be done
     * prior to here as the pic has been chopped to the minimal needs.
     */
    private Mat edgeDetection(Mat inputMat, Bitmap bmapImage) {
        Mat matEdge = new Mat(inputMat.rows(), inputMat.cols(), CvType.CV_8UC1, Scalar.all(0));
        Mat outputMat = new Mat(inputMat.rows(), inputMat.cols(), CvType.CV_8UC4, Scalar.all(0));

        //This simply replaces the canny with a 'seperately created edge picture'
        if (mCannyReplacementTestRun) {
            mCannyReplaceMat.copyTo(matEdge);
            matEdge.convertTo(matEdge, CvType.CV_8UC4);
            //Imgproc.cvtColor(matEdge, matEdge, Imgproc.COLOR_GRAY2BGRA);
        } else {
            Imgproc.GaussianBlur(inputMat, inputMat, new Size(7, 7), 3);
            Imgproc.Canny(inputMat, matEdge, ptrLow, ptrHigh, 3, false);
            //outputMat.create(inputMat.rows(), inputMat.cols(), CvType.CV_8UC4);
            //Imgproc.cvtColor(outputMat, outputMat, Imgproc.COLOR_BGR2GRAY);
            matEdge.convertTo(matEdge, CvType.CV_8UC4);
            Imgproc.cvtColor(matEdge, matEdge, Imgproc.COLOR_GRAY2BGRA);
        }


        matEdge.copyTo(outputMat, mContours);

        int kernelSize = 1;
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize));
        Imgproc.erode(outputMat, outputMat, erodeElement, new Point(-1, -1), 4);
        kernelSize = 1;
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernelSize, kernelSize));
        Imgproc.dilate(outputMat, outputMat, dilateElement, new Point(-1, -1), 1);

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/4a-Canny");
        }

        Mat hierarchy = new Mat();
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        int threshlimit = THRESHOLD_INTENSITY_LIMIT; //Current viable setting for the situation.
        //Imgproc.threshold(outputMat, matEdge, threshlimit, 255, Imgproc.THRESH_BINARY);
        Imgproc.cvtColor(outputMat, matEdge, Imgproc.COLOR_BGR2GRAY);

        Imgproc.findContours(matEdge, contourList, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        Log.d(TAG, "Edge Contours Found: ->" + contourList.size());

        //contoured.create(inputMat);
        Mat edgeContours = new Mat(matEdge.rows(), matEdge.cols(), CvType.CV_8UC3, Scalar.all(0));
        edgeContours.create(matEdge.rows(), matEdge.cols(), CvType.CV_8UC4);
        edgeContours.setTo(Scalar.all(0));
        //List<Point> ends = new ArrayList<Point>();
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        double totalRectSize = 0;
        for (int i = 0; i < contourList.size(); i++) {
            //double contourArea = Imgproc.contourArea(contourList.get(i));
            MatOfPoint2f mop2f = new MatOfPoint2f(contourList.get(i).toArray());
            double arcLength = Imgproc.arcLength(mop2f, false);

            if (arcLength > 20 && mop2f.toList().size() > 5) {
                RotatedRect rect = Imgproc.fitEllipse(mop2f);
                rects.add(rect);
                totalRectSize += rect.size.area();
                Imgproc.drawContours(edgeContours, contourList, i, new Scalar(255,255,255,255), 5);
            }
        }

        if (flgWriteImage) {
            Utils.matToBitmap(edgeContours, bmapImage);
            ImageUtility.storeImage(bmapImage, "/4b-Edge");
        }

        matEdge.release();
        outputMat.release();

        return edgeContours;
    }

    /*
     * This method analyzes a line and determines if it should be included within the contour print.
     * additionally this collects informatino for future analysis.
     */
    private boolean analyzeLine(MatOfPoint2f mop2f, double arcLength, double contourArea, List<RotatedRect> rects) {
        //TODO:  Remove if we don't need to have line analysis.  Currently unused.
        boolean addLine = false;

        if (arcLength > 100.0) {
            List<Point> points = mop2f.toList();
            //HashSet<Point> nonDupe = new HashSet<>(points);

            if (points.size() >= 5) {
                addLine = true;

                if (mContours.get((int)points.get(0).y, (int)points.get(0).x)[0] > 0) {
                    StringBuilder sbPointList = new StringBuilder();
                    StringBuilder sbLineList = new StringBuilder();
                    List<Point> newPoints = points;
                    sbLineList.append("LineAngles->");
                    for (int k = 0; k < points.size(); k++) {
                        if (k > 1) {
                            double lineAngle = Math.atan((points.get(k-2).y - points.get(k).y)/(points.get(k-2).x - points.get(k).x));
                            sbLineList.append(String.format("%.3f", lineAngle));
                            if (k + 1 < points.size()) {
                                sbLineList.append(",");
                            }
                        }
                        if (k > 2) {
                            if (lineLength(points.get(k - 2), points.get(k)) < lineLength(points.get(k - 1), points.get(k))) {
                                newPoints = points.subList(0, k - 1);
                                sbPointList.deleteCharAt(sbPointList.length() - 1);
                                break;
                            }
                        }

                        sbPointList.append("(");
                        sbPointList.append(String.format("%.0f", points.get(k).x));
                        sbPointList.append(",");
                        sbPointList.append("-" + String.format("%.0f",points.get(k).y));
                        sbPointList.append(")");
                        if (k + 1 < points.size()) {
                            sbPointList.append(",");
                        }
                    }
                    double shortestLength = Math.sqrt(Math.pow(newPoints.get(0).x - newPoints.get(newPoints.size() - 1).x, 2) + Math.pow(newPoints.get(0).y - newPoints.get(newPoints.size() - 1).y, 2));

//                    if (arcLength > (shortestLength * 4)) {
                    if (points.size() < newPoints.size() * 4) {
                        addLine = false;
                    } else {
                        Log.d(TAG, "------------");
                        Log.d(TAG, "Point1:(" + newPoints.get(0).x + ", " + newPoints.get(0).y + ") Point2:(" + newPoints.get(newPoints.size() - 1).x + ", " + newPoints.get(newPoints.size() - 1).y + ")");
                        Log.d(TAG, "arcLength: " + arcLength + " lineLength: " + shortestLength);
                        Log.d(TAG, sbPointList.toString());
                        Log.d(TAG, sbLineList.toString());
                        Log.d(TAG, "Old Point Count: " + points.size() + " New Point Count: " + newPoints.size());
                    }

                } else {
                    Log.d(TAG, "Outside of the mContour area");
                    addLine = false;
                }
            }
        }
        return addLine;
    }

    /*
     * This calculated the direct line distance between to given Points.
     */
    private double lineLength(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /*
     * This method takes the edge picture, inverses the colors (black and white), and then creates
     * highly accurate 'blobs' of the pills.
     */
    private Mat reverseEdge(Mat inputMat, Bitmap bmapImage) {
        //TODO: Still doesn't handle 'M' of the M&M very well, likely writing will impact detection until that is solved.
        Mat invertMat = new Mat();
        Mat outputMat = new Mat();
        int erosionSize = 4;
        int dilationSize = 1;
        Mat kernel = new Mat(new Size(3, 3), CvType.CV_8UC1, new Scalar(255));
        Mat erosionElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosionSize + 1, 2*erosionSize+1));
        Mat dilationElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilationSize + 1, 2*dilationSize+1));

        //Imgproc.threshold(inputMat, invertMat, 100, 255, Imgproc.THRESH_BINARY_INV);
        //invertMat.copyTo(outputMat, mContours);

        // Create binary image from source image
        Mat bw = new Mat();
        Imgproc.cvtColor(inputMat, bw, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(bw, bw, 40, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
//        Mat dist = new Mat();
//        Imgproc.distanceTransform(bw, dist, Imgproc.DIST_C, 5);
//        Core.multiply(dist, new Scalar(20), dist);


        Imgproc.cvtColor(mContours, mContours, Imgproc.COLOR_BGR2GRAY);
        bw.copyTo(outputMat, mContours);

        Imgproc.erode(outputMat, outputMat, erosionElement);
        Imgproc.dilate(outputMat, outputMat, dilationElement);

        outputMat.convertTo(outputMat, CvType.CV_8UC4);
        Imgproc.cvtColor(outputMat, outputMat, Imgproc.COLOR_GRAY2BGRA);

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/5a-Inverse");
        }

        invertMat.release();

        return outputMat;
    }

    /*
     * This is the initial attempt at the alpha level of detection.  Trying to improve detection from
     * 70% / 80% range to above the 95% range.  This uses a known method called watershedding to identify
     * the pills regardless of shape.
     */
    private Mat detectByWatershed(Mat inputMat, Mat edgeMat, Bitmap bmapImage) {
        Mat outputMat = new Mat();
        Mat processMat = new Mat();
        Mat invertMat = new Mat();
        Mat otsuMat = new Mat();
        int erosionSize = 4;
        int dilationSize = 2;
        Mat kernel = new Mat(new Size(3, 3), CvType.CV_8UC1, new Scalar(255));
        Mat erosionElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosionSize + 1, 2*erosionSize+1));
        Mat dilationElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilationSize + 1, 2*dilationSize+1));

        Imgproc.threshold(edgeMat, invertMat, 100, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.distanceTransform(invertMat, invertMat, Imgproc.CV_DIST_L2, 3);
        invertMat.convertTo(invertMat, CvType.CV_8UC4);
        Imgproc.cvtColor(mContours, mContours, Imgproc.COLOR_BGR2GRAY);

        invertMat.copyTo(outputMat, mContours);
        Core.multiply(outputMat, new Scalar(15), outputMat);

        Imgproc.erode(outputMat, outputMat, erosionElement);
        Imgproc.dilate(outputMat, outputMat, dilationElement);
        //Core.normalize(outputMat, outputMat);

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/W2-Invert");
        }

        Imgproc.cvtColor(inputMat, otsuMat, Imgproc.COLOR_BGR2GRAY);
        //invertMat.copyTo(processMat);
        //double heavyThreshold = ((maxIntensity - meanIntensity.val[0]) * PERCENT_FOR_BLOB_THRESHOLD) + meanIntensity.val[0];
        double heavyThreshold = 140;
        Imgproc.threshold(otsuMat, otsuMat, heavyThreshold, 255, Imgproc.THRESH_OTSU);
        Imgproc.morphologyEx(otsuMat, otsuMat, Imgproc.MORPH_OPEN, kernel);

        if (flgWriteImage) {
            Utils.matToBitmap(otsuMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/W3-Otsu");
        }

        Imgproc.distanceTransform(outputMat, processMat, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE);
        Core.multiply(processMat, new Scalar(20), processMat);
        Imgproc.threshold(processMat, processMat, 50, 255, Imgproc.THRESH_BINARY);
        processMat.convertTo(processMat, CvType.CV_8UC4);

        if (flgWriteImage) {
            Utils.matToBitmap(processMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/W4-Distance");
        }

        // get background
        Mat M = Mat.ones(3, 3, CvType.CV_8U);
        Mat opening = new Mat();
        Mat background = new Mat();
        Imgproc.erode(otsuMat, otsuMat, M);
        Imgproc.dilate(otsuMat, opening, M);
        Imgproc.dilate(opening, background, M);

        //get foreground
        Mat foreground = new Mat();
        Mat unknown = new Mat();
        Imgproc.threshold(processMat, foreground, 0.7 * 1, 255, Imgproc.THRESH_BINARY);
        foreground.convertTo(foreground, CvType.CV_8U, 1, 0);
        Core.subtract(background, foreground, unknown);

        Mat markers = new Mat();
        Imgproc.connectedComponents(foreground, markers);
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                double[] value = markers.get(i, j);
                double[] unknownValue = unknown.get(i, j);
                if (unknownValue[0] == 255) {
                    unknownValue[0] = 0.0;
                    markers.put(i, j, unknownValue);
                }
            }
        }

        Imgproc.cvtColor(inputMat, outputMat, Imgproc.COLOR_RGBA2RGB, 0);
        Imgproc.watershed(outputMat, markers);
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                double[] mark = markers.get(i, j);
                double[] clr = new double[]{255, 0, 255};
                if (mark[0] == -1) {
                    //Log.d(TAG, "Marked");
                    outputMat.put(i, j, clr);
                }
            }
        }

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/W5-Watershed");
        }

        //outputMat = processMat;
        return outputMat;
    }

    /*
     * This method uses the Simple Blob Detector from OpenCV to attempt to detect the pills via a
     * general function.  This may work well enough, but the biggest concern is if the memory of
     * the IoT device will be able to accommodate the needs of Blob Detection.
     */
    private Mat detectBlob(Mat inputMat, Mat finalMat, Bitmap bmapImage) {
        Mat greyMat = new Mat();
        Mat threshMat = new Mat();
        Mat outputMat = new Mat();

        // ***********************************
        // *  Deprecated FeatureDetector for simple blob
        // ***********************************
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);

        //Write the parameter file.
        File dir = Environment.getExternalStorageDirectory();
        detector.read(dir.getPath() + "/blob.xml");

        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(inputMat, keypoints);
        List<KeyPoint> lstKeyPoints = keypoints.toList();

        //Log.d(TAG, "Keypoint Count:->" + lstKeyPoints.size());
        inputMat.copyTo(outputMat);
        for (KeyPoint kp: lstKeyPoints) {
            Imgproc.circle(outputMat, kp.pt, 3, new Scalar(0,0,255), 8, 8, 0);
            Imgproc.circle(outputMat, kp.pt, (int)(kp.size / 2), new Scalar(255,0,0), 3, 8, 0);
        }

        if (flgWriteImage) {
            Utils.matToBitmap(outputMat, bmapImage);
            ImageUtility.storeImage(bmapImage, "/5b-Blob");
        }

        // **********************************
        // * STEP: Choose a Pill to pick up
        // **********************************
        finalMat.copyTo(outputMat);

        //Imgproc.cvtColor(outputMat, outputMat, Imgproc.COLOR_GRAY2BGR);
        for (KeyPoint kp: lstKeyPoints) {
            Imgproc.circle(outputMat, kp.pt, 2, new Scalar(0, 0, 255), 8, 8, 0);
            Imgproc.circle(outputMat, kp.pt, 4, new Scalar(0, 0, 255), 3, 8, 0);
        }

        outputMat = choosePill(lstKeyPoints, outputMat);

        keypoints.release();
        greyMat.release();
        threshMat.release();

        return outputMat;
    }

    /*
     * This method takes the key points and chooses the best in the area.  It selects by size, which
     * means the largest, or brightest, blob is the one selected.  Currently this selects the
     * first largest in the list, so if two are identical sized (highly rare), then the first of the
     * two would be selected.  Currently this doesn't bar any areas from selection so corners and
     * edges are valid.
     */
    private Mat choosePill(List<KeyPoint> lstKeyPoints, Mat inputMat) {

        KeyPoint chosenKp = new KeyPoint();
        chosenKp.size = 0;
        int cntPoints = AppConfig.getInstance().getDetectionsReturned();
        int i = 0;
        float maxSize = -1f;

        List<KeyPoint> tempList = lstKeyPoints;
        List<KeyPoint> fullList = lstKeyPoints;
        Log.d(TAG, "KeyPoint Count: " + fullList.size());

        if (fullList.size() < 2)
            cntPoints = fullList.size();

        while ( i < cntPoints && i < lstKeyPoints.size() && tempList.size() > 0) {
            fullList = tempList;
            tempList = new ArrayList<KeyPoint>();
            Log.d(TAG,"Loop(" + i + ") fulllist.size:" + fullList.size());

            //Get the biggest connection
            for (KeyPoint tl: fullList) {
                if (tl.size > maxSize) {
                    maxSize = tl.size;
                }
            }

            //Add the biggest connection first
            for (KeyPoint tl: fullList) {
                if (tl.size >= maxSize) {
                    chosenKp = tl;
                } else {
                    tempList.add(tl);
                }
            }

            //Convert the Point
            CoordinateData convPoint = ImageUtility.convertPoint(chosenKp.pt.x + startOfCutX, chosenKp.pt.y + startOfCutY, fillZ);
            convPoint.mmZ = fillMmZ;

            double radiusLimit = Math.abs(convPoint.radius);
            if (radiusLimit <= AppConfig.getInstance().getDeadzoneRhoLimit()) {
                double thetaLimit = (radiusLimit/12) + AppConfig.getInstance().getDeadzoneThetaIntercept(); //the slop intercept of deadzone y = x/12 + 4;
                if (Math.abs(convPoint.degrees) <= thetaLimit) {
                    //Draw Red Circles on selections
                    Imgproc.circle(inputMat, chosenKp.pt, 3, new Scalar(255, 0, 0), 8, 8, 0);
                    Imgproc.circle(inputMat, chosenKp.pt, 8, new Scalar(255, 0, 0), 6, 8, 0);

                    //Add to pills
                    pills.add(new PillLocation(convPoint, chosenKp.pt));
                    i++;
                }
            }

            maxSize = -1f;
            chosenKp = new KeyPoint();
        }

        //chosenPillKp = chosenKp;
        return inputMat;
    }

    /*
     * This method finds the fill to match
     */
    private int getFillLevelByUpperLeftPoint(int curX, int curY) {
        //Upper Left Corner of each template to use this method to find fill level.
        int[][] ulPoint = AppConfig.getInstance().getTemplateUpperLeftXY();

        Log.d(TAG, "ulPoint: " + ulPoint.length);

        //double slope = 9.0 / 50.0;
        double slope = AppConfig.getInstance().getTemplateSlope();

        int fillLevel = -1;
        for (int fill = 0; fill < 101; fill++) {
            double val = ulPoint[fill][1];
            double newY = curY + (val - curX) * slope;
            //Log.d(TAG, "newY: " + newY + " ->" + ulPoint[fill][2] + " slope:" + slope + " fill:" + fill);
            if (newY > ulPoint[fill][2]) {
                Log.d(TAG, "fill: " + fill + " x:" + val + " y:" + newY + " ulPoint:(" + ulPoint[fill][1] + ", " + ulPoint[fill][2] + ")");
                fillLevel = fill;
                break;
            }
        }
        //If it never fall within the boundary, it is likely filled up and beyond the barriers of the template used to discern full.
        if (fillLevel == -1) {
            fillLevel = 100;
        }

        return fillLevel;
    }

    OnDetectionCompleteListener mListener = new OnDetectionCompleteListener() {
        @Override
        public void onDetectionCompleted(DetectionData data) {
            //Empty
        }
    };

}
