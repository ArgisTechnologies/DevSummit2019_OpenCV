package com.logicpd.papapill.computervision.barcode;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.logicpd.papapill.computervision.detection.Detector;
import com.logicpd.papapill.data.Medication;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Iterator;
import java.util.List;

public class BarcodeReader {
    private static final String TAG = Detector.class.getSimpleName();

    private static int ROTATION_TOTAL = 90;
    private static int ROTATION_STEP = 10;

    private BarcodeReaderListener barcodeListener;

    private Bitmap mImage;
    private int curRotation = 0;
    private BarcodeTypes mType;

    //********************************
    //Constructors
    //********************************

    /*
     * This current constructor.  Not much happening yet.
     */
    public BarcodeReader() {
        barcodeListener = null;
        curRotation = 0;
    }

    //********************************
    //Getters and Setters
    //********************************
    public void setBarcodeListener(BarcodeReaderListener listener) {
        barcodeListener = listener;
    }

    //********************************
    //Public Methods
    //********************************

    /*
     * This is for reading the barcode that represents a bin.  There should be a numeral, and a
     * bar code.  This will be looking to read the bar code.  There should be a predictable rotation
     * for this bar code, so the code to use the image should be simplified from the requirements
     * on the bottle being held by a user.
     */
    public void readBinBarcode(Bitmap bcImage) throws BarcodeException {
        mImage = prepareImage(bcImage);
        mType = BarcodeTypes.BIN;

        readBarcode(mImage, mType);
    }

    /*
     * This is for reading the barcode that represents a medication.  Specifically the one that
     * Papapill is trying to make a standard and we are defining.
     */
    public void readMedicationBarcode(Bitmap bcImage) throws BarcodeException {
        mImage = prepareImage(bcImage);
        mType = BarcodeTypes.MEDICATION;

        readBarcode(mImage, mType);
    }

