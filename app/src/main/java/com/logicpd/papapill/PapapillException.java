package com.logicpd.papapill;

import com.logicpd.LogicpdException;

/*
 * This class is the base exception for the Papapill project.  All project specific exceptions
 * thrown within the project should extend this exception.
 */
public class PapapillException extends LogicpdException {

    public PapapillException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
