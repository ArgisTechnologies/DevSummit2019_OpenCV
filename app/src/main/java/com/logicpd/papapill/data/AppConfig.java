package com.logicpd.papapill.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.logicpd.papapill.BuildConfig;
import com.logicpd.papapill.PapapillException;

import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/*
 * This is a data object to hold the configuration data as loaded from the config.json file.
 * Brady Hustad - Logic PD - 2018-08-30 - Initial Creation
 * Brady Hustad - Logic PD - 2018-12-12 - Moved to App wide location and renamed to fit all needs.
 */
public class AppConfig {

    private static final String TAG = AppConfig.class.getSimpleName();

    //Private Variables
    private double[] cameraMatrix; //{1157.761879014254, 0, 820, 0, 1156.661159728501, 616, 0, 0, 1};
    private double[] distortionCoeffs; //{-0.4072881805734203, 0.1463792551013924, 0, 0, 0};

    private double rho0; // radius of bin center arc in millimeters
    private double mmm; // slope of line used to calculate mm/pixel given fill percentage 0-100
    private double mmb; // y-intercept of line used to calculate mm/pixel given fill percentage 0-100
    private int imageWidth; // the width of the image to capture from the camera, used for all calculation on image
    private int imageHeight; // the height of the image to capture from the camera, used for all calculation on image
    private double convergentTheta; // angle in degrees of Convergent point that moves minimally with fill
    private double convergentRho; // radius in millimeters of Convergent point that moves minimally with fill
    private int convergentPixelX; // X coordinate in pixels of Convergent point that moves minimally with fill
    private int convergentPixelY; // Y coordinate in pixels of Convergent point that moves minimally with fill
    private double rotation;

    //Variables for the deadzone of the picking head.
    // - Rho Limit is the positive and negative on the Rho axis for picking from the 0 point.
    // - Theta is the intercept of y = x/12 + b (intercept) bigger number more space, littler less
    double deadzoneRhoLimit;
    double deadzoneThetaIntercept;

    int detectionsReturned;

    private double binThetaMax;
    private double binThetaMin;
    private double binRhoMax;
    private double binDepth; //The depth of the bin in mm.

    private double templateSlope;
    private int[][] templateUpperLeftXY;

    //Motor control variables
    private double absPositionOffset;

    public boolean isCameraAvailable = false;
    public boolean isTinyGAvailable = false;

    public double camera2TinyGRhoOffset;
    public double camera2TinyGThetaOffset;

    /******************************************************
     * Singleton Setup
     ******************************************************/
    private static class InstanceHolder {
        private static final AppConfig mConfig = new AppConfig();
    }

    public static AppConfig getInstance() {
        return InstanceHolder.mConfig;
    }

    /******************************************************
     * Constructors
     ******************************************************/
    //Assumes the file is the generic file from the SDcard.
    private AppConfig() {
    }

    private AppConfig(String jsonFile) {
        try {
            parseJson(jsonFile);
            Log.d("Appconfig", "parseJson()");
        } catch(PapapillException pe) {
            Log.e(TAG, pe.getMessage());
        }
    }

