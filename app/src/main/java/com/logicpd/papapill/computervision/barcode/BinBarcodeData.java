package com.logicpd.papapill.computervision.barcode;

/*
 * This class holds the information returned from reading the barcode attached to the Carousel bin.
 */
public class BinBarcodeData  extends BarcodeData {

    private int binId;

    //****************************************
    // Constructors
    //****************************************

    public BinBarcodeData() {
        super();
    }

    public BinBarcodeData(BarcodeData bd) {
        super(bd);
    }

    //****************************************
    // Getters and Setters
    //****************************************

    public int getBinId() {
        return binId;
    }

    public void setBinId(int binId) {
        this.binId = binId;
    }
}