    /*
     * This is for reading the barcode(s) generically and passing back the data as it is found. to
     * the listener with the type that is given.
     */
    private void readBarcode(final Bitmap bcImage, final BarcodeTypes type) throws BarcodeException {
        mImage = bcImage;

        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_DATA_MATRIX,
                                FirebaseVisionBarcode.FORMAT_UPC_A,
                                FirebaseVisionBarcode.FORMAT_UPC_E)
                        .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bcImage);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> result;

        result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        BarcodeData bData = new BarcodeData();
                        bData.setType(type);
                        Log.d(TAG, "Barcode Count:" + firebaseVisionBarcodes.size());
                        if (firebaseVisionBarcodes.size() > 0) {
                            for (FirebaseVisionBarcode barcode : firebaseVisionBarcodes) {
                                RawBarcodeData rbd = new RawBarcodeData();
                                rbd.setBounds(barcode.getBoundingBox());
                                rbd.setCorners(barcode.getCornerPoints());
                                rbd.setRawData(barcode.getRawValue());
                                rbd.setFormat(barcode.getFormat());

                                bData.addRawData("" + barcode.hashCode(), rbd);
                            }
                            bData.setBaseImage(mImage);

                            try {
                                bData = getBarcodeData(bData, type);

                                if (barcodeListener != null)
                                    barcodeListener.onSuccess(bData);
                            } catch (BarcodeException be) {
                                bData.setErrorMessage(be.getMessage());

                                if (barcodeListener != null)
                                    barcodeListener.onFailure(bData);
                            }

                        } else {
                            if (curRotation >= ROTATION_TOTAL) {
                                if (barcodeListener != null) {
                                    bData.setErrorMessage("No Detectable Barcode within image.");
                                    bData.setBaseImage(mImage);
                                    barcodeListener.onFailure(bData);
                                }
                                curRotation = 0;
                            } else {
                                curRotation += ROTATION_STEP;

                                mImage = rotateBitmap(mImage, ROTATION_STEP);

                                try {
                                    readBarcode(bcImage, type);
                                } catch (BarcodeException be) {
                                    Log.d(TAG, be.getMessage());
                                    if (barcodeListener != null) {
                                        bData.setErrorMessage(be.getMessage());
                                        bData.setBaseImage(mImage);
                                        barcodeListener.onFailure(bData);
                                    }
                                }

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        BarcodeData bData = new BarcodeData();
                        bData.setType(type);
                        if (curRotation >= ROTATION_TOTAL) {
                            if (barcodeListener != null) {
                                bData.setErrorMessage("No Detectable Barcode within image.");
                                bData.setBaseImage(mImage);
                                barcodeListener.onFailure(bData);
                            }
                            curRotation = 0;
                        } else {
                            curRotation += ROTATION_STEP;

                            mImage = rotateBitmap(mImage, ROTATION_STEP);

                            try {
                                readBarcode(mImage, type);
                            } catch (BarcodeException be) {
                                Log.d(TAG, be.getMessage());
                                if (barcodeListener != null) {
                                    bData.setErrorMessage(be.getMessage());
                                    bData.setBaseImage(mImage);
                                    barcodeListener.onFailure(bData);
                                }
                            }

                        }
                    }
                });
    }

    //********************************
    //Private Methods
    //********************************

    /*
     * This method parses the data, and identifies what type of data we are getting.  This puts it in the
     * correct place to return to the system.
     */
    private BarcodeData getBarcodeData(BarcodeData bData, BarcodeTypes type) throws BarcodeException {
        if (type == BarcodeTypes.BIN) {
            BinBarcodeData bbd = new BinBarcodeData(bData);

            RawBarcodeData rbd = bData.getRawData((String)bData.getRawDataKeys().toArray()[0]);

            try {
                bbd.setBinId(Integer.parseInt(rbd.getRawData()));
            } catch (NumberFormatException nfe) {
                throw new BarcodeException(nfe.getMessage());
            }

            return bbd;
        } else if (type == BarcodeTypes.MEDICATION) {
            MedicationBarcodeData mbd = new MedicationBarcodeData(bData);

            Iterator<String> it = bData.getRawDataKeys().iterator();
            while(it.hasNext()) {
                String key = it.next();
                RawBarcodeData rbData = bData.getRawData(key);

                if (rbData.getFormat() == FirebaseVisionBarcode.FORMAT_DATA_MATRIX) {
                    mbd.setMedication(parseMedicationData(rbData.getRawData()));
                } else if (rbData.getFormat() == FirebaseVisionBarcode.FORMAT_UPC_E || rbData.getFormat() == FirebaseVisionBarcode.FORMAT_UPC_A) {
                    mbd.setUpcCode(rbData.getRawData());
                    //TODO: At some point get information from some database that matches with this UPC code and populates information.
                }
                // Do nothing if it isn't one of these recognized formats
            }


            return mbd;
        } else {
            return bData;
        }
    }

    /*
     * This parses the medication data out of the raw data sent from the Data Matrix 2D barcode.
     */
    private Medication parseMedicationData(String rawData) throws BarcodeException {
        Medication m = new Medication();

        try {


            // Medication Strength
            String curSection = rawData.substring(0, 4);
            m.setStrength_value(Integer.parseInt(curSection));
            //Log.d(TAG, "mediation strength done");

            //Time between doses
            curSection = rawData.substring(4, 6);
            m.setTime_between_doses(Integer.parseInt(curSection));
            //Log.d(TAG, "time between doses done");

            //Max Units per day
            curSection = rawData.substring(6,8);
            m.setMax_units_per_day(Integer.parseInt(curSection));
            //Log.d(TAG, "max units per day done");

            //Parse the value field for next two values
            curSection = rawData.substring(8,9);
            int value = 0;
            try {
                value = Integer.parseInt(curSection);
            } catch (NumberFormatException nfe) {
                if (curSection == "A") {
                    value = 10;
                } else {
                    value = 11;
                }
            }
            //Schedule Type (As Needed, As Schedule, or Both)
            //TODO: Implement Scheduling at some point, currently ignore

            //Medication Strength Units
            if (value >= 9) {
                m.setStrength_measurement("mcg");
            } else if (value >= 6) {
                m.setStrength_measurement("g");
            } else {
                m.setStrength_measurement("mg");
            }
            //Log.d(TAG, "value field parsed and done");

            //Quantity
            curSection = rawData.substring(9,12);
            m.setMedication_quantity(Integer.parseInt(curSection));
            //Log.d(TAG, "Quantity done");

            //Use By Date
            String useMonth, useDay, useYear;
            curSection = rawData.substring(12, 16);
            useYear = "20" + curSection.substring(0,2);
            useMonth = parseAlphanumeric(curSection.substring(2,3));
            useDay = parseAlphanumeric(curSection.substring(3,4));
            m.setUse_by_date(useMonth + "-" + useDay + "-" + useYear);
            //Log.d(TAG, "Use by Date Done.");

            //No of Schedule Dispenses per day
            curSection = rawData.substring(16, 17);
            int dispenses = Integer.parseInt(parseAlphanumeric(curSection)) - 1;
            //TODO: Build Schedule at some point.

            //Currently Skip the schedule plans
            int endPoint = 17 + (dispenses * 5);
            //Log.d(TAG, "schedule skip is done.");

            //Patient Name and Medication Name
            curSection = rawData.substring(endPoint);
            int idxPatient = curSection.indexOf("|");
            m.setPatient_name(curSection.substring(0, idxPatient));
            m.setName(curSection.substring(idxPatient + 1, curSection.length() - 1));

        } catch (Exception e) {
            Log.e(TAG, "Parsing Error: " + e.getMessage());
            throw new BarcodeException(e.getMessage());
        }

        return m;
    }

    /*
     * This parses the single character value into the numeric count 01 to 36;
     */
    private String parseAlphanumeric(String s) {
        String result;

        switch(s)
        {
            case "0" :
                result = "01";
                break; // break is optional
            case "1" :
                result = "02";
                break; // break is optional
            case "2" :
                result = "03";
                break; // break is optional
            case "3" :
                result = "04";
                break; // break is optional
            case "4" :
                result = "05";
                break; // break is optional
            case "5" :
                result = "06";
                break; // break is optional
            case "6" :
                result = "07";
                break; // break is optional
            case "7" :
                result = "08";
                break; // break is optional
            case "8" :
                result = "09";
                break; // break is optional
            case "9" :
                result = "10";
                break; // break is optional
            case "A" :
                result = "11";
                break; // break is optional
            case "B" :
                result = "12";
                break; // break is optional
            case "C" :
                result = "13";
                break; // break is optional
            case "D" :
                result = "14";
                break; // break is optional
            case "E" :
                result = "15";
                break; // break is optional
            case "F" :
                result = "16";
                break; // break is optional
            case "G" :
                result = "17";
                break; // break is optional
            case "H" :
                result = "18";
                break; // break is optional
            case "I" :
                result = "19";
                break; // break is optional
            case "J" :
                result = "20";
                break; // break is optional
            case "K" :
                result = "21";
                break; // break is optional
            case "L" :
                result = "22";
                break; // break is optional
            case "M" :
                result = "23";
                break; // break is optional
            case "N" :
                result = "24";
                break; // break is optional
            case "O" :
                result = "25";
                break; // break is optional
            case "P" :
                result = "26";
                break; // break is optional
            case "Q" :
                result = "27";
                break; // break is optional
            case "R" :
                result = "28";
                break; // break is optional
            case "S" :
                result = "29";
                break; // break is optional
            case "T" :
                result = "30";
                break; // break is optional
            case "U" :
                result = "31";
                break; // break is optional
            case "V" :
                result = "32";
                break; // break is optional
            case "W" :
                result = "33";
                break; // break is optional
            case "X" :
                result = "34";
                break; // break is optional
            case "Y" :
                result = "35";
                break; // break is optional
            case "Z" :
                result = "36";
                break; // break is optional
            default :
                result = "00";
        }

        return result;
    }

    /*
     * This cleans the image and preps it for barcode reading.  Currently it does nothing.
     * It was undistorting the image, but we are getting an undistorted image already so
     * this was causing a mess.
     */
    private Bitmap prepareImage(Bitmap bcImage) {
        return bcImage;
    }

    /*
     * This method uses OpenCV to rotate the image and return it in a new direction to see if there
     * is a barcode identified in other rotations.  the ML toolkit requires the barcode to be correctly
     * aligned to be visible.
     */
    private Bitmap rotateBitmap(Bitmap srcImage, double rotation) {
        Mat src = new Mat();
        Mat dst = new Mat();

        Log.d(TAG, "Rotating Image by: " + rotation);

        Utils.bitmapToMat(mImage, src);

        int centerX = Math.round(src.width() / 2);
        int centerY = Math.round(src.height() / 2);
        Point center = new Point(centerX, centerY);
        double scale = 1.0;

        int rotatedHeight = (int)Math.round(src.height());
        int rotatedWidth = (int)Math.round(src.width());

        Mat mapMatrix = Imgproc.getRotationMatrix2D(center, rotation, scale);
        Size rotatedSize = new Size(rotatedWidth, rotatedHeight);
        Mat mInter = new Mat(rotatedSize, src.type());

        Imgproc.warpAffine(src, mInter, mapMatrix, mInter.size(), Imgproc.INTER_LINEAR);
        dst = src.submat(0, mInter.rows(), 0, mInter.cols());
        mInter.copyTo(dst);

        Bitmap newImage = Bitmap.createBitmap(srcImage);
        Utils.matToBitmap(dst, newImage);
        src.release();
        dst.release();

        return newImage;
    }

    //********************************
    //Listener Structure
    //********************************
    public interface BarcodeReaderListener {
        void onSuccess(BarcodeData bd);
        void onFailure(BarcodeData bd);
    }
}
