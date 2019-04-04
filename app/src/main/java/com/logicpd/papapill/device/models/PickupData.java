package com.logicpd.papapill.device.models;

/**
 * This object contains data passed to and from the pill pickup command and used
 * during pill pickup / full dispense sequence. Possible things that may go here
 * in the future could be nudgefactors, pill size/type, etc.
 */
public class PickupData {

    public double mmz;
    public boolean success;

    public PickupData() { }
}
