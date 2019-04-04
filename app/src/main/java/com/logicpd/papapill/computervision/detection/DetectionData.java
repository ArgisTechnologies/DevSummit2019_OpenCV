package com.logicpd.papapill.computervision.detection;

import android.graphics.Bitmap;

import com.logicpd.papapill.device.models.CoordinateData;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/*
 * This class is the object that is sent back to main system with the appropriate
 * data required to move to and pick a specific pill.
 */
public class DetectionData {

    //variables
    private int pillCount;
    private DetectionStatus status;
    private String errorMessage;
    private Bitmap selectedPill;
    private ArrayList<PillLocation> selectedPills  = new ArrayList<PillLocation>();

    //Constructors
    public DetectionData() {}

    public DetectionData(int pillCount, DetectionStatus status, String errorMessage, Bitmap selectedPill, ArrayList<PillLocation> selectedPills) {
        this.pillCount = pillCount;
        this.status = status;
        this.errorMessage = errorMessage;
        this.selectedPill = selectedPill;
        this.selectedPills = selectedPills;
    }

    //Getters and Setters
    public int getPillCount() {
        return pillCount;
    }

    public void setPillCount(int pillCount) {
        this.pillCount = pillCount;
    }

    public DetectionStatus getStatus() {
        return status;
    }

    public void setStatus(DetectionStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Bitmap getSelectedPill() {
        return selectedPill;
    }

    public void setSelectedPill(Bitmap selectedPill) {
        this.selectedPill = selectedPill;
    }

    public ArrayList<PillLocation> getSelectedPills() {
        return selectedPills;
    }

    public void setSelectedPills(ArrayList<PillLocation> selectedPills) {
        this.selectedPills = selectedPills;
    }
}
