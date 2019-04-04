package com.logicpd.papapill.computervision.barcode;

import com.logicpd.papapill.data.Medication;

/*
 * This class holds the information gained from reading the barcode(s) from a pill bottle.  There
 * may be more than one bar code to collect all data, so this will include an array of barcode
 * raw data and types in a key value pair.
 */
public class MedicationBarcodeData extends BarcodeData {

    private String upcCode;
    private Medication medication;

    //****************************************
    // Constructors
    //****************************************

    public MedicationBarcodeData() {
        super();
    }

    public MedicationBarcodeData(BarcodeData bd) {
        super(bd);
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }
}
