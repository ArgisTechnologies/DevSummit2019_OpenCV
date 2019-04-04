package com.logicpd.papapill.computervision;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.logicpd.papapill.App;
import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.device.models.CoordinateData;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * This class is basic and public functions to help the visualizer and system manage images more
 * efficiently for the project.
 * Brady Hustad - Logic PD - 2018-07-16 - Initial Creation
 * Brady Hustad - Logic PD - 2018-08-31 - Implemented Configuration, refactored code.
 * Brady Hustad - Logic PD - 2018-12-11 - Refactored and removed android/java warnings.
 */
@SuppressWarnings("SpellCheckingInspection")
public class ImageUtility {
    //Class Variables
    private static final String TAG = ImageUtility.class.getSimpleName();

    //Parameters as calculated for the camera.  These may need to become dynamic.
    private static final int XPixels = AppConfig.getInstance().getImageWidth();
    private static final int YPixels = AppConfig.getInstance().getImageHeight();

    //Calculated Image Properties
    private static double mImageUpperLeftXmm;
    private static double mImageUpperLeftYmm;
    private static double mImageConvergeXmm;
    private static double mImageConvergeYmm;

    //No Constructor

    /*
     * Create Pictures directory for templatesF
     * - copy default files (fail save if 101 templates are not there
     */
    public static String initializePictureDirectory()
    {
        String pictureDir = Environment.getExternalStorageDirectory().getPath() + "/Pictures";
        Log.d(TAG, "pictureDir:"+pictureDir);

        File f = new File(pictureDir);
        if(!f.exists()) {
            if(f.mkdir())
            {
                Log.d(TAG, "Created directory");
                initializeDefaultTemplates("template-000.jpg", "/sdcard/Pictures/template-000.jpg");
                initializeDefaultTemplates("template-091.jpg", "/sdcard/Pictures/template-091.jpg");
                initializeDefaultTemplates("bin-1-image.jpg", "/sdcard/Pictures/bin-1-image.jpg");
                initializeDefaultTemplates("blob.xml", "/sdcard/blob.xml");
            }
            else
            {
                Log.d(TAG, "Failed to create directory");
            }
        }
        return pictureDir;
    }

