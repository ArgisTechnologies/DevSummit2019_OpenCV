package com.logicpd.papapill.computervision.barcode;

import android.graphics.Point;
import android.graphics.Rect;

/*
 * This class holds the core raw data of a bar code.  This will be the 'data in the barcode'
 * without translation, the bounding box and the corner points.
 */
public class RawBarcodeData {

    private String rawData;
    private Rect bounds;
    private Point[] corners;
    private int format;

    //****************************************
    // Constructors
    //****************************************
    public RawBarcodeData() {
    }

    public RawBarcodeData(String rawData, Rect bounds, Point[] corners, int format) {
        this.rawData = rawData;
        this.bounds = bounds;
        this.corners = corners;
        this.format = format;
    }

    //****************************************
    // Getters and Setters
    //****************************************
    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public Point[] getCorners() {
        return corners;
    }

    public void setCorners(Point[] corners) {
        this.corners = corners;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }
}
