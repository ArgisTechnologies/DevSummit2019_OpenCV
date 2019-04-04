package com.logicpd.papapill.device.tinyg;

import android.util.Log;

/**
 * This class contains several global parameters that factor in calculating
 * motor movements throughout the system.
 *
 * Frank approved my next implementation to change indexing.
 * It is bin0 (Carousel HOME), then clockwise, 1, 2 ....14.
 */
public final class CoordinateManager {
    private static final String TAG = "CoordinateManager";
    private static final int BIN_COUNT = 15;
    private static final int CIRCLE_DEGREE = 360;
    private static final int HALF_CIRCLE_DEGREE = CIRCLE_DEGREE/2;
    private static final int BIN_ANGLE = (CIRCLE_DEGREE / BIN_COUNT);
    private static final double DOOR_OFFSET = 3.8;

    public static final int DISPENSE_BIN_OFFSET = 100;

    // HOME bin location is 10th (from 0th)
    public static final int HOME_BIN = 10;

    /*
     * Home is not the same as optical home.
     * 10th bin (clockwise) from optical home.
     */
    public static int getHomeBinOffset()
    {
        return HOME_BIN * BIN_ANGLE;
    }

    /**
     * Given the binId (a value stored in the database representing a bin),
     * return the ideal location in degrees as an offset from the origin.
     * @param binId
     * @return
     */
    public static int getBinOffset(int binId) {
        if(binId < 0) {
            Log.e(TAG, "Invalid binId");
            return 0;
        }
        // The bin Id (primary key) should be number from 1 to 15.
        // 0 is HOME
        Log.d(TAG, "getBinOffset() binId:"+binId);
        // apply offset to where HOME actually is
        int binIndex = (binId + HOME_BIN);

        // wrap around the queue if index is larger than max value

        if(binIndex > BIN_COUNT)
        {
            binIndex =  binIndex - BIN_COUNT;
        }

        Log.d(TAG, "binIndex:"+binIndex);
        return binIndex * BIN_ANGLE;
    }

    /**
     * Given the binId (a value stored in the database representing a bin),
     * return the ideal location in degrees as an offset for the door (user manual access to load meds)
     * @param binId
     * @return
     */
    public static int getDoorOffset(int binId) {
        Log.d(TAG, "getDoorOffset() binId:"+binId);

        int binIndex = getBinOffset(binId)- (int)(DOOR_OFFSET * BIN_ANGLE);

        if(binIndex < 0)
        {
            binIndex = CIRCLE_DEGREE + binIndex;
        }

        Log.d(TAG, "Door Offset (degrees):"+binIndex);
        return binIndex;
    }

    /**
     * Returns the shortest path to move from current angle to target angle.
     * @param current
     * @param target
     * @return
     */
    public static double getShortestAngle(double current, double target) {
        // Take the difference between the two input angles. Normalize the angle
        // to within [-180, 180) range to find the shortest path.
        double diff = (target - current + HALF_CIRCLE_DEGREE) % CIRCLE_DEGREE - HALF_CIRCLE_DEGREE;
        return (diff < -HALF_CIRCLE_DEGREE) ? (diff + CIRCLE_DEGREE) : diff;
    }

}
