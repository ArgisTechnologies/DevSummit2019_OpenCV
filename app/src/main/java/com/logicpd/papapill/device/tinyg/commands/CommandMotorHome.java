// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.MachineManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;

public final class CommandMotorHome extends BaseCommand {

    // Identifier for this command.
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_MOTOR_HOME;

    // Internal parameters.
    private String axis;

    // Internal variable to keep track of motor homing state.
    private boolean isMotorHomingLast;

    public CommandMotorHome() {
        super(identifier);
    }

    /**
     * This helper method checks to see if the motor is done homing or not. For the TinyG, home
     * finished is defined as a machine state transition from the homing state to any other state.
     * @return
     */
    // TODO: A better way to do this may be sending a query rather than monitoring status reports.
    private boolean isHomeFinished() {
        boolean isMotorHoming = MachineManager.getInstance().isMachineHoming();
        boolean retval;

        // Check if TinyG indicates a state transition from homing to not homing.
        retval = (isMotorHomingLast && !isMotorHoming);

        isMotorHomingLast = isMotorHoming;
        return retval;
    }

    /**
     * This is the execute method for the motor move command. It is simply the state machine for
     * the motor move command. It tells the motor to start moving, and will exit when the motor
     * indicates it is finished with the move.
     * @return
     */
    @Override
    public void execute() {

        switch (operation) {
            case OP_STATE_START:
                // Separate the parameters by whitespace.
                String[] paramArray = getParamArray();
                // This command is expected to have 1 parameter. Ensure we have
                // that amount before continuing (we'll ignore any additional parameters).
                if (paramArray.length < 1) {
                    Log.e(name, "Command failed due to too few or bad parameters.");
                    return;
                } else {
                    // Assign our parameters.
                    axis = paramArray[0];

                    // TODO: Range checking

                    // Advance the command state.
                    setState(CommandState.MOTOR_WAIT_FOR_PREVIOUS_MOVE_TO_FINISH);
                }
                break;

            case OP_STATE_RUN:
                // Run the motor homing state machine.
                switch (state) {
                    case MOTOR_WAIT_FOR_PREVIOUS_MOVE_TO_FINISH:
                        // Make sure no motors are already moving due to the motor move command, which
                        // is the only other command that could cause a previous move to be currently
                        // ongoing (the other one being home command but that is not possible since
                        // by getting to this point, the command was idle meaning no homing was taking
                        // place on the TinyG.
                        if(CommandManager.getInstance().isCommandDone(DeviceCommand.COMMAND_MOTOR_MOVE))
                        {
                        // All motors are at a standstill and the parameters are in the correct
                        // format. Send the command to the motor control board.
                        switch (axis) {
                            case "x":
                                TinyGDriver.getInstance().write(CommandBuilder
                                        .CMD_APPLY_HOME_X_AXIS);
                                break;
                            case "z":
                                TinyGDriver.getInstance().write(CommandBuilder
                                        .CMD_APPLY_HOME_Z_AXIS);
                                break;
                            case "a":
                                TinyGDriver.getInstance().write(CommandBuilder
                                        .CMD_APPLY_HOME_A_AXIS);
                                break;
                            default:
                                break;
                        }

                        // Then start up the state machine.
                        setState(CommandState.MOTOR_MOVING_STATE);
                    }
                        break;
                    case MOTOR_MOVING_STATE:
                        // The motor is currently moving, we must wait until the move is finished.
                        if(isHomeFinished()) {
                            // The move has completed. We're done.
                            setState(CommandState.COMPLETE_STATE);

                            Log.d(name, "Motor HOME has completed");
                        }
                        break;
                }
                break;

            default:
                break;
        }
    }
}
