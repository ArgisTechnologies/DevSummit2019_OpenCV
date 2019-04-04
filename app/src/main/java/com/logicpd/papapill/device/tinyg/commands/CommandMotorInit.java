package com.logicpd.papapill.device.tinyg.commands;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.tinyg.BoardDefaults;
import com.logicpd.papapill.device.tinyg.CommandManager;

public final class CommandMotorInit extends BaseCommand {

    private static final DeviceCommand identifier = DeviceCommand.COMMAND_MOTOR_INIT;

    private int idx;

    public CommandMotorInit() {
        super(identifier);
    }

    @Override
    public void execute() {
        switch(operation) {
            case OP_STATE_START:

                // Initialize our index variable to 0 so we start at the beginning of the TinyG
                // settings array.
                idx = 0;

                // Set our state to begin sending.
                setState(CommandState.INITIALIZE_SENDING_NEXT);
                break;

            case OP_STATE_RUN:
                switch(state) {
                    case INITIALIZE_SENDING_NEXT:

                        // Form the parameter string for the set config command (name and value).
                        String cmdParams = BoardDefaults.tinygSettings[idx].name + " "
                                + BoardDefaults.tinygSettings[idx].value;

                        // Call sub command with our parameters to set the configurable setting.
                        CommandManager.getInstance().callCommand(
                                DeviceCommand.COMMAND_SET_CONFIG, cmdParams, null);

                        // Advance state variable.
                        setState(CommandState.INITIALIZE_WAITING_FOR_RESPONSE);
                        break;

                    case INITIALIZE_WAITING_FOR_RESPONSE:
                        // Wait until the set config command we called earlier indicates that it
                        // has finished.
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_SET_CONFIG)) {

                            // The set config sub command returned successfully. We are done with
                            // this command. Increment index to move onto the next one.
                            idx++;

                            // If we've reached the end of our settings array, mark the command as
                            // complete as we are done.
                            if (idx >= BoardDefaults.tinygSettings.length) {
                                setState(CommandState.COMPLETE_STATE);
                                break;
                            }

                            // Otherwise, send us back to set the next config.
                            setState(CommandState.INITIALIZE_SENDING_NEXT);
                        }
                        break;
                }
                break;

            default:
                break;
        }
    }
}
