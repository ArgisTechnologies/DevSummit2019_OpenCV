package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.models.PositionData;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.CoordinateManager;

public final class CommandHomeAll extends BaseCommand
{
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_HOME_ALL;
    private PositionData positionData = new PositionData();

    public CommandHomeAll()
    {
        super(identifier);
    }

    @Override
    public void execute() {

        switch (operation) {
            case OP_STATE_START:
                CommandManager.getInstance().callCommand(DeviceCommand.COMMAND_MOTOR_HOME, "z", null);
                setState(CommandState.HOME_ALL_WAITING_FOR_Z_HOME);
            break;

            case OP_STATE_RUN:
                switch (state) {
                    case HOME_ALL_WAITING_FOR_Z_HOME:
                        if (CommandManager.getInstance().isCommandDone(DeviceCommand.COMMAND_MOTOR_HOME)) {

                            Log.d("RESET_WAITING_FOR_Z_HOME", "Calling COMMAND_MOTOR_HOME -> X");
                            // Now call the same homing command as before, except with an updated
                            // parameter this time around.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_MOTOR_HOME, "x", null);

                            // The z axis is homed and the pickup head is now raised up all the
                            // way so as to not interfere with the other axes. Our next action is
                            // to home the x axis (the radial-stage). Call the command and advance
                            // to our next state.
                            setState(CommandState.HOME_ALL_WAITING_FOR_X_HOME);
                        }
                        break;

                    case HOME_ALL_WAITING_FOR_X_HOME:
                        if (CommandManager.getInstance().isCommandDone(DeviceCommand.COMMAND_MOTOR_HOME))
                        {
                            Log.d("RESET_WAITING_FOR_R_HOME", "Calling COMMAND_MOTOR_HOME -> R");

                            double targetPosition = CoordinateManager.getHomeBinOffset();
                            String params = String.format("%f 0.3", targetPosition);

                            // Once both Z and Rho are homed, its now safe to rotate the carousel
                            // to the proper bin location.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_ROTATE_CAROUSEL, params, null);

                            // Advance our state variable.
                            setState(CommandState.HOME_ALL_WAITING_FOR_R_HOME);
                        }
                        break;

                    case HOME_ALL_WAITING_FOR_R_HOME:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_ROTATE_CAROUSEL)) {

                            Log.d("CommandHomeAll", "Calling COMPLETE_STATE");
                            setState(CommandState.COMPLETE_STATE);
                        }
                        break;

                    default:
                        break;
                }
        }
    }
}
