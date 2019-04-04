package com.logicpd.papapill.interfaces;

/**
 * This interface is used to notify the UI when a hardware call has finished.  In this case, we are
 * using it for notifying the UI that a bin has been placed into position and is ready to dispense.
 *
 * @author alankilloren
 */
public interface OnBinReadyListener {
    void onBinReady();
}
