package com.logicpd.papapill.computervision.barcode;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Set;

/*
 * This class is the generic barcode data that comes from an image.  If an image detects a barcode
 * or multiple barcodes.  That information is initially of unknown form and function with basic
 * information attached to a raw string of data from the barcode.  This is the parent class
 * for the specific barcode data objects of MedicationBarcodeData and BinBarcodeData.
 */
@SuppressWarnings("unused")
public class BarcodeData {

    private final HashMap<String, RawBarcodeData> rawData;
    private Bitmap baseImage;
    private String errorMessage;
    private BarcodeTypes type;

    //****************************************
    // Constructors
    //****************************************
    public BarcodeData() {
        rawData = new HashMap<>();
        baseImage = null;
    }

    public BarcodeData(BarcodeData bd) {
        rawData = new HashMap<>();
        setBaseImage(bd.getBaseImage());
        setErrorMessage(bd.getErrorMessage());
        setType(bd.getType());

        for (String key : bd.getRawDataKeys()) {
            addRawData(key, bd.getRawData(key));
        }
    }

    //****************************************
    // Getters and Setters
    //****************************************
    public RawBarcodeData getRawData(String barcodeId) {
        return rawData.get(barcodeId);
    }

    public void addRawData(String barcodeId, RawBarcodeData rawData) {
        this.rawData.put(barcodeId, rawData);
    }

    public void removeRawData(String barcodeId) {
        rawData.remove(barcodeId);
    }

    public void clearRawData() {
        rawData.clear();
    }

    public boolean isEmptyRawData() {
        return rawData.isEmpty();
    }

    public Set<String> getRawDataKeys() {
        return rawData.keySet();
    }

    public Bitmap getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(Bitmap baseImage) {
        this.baseImage = baseImage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BarcodeTypes getType() {
        return type;
    }

    public void setType(BarcodeTypes type) {
        this.type = type;
    }

}
