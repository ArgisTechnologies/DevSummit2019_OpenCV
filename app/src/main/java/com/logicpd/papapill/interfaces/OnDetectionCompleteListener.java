package com.logicpd.papapill.interfaces;

import com.logicpd.papapill.computervision.detection.DetectionData;

/*
 * This is the event that fires when the Visualization detection is complete.
 */
public interface OnDetectionCompleteListener {
    void onDetectionCompleted(DetectionData data);
}
