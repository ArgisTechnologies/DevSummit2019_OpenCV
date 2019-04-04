package com.logicpd;

/*
 * This is the core Exception class for Logic PD project.  This should identify any exception that
 * is thrown within a Logic PD application.
 */
public class LogicpdException extends Exception {

    public LogicpdException(String exceptionMessage) {
        super(exceptionMessage);
    }

}
