// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.models.PickupData;
import com.logicpd.papapill.device.models.PositionData;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;

public final class CommandMotorPickup extends BaseCommand {

    private static final DeviceCommand identifier = DeviceCommand.COMMAND_MOTOR_PICKUP;

    private PositionData positionData = new PositionData();

    public PickupData pickupData;

    public CommandMotorPickup() {
        super(identifier);
    }

    @Override
    public void execute() {
        switch(operation) {
            case OP_STATE_START:

                if (data instanceof PickupData) {
                    pickupData = (PickupData) data;
                } else {
                    Log.e(name, "Command failed. Data argument is wrong type or null.");
                    return;
                }

                // Make sure our return flag is false to start.
                pickupData.success = false;

                // No parameters for this command. Just turn on the vac pump and start moving.
                TinyGDriver.getInstance().write(CommandBuilder.CMD_COOLANT_ON);

                // Get mmZ from pickup data and use it to schedule the initial "fast move".
                // mmZ is the distance in mm from top of bin to pill. The 25 offset is distance
                // between top of bin to pick-up head home.
                double fastMoveZ = pickupData.mmz;

                // The first fast move brings pickup head to "close" to the pill height.
                CommandManager.getInstance().callCommand(
                        DeviceCommand.COMMAND_MOTOR_MOVE, String.format("z %f 1000", fastMoveZ), null);

                // Advance our state variable.
                setState(CommandState.PICKUP_WAITING_FOR_FAST_MOVE);
                break;

            case OP_STATE_RUN:
                switch(state) {
                    case PICKUP_WAITING_FOR_FAST_MOVE:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_MOVE)) {

                            // We are done with the initial "fast move." Now we must move the pickup
                            // head down slowly as we look for pressure spikes.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_MOTOR_MOVE, "z 70 100", null);

                            setState(CommandState.PICKUP_WAITING_FOR_SLOW_MOVE);
                        }
                        break;

                    case PICKUP_WAITING_FOR_SLOW_MOVE:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_MOVE)) {

                            // After this move completes, the pickup head should be in one of two
                            // places: Zero or Non-zero. If it is at zero, then the Tinyg found a pill
                            // and automatically homed the Z axis. Otherwise, any Non-zero number means
                            // the pickup head remains extended and no pill was found.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_READ_POSITION, "", positionData);

                            setState(CommandState.PICKUP_WAITING_FOR_POSITION);
                        }
                        break;

                    case PICKUP_WAITING_FOR_POSITION:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_READ_POSITION)) {

                            Log.d(name, "Pickup Z Position: " + positionData.z);

                            if (positionData.isAxisHomedZ()) {
                                Log.d(name, "Pickup Z Success = true");

                                // The current position is read back as 0, meaning a pill was picked
                                // and the pickup head returned to the top (zero/home) position.
                                pickupData.success = true;

                                // Mark our command as complete.
                                setState(CommandState.COMPLETE_STATE);
                            } else {
                                Log.d(name, "Pickup Z Success = false");

                                // Else if any other position is read, it means no pill was picked
                                // and the pickup head remains extended. An extra step must be taken
                                // to home the Z axis before calling the command complete.
                                CommandManager.getInstance().callCommand(
                                        DeviceCommand.COMMAND_MOTOR_HOME, "z", null);

                                setState(CommandState.PICKUP_WAITING_FOR_HOME);
                            }
                        }
                        break;

                    case PICKUP_WAITING_FOR_HOME:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_HOME)) {

                            // Now complete the command.
                            setState(CommandState.COMPLETE_STATE);
                        }
                        break;
                }
                break;

            default:
                break;
        }
    }
}
