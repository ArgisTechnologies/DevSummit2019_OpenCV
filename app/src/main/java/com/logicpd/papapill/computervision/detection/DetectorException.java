package com.logicpd.papapill.computervision.detection;

import com.logicpd.papapill.PapapillException;

/*
 * This is the base exception for any exceptions that will be thrown by the detection process
 * within the Papapill project.
 */
public class DetectorException extends PapapillException {

    public DetectorException(String exceptionMessage) {super(exceptionMessage); }
}
