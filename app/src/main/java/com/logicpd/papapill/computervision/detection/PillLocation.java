package com.logicpd.papapill.computervision.detection;

import com.logicpd.papapill.device.models.CoordinateData;

import org.opencv.core.Point;

/*
 * This class holds each selected pills location information, both pixel data and coordinate data.
 */
public class PillLocation {

    //Private Variables
    private CoordinateData coordinateData = new CoordinateData();
    private Point pixelData = new Point();

    //Constructors
    public PillLocation() { }

    public PillLocation(CoordinateData coordinateData, Point pixelData) {
        this.coordinateData = coordinateData;
        this.pixelData = pixelData;
    }

    //Getters and Setters
    public CoordinateData getCoordinateData() {
        return coordinateData;
    }

    public void setCoordinateData(CoordinateData coordinateData) {
        this.coordinateData = coordinateData;
    }

    public Point getPixelData() {
        return pixelData;
    }

    public void setPixelData(Point pixelData) {
        this.pixelData = pixelData;
    }
}
