// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.MachineManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;

public final class CommandMotorMove extends BaseCommand {

    // Identifier for this command.
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_MOTOR_MOVE;

    // Internal parameters.
    private String axis;
    private double distance;
    private int feedrate;

    // Local state variable to keep track of motor running state.
    private boolean isMotorRunningLast;
    private boolean isMotorHoldingLast;

    public CommandMotorMove() {
        super(identifier);
    }

    /**
     * This helper method checks to see if the motor is done moving or not. For the Tinyg, movement
     * finished is defined as a machine state transition from the running state to any other state.
     * @return
     */
    // TODO: A better way to do this may be sending a query rather than monitoring status reports.
    private boolean isMoveFinished() {
        boolean isMotorRunning = MachineManager.getInstance().isMachineRunning();
        boolean isMotorHolding = MachineManager.getInstance().isMachineHolding();
        boolean retval;

        // First check to see if the TinyG was switched into the "Hold" state from any other state.
        // If so, then we must return false here as the motor is NOT actually done moving. The only
        // time TinyG reached HOLD state is if the pressure sensor tripped, in which case, our custom
        // TinyG firmware will automatically schedule a Z-axis move to the zero position. For a brief
        // second, it will be in HOLD state and then shortly switch back to RUN state. If we did not
        // have this extra conditional here, it would look as if the move had finished but in reality
        // it has not.
        // TODO: This works, but I'm convinced there is a better way to handle this somewhere.
        if (!isMotorHoldingLast && isMotorHolding) {
            Log.d(name, "Machine is holding!");
            retval = false;
        } else {
            // With that initial check out of the way, simply check to see if the TinyG switched from
            // RUN state to any other state. If so, then we are no longer moving and are considered
            // "done." Otherwise, we are not "done" yet.
            retval = (isMotorRunningLast && !isMotorRunning);
        }

        isMotorRunningLast = isMotorRunning;
        isMotorHoldingLast = isMotorHolding;
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
                // Separate the parameters via whitespace regex.
                String[] paramArray = getParamArray();
                // This command is expected to have 3 parameters. Ensure we have
                // that amount before continuing (we'll ignore any additional parameters).
                if (paramArray.length < 3) {
                    Log.e(name, "Command failed due to too few or bad parameters.");
                    return;
                } else {
                    // Assign our parameters.
                    // TODO: Must validate these parameters.
                    axis = paramArray[0];
                    distance = Double.parseDouble(paramArray[1]);
                    feedrate = Integer.parseInt(paramArray[2]);

                    // TODO: Perform some limit checking here.

                    // The command is now primed. Set the state to something other than the waiting
                    // state. This will cause command manager to switch the operation state of
                    // the command to run mode.
                    setState(CommandState.MOTOR_WAIT_FOR_PREVIOUS_MOVE_TO_FINISH);
                }
                break;

            case OP_STATE_RUN:
                // Run the motor moving state machine.
                switch (state) {
                    case MOTOR_WAIT_FOR_PREVIOUS_MOVE_TO_FINISH:
                        // If the axis needs homing, we need to ensure that the homing command has
                        // finished its move before trying to move again.
                        // TODO: Replace this with an isMotorsMoving() method in Command Manager.
                        if(CommandManager.getInstance().isCommandDone(DeviceCommand.COMMAND_MOTOR_HOME)) {
                            // All motors are at a standstill and the parameters are in the correct
                            // format. Send the command to the motor control board.
                            TinyGDriver.getInstance().write(CommandBuilder
                                    .CMD_STRAIGHT_FEED(axis, distance, feedrate));

                            // Then start up the state machine.
                            setState(CommandState.MOTOR_MOVING_STATE);
                        }
                        break;
                    case MOTOR_MOVING_STATE:
                        // The motor is currently moving, we must wait until the move is finished.
                        if(isMoveFinished()) {
                            // The move has completed. We're done and back to waiting.
                            setState(CommandState.COMPLETE_STATE);

                            Log.d(name, "Motor move has completed");
                        }
                        break;
                }
                break;

            default:
                break;
        }
    }
}