    /*
     * copy default files to Pictures directory if necessary
     * - these are not the calibrated values but at least app won't crash.
     * - Should run BuildTemplates() to create 101 templates.
     */
    public static void initializeDefaultTemplates(String source,
                                                  String destination)
    {
        try {
            File destinationFile = new File(destination);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            InputStream inputStream = App.getContext().getAssets().open(source);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            Log.d(TAG, "initializeDefaultTemplates done:"+destination);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "initializeDefaultTemplates failed:"+ex);
        }
    }

    /*
     * This static method simply writes the given bitmap to the sdcard/Pictures
     * directory as a jpeg named [filename].jpg
     */
    public static void storeImage(Bitmap imageToStore, String filename) {
        String fileToWrite;

        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();

            imageToStore.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
            byte[] bArray = bStream.toByteArray();

            File dir = Environment.getExternalStorageDirectory();
            fileToWrite = dir.getPath() + "/Pictures/" + filename + ".jpg";

            FileOutputStream fStream = new FileOutputStream(fileToWrite, false);
            fStream.write(bArray);
            fStream.close();

        } catch (Exception e) {
            Log.e(TAG, "Error in storeImage:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * This static method retrieves a bitmap that is stored into the SD Card storage on the
     * android device.  Specific use is for empty bin pictures.
     */
    public static Bitmap retrieveImage(String filename) {
        Bitmap bm = null;
        String fileToGet;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            File dir = Environment.getExternalStorageDirectory();
            fileToGet = dir.getPath() + "/Pictures/" + filename + ".jpg";

            bm = BitmapFactory.decodeFile(fileToGet, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in retrieveImage:" + e.getMessage());
            e.printStackTrace();
        }

        return bm;
    }

    /*
     * This method converts real coordinates of Rho (Radius) and Theta (degrees)
     * into pixels as seen in the image for detection.
     */
    public static Point convertRealCoordinates(double r, double theta, double z) {
        AppConfig dConfig = AppConfig.getInstance();

        mImageConvergeXmm = -1 * Math.cos(Math.toRadians(dConfig.getConvergentTheta())) * dConfig.getConvergentRho(); // X Convergence point in millimeters, negative sign to put small numbers at left
        mImageConvergeYmm = Math.sin(-1 * Math.toRadians(dConfig.getConvergentTheta())) * dConfig.getConvergentRho(); // Y Convergence point in millimeters, negative sign to put small numbers at top

        // fill is a value in percent full ranging between 0-100
        double fill = Math.abs(z);
        if (fill > 100) {
            fill = 100;
        }

        mImageUpperLeftXmm = mImageConvergeXmm - (dConfig.getMmm() * fill + dConfig.getMmb()) * dConfig.getConvergentPixelX(); // upper left X position in millimeters
        mImageUpperLeftYmm = mImageConvergeYmm - (dConfig.getMmm() * fill + dConfig.getMmb()) * dConfig.getConvergentPixelY(); // upper left Y position in millimeters

        double Xabs, Yabs, Xrel, Yrel;
        double PixelX, PixelY;

        double rtheta = Math.toRadians(theta);
        Xabs = -1 * Math.cos(rtheta) * (r + dConfig.getRho0()); // Absolute X position in millimeters, small numbers left
        Yabs = Math.sin(-1 * rtheta) * (r + dConfig.getRho0()); // Absolute Y position in millimeters, small numbers top

        Xrel = Xabs - mImageUpperLeftXmm; // Relative X position in millimeters, 0 at UL
        Yrel = Yabs - mImageUpperLeftYmm; // Relative Y position in millimeters, 0 at UL

        PixelX = Xrel / (dConfig.getMmm() * fill + dConfig.getMmb()); // Convert millimeters back to pixels
        PixelY = Yrel / (dConfig.getMmm() * fill + dConfig.getMmb()); // Convert millimeters back to pixels

        return new Point(PixelX, PixelY);
    }

    /*
     * This method converts a pixel coordinate at a specific height to a radius
     * and theta.  The height is given as a height percentage from the bottom of the bucket.
     */
    public static CoordinateData convertPoint(double x, double y, double z) {
        CoordinateData data = new CoordinateData();

        AppConfig dConfig = AppConfig.getInstance();

        mImageConvergeXmm = -1 * Math.cos(dConfig.getConvergentTheta()) * dConfig.getConvergentRho(); // X Convergence point in millimeters, negative sign to put small numbers at left
        mImageConvergeYmm = Math.sin(-1 * dConfig.getConvergentTheta()) * dConfig.getConvergentRho(); // Y Convergence point in millimeters, negative sign to put small numbers at top

        //Convert Z to a percentage of fill
        double fill = Math.abs(z);
        if (fill > 100) {
            fill = 100;
        }

        mImageUpperLeftXmm = mImageConvergeXmm - (dConfig.getMmm() * fill + dConfig.getMmb()) * dConfig.getConvergentPixelX(); // upper left X position in millimeters
        mImageUpperLeftYmm = mImageConvergeYmm - (dConfig.getMmm() * fill + dConfig.getMmb()) * dConfig.getConvergentPixelY(); // upper left Y position in millimeters

        //Do  Rotation
        double XbasePixels = x - dConfig.getConvergentPixelX();
        double YbasePixels = y - dConfig.getConvergentPixelY();

        double rotTheta = -dConfig.getRotation();
        double XrotPixels = (Math.cos(rotTheta) * XbasePixels - Math.sin(rotTheta) * YbasePixels) + dConfig.getConvergentPixelX();
        double YrotPixels = (Math.sin(rotTheta) * XbasePixels + Math.cos(rotTheta) * YbasePixels) + dConfig.getConvergentPixelY();

        //Convert Pixels to Millimeters
        Double Xrel = XrotPixels * (dConfig.getMmm() * fill + dConfig.getMmb());
        Double Yrel = YrotPixels * (dConfig.getMmm() * fill + dConfig.getMmb());

        Double Xabs = Xrel + mImageUpperLeftXmm;
        Double Yabs = Yrel + mImageUpperLeftYmm;

        double finalR = Math.sqrt(Xabs * Xabs + Yabs * Yabs) - dConfig.getConvergentRho();
        double rtheta = dConfig.getConvergentTheta() + (Math.atan(Yabs / Xabs));
        double finalTheta = Math.toDegrees(rtheta);

        data.radius = finalR;
        data.degrees = finalTheta;
        data.z = z;

        return data;
    }

    /*
     * This method adds calibration dots to an image to confirm alignment
     * with an image of a calibration bin.  These bins are generallyl
     * 0, 25, 50, 75, and 100 in fill, I use a double so that we can
     * if needed, test other fill levels.
     *
     * (Code adapted from J. Eliason's psuedocode)
     */
    public static Bitmap buildScaleCalibrationImage(Bitmap original, double height) {

        AppConfig dConfig = AppConfig.getInstance();

        Mat matOriginal = new Mat();
        Mat matUndistort = new Mat();
        Utils.bitmapToMat(original, matOriginal); //matColor is Type CV_8UC4; (Type = 24)
        BinCamera mCamera = BinCamera.getInstance();
        Imgproc.undistort(matOriginal, matUndistort, mCamera.getCameraMatrix(), mCamera.getDistortionCoefficients());

        mImageConvergeXmm = -1 * Math.cos(dConfig.getConvergentTheta()) * dConfig.getConvergentRho(); // X Convergence point in millimeters, negative sign to put small numbers at left
        mImageConvergeYmm = Math.sin(-1 * dConfig.getConvergentTheta()) * dConfig.getConvergentRho(); // Y Convergence point in millimeters, negative sign to put small numbers at top

        // fill is a value in percent full ranging between 0-100
        double fill = Math.abs(height);
        if (fill > 100) {
            fill = 100;
        }

        mImageUpperLeftXmm = mImageConvergeXmm - (dConfig.getMmm() * fill + dConfig.getMmb()) * dConfig.getConvergentPixelX(); // upper left X position in millimeters
        mImageUpperLeftYmm = mImageConvergeYmm - (dConfig.getMmm() * fill + dConfig.getMmb()) * dConfig.getConvergentPixelY(); // upper left Y position in millimeters

        double Xabs, Yabs, Xrel, Yrel;
        double PixelX, PixelY;

        Imgproc.circle(matUndistort, new Point(dConfig.getConvergentPixelX(), dConfig.getConvergentPixelY()), 8, new Scalar(0, 255, 0), 8, 8, 0);

        for (double lrho = -30; lrho <= 30; lrho += 10){
            for (double ltheta = -9; ltheta <= 9; ltheta += 3) {
                double rtheta = Math.toRadians(ltheta);

                //Do rotation
                //Get absolute x,y of real world (mm in both directions) with the 0,0 base at convergence
                Xabs = -1 * Math.cos(rtheta) * (lrho + dConfig.getRho0()); // Absolute X position in millimeters, small numbers left
                Yabs = Math.sin(-1 * rtheta) * (lrho + dConfig.getRho0()); // Absolute Y position in millimeters, small numbers top

                double Xbase = Xabs - mImageConvergeXmm;
                double Ybase = Yabs - mImageConvergeYmm;

                double rotTheta = dConfig.getRotation();
                double Xrot = (Math.cos(rotTheta) * Xbase - Math.sin(rotTheta) * Ybase) + mImageConvergeXmm;
                double Yrot = (Math.sin(rotTheta) * Xbase + Math.cos(rotTheta) * Ybase) + mImageConvergeYmm;

                Xrel = Xrot - mImageUpperLeftXmm; // Relative X position in millimeters, 0 at UL
                Yrel = Yrot - mImageUpperLeftYmm; // Relative Y position in millimeters, 0 at UL

                PixelX = Xrel / (dConfig.getMmm() * fill + dConfig.getMmb()); // Convert millimeters back to pixels
                PixelY = Yrel / (dConfig.getMmm() * fill + dConfig.getMmb()); // Convert millimeters back to pixels

                // Typecast PixelX and PixelY back to int and use to draw dots on the screen where
                // we expect the calibration intersections to be.
                // Verify that pixels are within image extents before plotting.
                // Overlay this with the original image for a given height.  If this looks good,
                // we can move on to the next step to determine
                // rho and theta based on the pixel X, Y.
                if (PixelX >= 0 && PixelY >=0 && PixelX <= XPixels && PixelY <= YPixels) {
                    Log.d(TAG, "Plotting a Calibration Point!");
                    Point cl = new Point(PixelX, PixelY);
                    Imgproc.circle(matUndistort, cl, 3, new Scalar(0, 0, 255), 3, 8, 0);
                }
            }
        }

        Utils.matToBitmap(matUndistort, original);
        ImageUtility.storeImage(original, "7-Calibration");

        return original;
    }

    /*
     * This method will build templates for each fill level.  The main area will be a solid color
     * and the rest blank.  This should work to give us a method to get the formula to figure out
     * the fill level from the pill grab and remove all area outside the bin.  The templates will
     * be created for every 5% of fill from 0% to 100%.
     */
    @SuppressWarnings("ConstantConditions")
    public static void buildTemplates() {

        initializePictureDirectory();
        
        int maxRow = AppConfig.getInstance().getImageWidth();
        int maxColumn = AppConfig.getInstance().getImageHeight();
        double slopes = 0.0;

        Bitmap output;

        //The metrics of 'in bin'
        double maxTheta = AppConfig.getInstance().getBinThetaMax(); //This is plus top of the bin;
        double minTheta = AppConfig.getInstance().getBinThetaMin(); //This is bottom of the bin;
        double maxR = 40.0; //This is plus or minus;

        //Every time it builds the image it will scale up, or get bigger, so therefore we only have
        //to instantiate this once.
        Mat template = new Mat();
        output = Bitmap.createBitmap(maxRow, maxColumn, Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(output, template);

        //The loop for each fill level
        for (int fill = 0; fill <= 100; fill += 1) {
            String filename;
            if (fill < 10) {
                filename = "template-00" + fill;
            } else if (fill < 100) {
                filename = "template-0" + fill;
            } else {
                filename = "template-" + fill;
            }

            int cntArea = 0;
            int ulX = 10000;
            int ulY = 10000;
            int hundredX = 10000;
            int hundredY = 10000;

            //The row loop
            for (int row = 0; row < maxRow; row++) {
                //The column loop
                for (int column = 0; column < maxColumn; column++) {
                    CoordinateData data = ImageUtility.convertPoint(row, column, fill);

                    if (data.degrees <= maxTheta && data.degrees > minTheta && Math.abs(data.radius) <= maxR) {
                        Imgproc.line(template, new Point(row, column), new Point(row, column), new Scalar(255, 255, 255));
                        cntArea++;
                        if (ulY > column) {
                            ulY = column;
                            ulX = row;
                        }

                        if (ulX + 100 == row) {
                            hundredX = ulX + 100;
                            if (hundredY > column) {
                                hundredY = column;
                            }
                        }
                    }
                }
            }

            //Log.d(TAG, "Fill Level: " + fill + " completed. Area (pixels) count:" + cntArea);
            //Log.d(TAG, "Fill Level: " + fill + " Upper Left Point: x:" + ulX + " y:" + ulY + " Next Point: x:" + hundredX + " y:" + hundredY);
            slopes += Math.abs(hundredY - ulY) / Math.abs(hundredX - ulX);
            Log.d(TAG, "Fill Level: " + fill + ", " + ulX + ", " + ulY);

            Utils.matToBitmap(template, output);
            ImageUtility.storeImage(output, filename);
        }

        Log.d(TAG, "Slope of the Bin Edge: " + (slopes / 101));
        Log.d(TAG, "Template Creation Complete");
    }
}
