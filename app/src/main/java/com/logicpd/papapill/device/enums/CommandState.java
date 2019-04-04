package com.logicpd.papapill.device.enums;

/**
 * This enum holds the list of possible states for the "inner" command state machine.
 * These are states that are specific to a particular command. These states are used
 * internally by the individual commands and are unique to that command (with the
 * exception of the WAITING_STATE and COMPLETE_STATE which are shared are "common"
 * states used by multiple commands).
 */
public enum CommandState {
    // The initial waiting state. Every command starts in this state.
    WAITING_STATE,

    // The ending state. Every command finishes its execution in this state.
    COMPLETE_STATE,

    // Initialize.
    INITIALIZE_SENDING_NEXT,
    INITIALIZE_WAITING_FOR_RESPONSE,

    // Full dispense.
    FULL_DISPENSE_WAITING_FOR_Z_HOME,
    FULL_DISPENSE_WAITING_FOR_R_HOME,
    FULL_DISPENSE_WAITING_FOR_CAROUSEL_MOVE,
    FULL_DISPENSE_WAITING_FOR_CAMERA_CENTERED,
    FULL_DISPENSE_WAITING_FOR_CAMERA_PICTURE,
    FULL_DISPENSE_CAMERA_PICTURE_DONE,
    FULL_DISPENSE_WAITING_FOR_R_REPOSITION,
    FULL_DISPENSE_WAITING_FOR_READ_POSITION,
    FULL_DISPENSE_WAITING_FOR_THETA_REPOSITION,
    FULL_DISPENSE_MOVING_TO_PICKUP_PILL,
    FULL_DISPENSE_WAIT_FOR_DISPENSE_CONFIRMATION,
    FULL_DISPENSE_WAIT_FOR_VISION_ERROR_DISPLAY,
    FULL_DISPENSE_WAIT_FOR_PICK_ERROR_DISPLAY,

    // Pickup.
    PICKUP_WAITING_FOR_FAST_MOVE,
    PICKUP_WAITING_FOR_SLOW_MOVE,
    PICKUP_WAITING_FOR_POSITION,
    PICKUP_WAITING_FOR_HOME,

    // Rotate Carousel
    ROTATE_CAROUSEL_WAITING_FOR_MOVE_FINISH,
    ROTATE_CAROUSEL_WAITING_FOR_READ_FINISH,

    // General states that several commands may use.
    WAITING_FOR_RESPONSE,

    // Motor movement specific states.
    MOTOR_MOVING_STATE,
    MOTOR_WAIT_FOR_PREVIOUS_MOVE_TO_FINISH,

    // Reset
    RESET_WAITING_FOR_IDLE,
    RESET_WAITING_FOR_HOME,

    // Home
    HOME_ALL_WAITING_FOR_Z_HOME,
    HOME_ALL_WAITING_FOR_R_HOME,
    HOME_ALL_WAITING_FOR_X_HOME,
}
