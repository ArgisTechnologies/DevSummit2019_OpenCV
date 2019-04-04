package com.logicpd.papapill.device.models;

/**
 * This object contains data pill position data returned from the OpenCV
 * image detection algorithm during full dispense sequence.
 */
public class CoordinateData {

    public double radius;
    public double degrees;
    public double z;
    public double mmZ;

    public CoordinateData() { }
}
