package com.logicpd.papapill.device.enums;

/**
 * This enum holds the list of possible states for the "outer" command state machine.
 * This outer state machine has a basic set of states that ALL commands must contain
 * as the Command Manager loops through every command in the system, checks the state
 * of their "operation" and acts accordingly.
 */
public enum CommandOperation {
    OP_STATE_NONE,
    OP_STATE_START,
    OP_STATE_RUN
}
