package com.logicpd.papapill.computervision.barcode;

import com.logicpd.papapill.PapapillException;

/*
 * This is the base exception for any exceptions thrown within the Barcode processing for the
 * Papapill project.
 */
public class BarcodeException extends PapapillException {

    public BarcodeException(String exceptionMessage) {super(exceptionMessage); }
}
