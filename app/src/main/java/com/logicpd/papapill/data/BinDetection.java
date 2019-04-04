package com.logicpd.papapill.data;

import android.graphics.Bitmap;

/*
 * This class defines the information needed for vision detection of each bin.  This will store
 * specific information to speed and stabilize detection.
 * Brady Hustad - Logic PD - Initial Creation - 2018-10-05
 */
public class BinDetection {
    private int binId;
    private Bitmap detectImage;
    private int pillCount;
    private int upperLeftX;
    private int upperLeftY;
    private int lowerRightX;
    private int lowerRightY;
    private String pillTemplate;
    private boolean runAnalysis;
    private int fillLevel;

    public int getBinId() {
        return binId;
    }

    public void setBinId(int binId) {
        this.binId = binId;
    }

    public Bitmap getDetectImage() {
        return detectImage;
    }

    public void setDetectImage(Bitmap detectImage) {
        this.detectImage = detectImage;
    }

    public int getPillCount() {
        return pillCount;
    }

    public void setPillCount(int pillCount) {
        this.pillCount = pillCount;
    }

    public int getUpperLeftX() {
        return upperLeftX;
    }

    public void setUpperLeftX(int upperLeftX) {
        this.upperLeftX = upperLeftX;
    }

    public int getUpperLeftY() {
        return upperLeftY;
    }

    public void setUpperLeftY(int upperLeftY) {
        this.upperLeftY = upperLeftY;
    }

    public int getLowerRightX() {
        return lowerRightX;
    }

    public void setLowerRightX(int lowerRightX) {
        this.lowerRightX = lowerRightX;
    }

    public int getLowerRightY() {
        return lowerRightY;
    }

    public void setLowerRightY(int lowerRightY) {
        this.lowerRightY = lowerRightY;
    }

    public String getPillTemplate() {
        return pillTemplate;
    }

    public void setPillTemplate(String pillTemplate) {
        this.pillTemplate = pillTemplate;
    }

    public boolean isRunAnalysis() {
        return runAnalysis;
    }

    public void setRunAnalysis(boolean runAnalysis) {
        this.runAnalysis = runAnalysis;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }
}