    public boolean loadFile(Context context)
    {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("config.json")));

            StringBuilder text = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                Log.d("AppConfig", "text:"+line);
            }
            br.close();

            parseJson(text.toString());
            return true;
        }
        catch (Exception ex)
        {
            Log.e("AppConfig", "loadFile failed");
            return false;
        }
    }

    /******************************************************
     * Getters and Setters
     ******************************************************/



    public double[] getCameraMatrix() {
        return cameraMatrix;
    }

    private void setCameraMatrix(double[] cameraMatrix) {
        this.cameraMatrix = cameraMatrix;
    }

    public double[] getDistortionCoeffs() {
        return distortionCoeffs;
    }

    private void setDistortionCoeffs(double[] distortionCoeffs) {
        this.distortionCoeffs = distortionCoeffs;
    }

    public double getRho0() {
        return rho0;
    }

    private void setRho0(double rho0) {
        this.rho0 = rho0;
    }

    public double getMmm() {
        return mmm;
    }

    private void setMmm(double mmm) {
        this.mmm = mmm;
    }

    public double getMmb() {
        return mmb;
    }

    private void setMmb(double mmb) {
        this.mmb = mmb;
    }

    public int getImageWidth() { return imageWidth; }

    private void setImageWidth(int imageWidth) { this.imageWidth = imageWidth; }

    public int getImageHeight() { return  imageHeight; }

    private void setImageHeight(int imageHeight) {this.imageHeight = imageHeight; }

    public double getConvergentTheta() {
        return convergentTheta;
    }

    private void setConvergentTheta(double convergentTheta) {
        this.convergentTheta = convergentTheta;
    }

    public double getConvergentRho() {
        return convergentRho;
    }

    private void setConvergentRho(double convergentRho) {
        this.convergentRho = convergentRho;
    }

    public int getConvergentPixelX() {
        return convergentPixelX;
    }

    private void setConvergentPixelX(int convergentPixelX) {
        this.convergentPixelX = convergentPixelX;
    }

    public int getConvergentPixelY() {
        return convergentPixelY;
    }

    private void setConvergentPixelY(int convergentPixelY) {
        this.convergentPixelY = convergentPixelY;
    }

    public double getRotation() {
        return rotation;
    }

    private void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getBinThetaMax() {
        return binThetaMax;
    }

    private void setBinThetaMax(double binThetaMax) {
        this.binThetaMax = binThetaMax;
    }

    public double getBinThetaMin() {
        return binThetaMin;
    }

    private void setBinThetaMin(double binThetaMin) {
        this.binThetaMin = binThetaMin;
    }

    public double getBinRhoMax() {
        return binRhoMax;
    }

    private void setBinRhoMax(double binRhoMax) {
        this.binRhoMax = binRhoMax;
    }

    public double getBinDepth() {
        return binDepth;
    }

    private void setBinDepth(double binDepth) {
        this.binDepth = binDepth;
    }

    public double getAbsPositionOffset() { return absPositionOffset; }

    private void setAbsPositionOffset(double absPositionOffset) {
        this.absPositionOffset = absPositionOffset;
    }

    public double getTemplateSlope() { return templateSlope; }

    private void setTemplateSlope(double templateSlope) { this.templateSlope = templateSlope; }

    public int[][] getTemplateUpperLeftXY() { return templateUpperLeftXY; }

    private void setTemplateUpperLeftXY(int[][] templateUpperLeftXY) { this.templateUpperLeftXY = templateUpperLeftXY; }

    public double getDeadzoneRhoLimit() {
        return deadzoneRhoLimit;
    }

    public void setDeadzoneRhoLimit(double deadzoneRhoLimit) {
        this.deadzoneRhoLimit = deadzoneRhoLimit;
    }

    public double getDeadzoneThetaIntercept() {
        return deadzoneThetaIntercept;
    }

    public void setDeadzoneThetaIntercept(double deadzoneThetaIntercept) {
        this.deadzoneThetaIntercept = deadzoneThetaIntercept;
    }

    public int getDetectionsReturned() {
        return detectionsReturned;
    }

    public void setDetectionsReturned(int detectionsReturned) {
        this.detectionsReturned = detectionsReturned;
    }

    public void setCameraAvailable(boolean cameraIsAvailable) {
        isCameraAvailable = cameraIsAvailable;
    }

    public boolean getCameraAvailable()
    {
        return isCameraAvailable;
    }

    public void setTinyGAvailable(boolean tinyGIsAvailable)
    {
        isTinyGAvailable = tinyGIsAvailable;
    }

    public boolean getTinyGAvailable()
    {
        return isTinyGAvailable;
    }

    public double getCamera2TinyGRhoOffset() {
        return camera2TinyGRhoOffset;
    }

    public void setCamera2TinyGRhoOffset(double rho)
    {
        camera2TinyGRhoOffset = rho;
    }

    public double getCamera2TinyGThetaOffset(){
        return camera2TinyGThetaOffset;
    }

    public void setCamera2TinyGThetaOffset(double theta) {
        camera2TinyGThetaOffset = theta;
    }

    /******************************************************
     * Private Methods
     ******************************************************/

    /*
     * This parses the config.json file into the various fields.
     */
    private void parseJson(String jsonFile) throws PapapillException {
        try {
            JSONObject config = new JSONObject(jsonFile);

            String cameraMatrix = config.getString("cameraMatrix");
            List<String> cm = Arrays.asList(cameraMatrix.split("\\s*,\\s*"));
            double[] cmArray = new double[cm.size()];
            for (int i = 0; i < cm.size(); i++) {
                cmArray[i] = Double.parseDouble(cm.get(i));
            }
            setCameraMatrix(cmArray);

            String distortionCoeffs = config.getString("distortionCoefficients");
            List<String> dc = Arrays.asList(distortionCoeffs.split("\\s*,\\s*"));
            double[] dcArray = new double[dc.size()];
            for (int i = 0; i < dc.size(); i++) {
                dcArray[i] = Double.parseDouble(dc.get(i));
            }
            setDistortionCoeffs(dcArray);

            setRho0(config.getDouble("rho0"));
            setMmm(config.getDouble("mmm"));
            setMmb(config.getDouble("mmb"));
            setImageWidth(config.getInt("imageWidth"));
            setImageHeight(config.getInt("imageHeight"));
            setConvergentTheta(Math.toRadians(config.getDouble("convergentTheta")));
            setConvergentRho(getRho0() + config.getDouble("convergentRho"));
            setConvergentPixelX(config.getInt("convergentPixelX"));
            setConvergentPixelY(config.getInt("convergentPixelY"));
            setRotation(Math.toRadians(config.getDouble("rotation")));

            setDeadzoneRhoLimit(config.getDouble("deadzoneRhoLimit"));
            setDeadzoneThetaIntercept(config.getDouble("deadzoneThetaIntercept"));

            setDetectionsReturned(config.getInt("detectionsReturned"));

            setBinThetaMax(config.getDouble("binThetaMax"));
            setBinThetaMin(config.getDouble("binThetaMin"));
            setBinRhoMax(config.getDouble("binRhoMax"));
            setBinDepth(config.getDouble("binDepth"));

            setAbsPositionOffset(config.getDouble("absPositionOffset"));

            setTemplateSlope(config.getDouble("templateSlope"));

            String templateULXY = config.getString("templateUpperLeftXY");
            List<String> ulxy = Arrays.asList(templateULXY.split("\\s*,\\s*"));
            //Log.d(TAG, "ulxy content:" + templateULXY);
            //Log.d(TAG, "ulxy size" + ulxy.size());
            int[][] ulxyArray = new int[ulxy.size() / 3][3];
            int ptrThree = 0;
            int ptrArray = 0;
            for (int i = 0; i < ulxy.size(); i++) {
                if (ptrThree >= 3) {
                    ptrThree = 0;
                    ptrArray++;
                }
                ulxyArray[ptrArray][ptrThree] = Integer.parseInt(ulxy.get(i));
                //Log.d(TAG, "ulArray[" + ptrArray + "][" + ptrThree + "] = " + ulxyArray[ptrArray][ptrThree]);
                ptrThree++;
            }
            setTemplateUpperLeftXY(ulxyArray);

            setCameraAvailable(config.getBoolean("isCameraAvailable"));
            setTinyGAvailable(config.getBoolean("isTinyGAvailable"));

            String offsetRho = config.getString("camera2TinyGRhoOffset");
            setCamera2TinyGRhoOffset(Double.parseDouble(offsetRho));

            String offsetTheta = config.getString("camera2TinyGThetaOffset");
            setCamera2TinyGThetaOffset(Double.parseDouble(offsetTheta));
        }
        catch (JSONException je)
        {
            Log.e(TAG, je.getMessage());
            throw new PapapillException("Error in building config information: " + je.getMessage());
        }
    }
}
